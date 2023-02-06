package server

import (
	"bufio"
	"errors"
	"fmt"
	"google.golang.org/protobuf/proto"
	"google.golang.org/protobuf/types/known/anypb"
	"io"
	"net"
	"sync"
	"time"
)

var (
	lock    = sync.Mutex{}
	running = false
	options *Options
	conn    *net.TCPConn
	ticker  *time.Ticker
)

type Options struct {
	Host              string
	Port              int
	Token             string
	OnOpen            func()
	OnClose           func()
	OnError           func(err error)
	OnKickOut         func()
	OnAuthenticated   func()
	OnUnauthenticated func()
	OnHeartbeat       func()
	OnMessage         func(box *Box)
}

func SetOptions(opts *Options) {
	options = opts
}

func Startup() error {
	if running {
		fmt.Println("Already startup")
		return nil
	}
	if lock.TryLock() {
		running = true
		err := run()
		if err != nil {
			fmt.Printf("Startup failed: %v", err)
		}
		lock.Unlock()
		return err
	}
	return nil
}

func Shutdown() error {
	if !running {
		fmt.Println("Already shutdown")
		return nil
	}
	if lock.TryLock() {
		running = false
		err := conn.Close()
		if err != nil {
			fmt.Printf("Shutdown failed: %v", err)
		}
		lock.Unlock()
		return err
	}
	return nil
}

func Send(code int32, data proto.Message) error {
	if data == nil {
		return send(code, nil)
	}
	buf, err := proto.Marshal(data)
	if err != nil {
		return err
	}
	return send(code, buf)
}

func send(code int32, data []byte) error {
	box := &Box{Code: code}
	if data != nil {
		box.Data = &anypb.Any{Value: data}
	}
	buf, err := proto.Marshal(box)
	if err != nil {
		return err
	}
	return write(buf)
}

func run() error {
	addr, err := net.ResolveTCPAddr("tcp", fmt.Sprintf("%s:%d", options.Host, options.Port))
	if err != nil {
		fmt.Printf("ResolveTCPAddr failed: %v", err)
		return err
	}
	conn, err = net.DialTCP("tcp", nil, addr)
	if err != nil {
		fmt.Printf("DialTCP failed: %v", err)
		return err
	}
	conn.SetNoDelay(true)
	conn.SetKeepAlive(true)
	err = handleOpen()
	if err != nil {
		return err
	}
	go read()
	return nil
}

func write(buf []byte) error {
	// computeRawVarint32Size computes size of protobuf varint32 after encoding.
	computeRawVarint32Size := func(v int) int {
		if (v & (0xFFFFFFFF << 7)) == 0 {
			return 1
		} else if (v & (0xFFFFFFFF << 14)) == 0 {
			return 2
		} else if (v & (0xFFFFFFFF << 21)) == 0 {
			return 3
		} else if (v & (0xFFFFFFFF << 28)) == 0 {
			return 4
		} else {
			return 5
		}
	}
	// writeRawVarint32 writes protobuf varint32 to byte array.
	writeRawVarint32 := func(v int, p []byte) {
		i := 0
		for {
			if (v & ^0x7F) == 0 {
				p[i] = byte(v)
				return
			} else {
				p[i] = byte((v & 0x7F) | 0x80)
				v >>= 7
			}
			i++
		}
	}
	bl := len(buf)
	hl := computeRawVarint32Size(bl)
	p := make([]byte, hl+bl)
	writeRawVarint32(bl, p)
	copy(p[hl:], buf)
	_, err := conn.Write(p)
	return err
}

func read() {
	r := bufio.NewReader(conn)
	readRawVarint32 := func() (int, error) {
		b, err := r.ReadByte()
		if err != nil {
			return 0, err
		}
		tmp := int(b)
		if tmp >= 0 {
			return tmp, nil
		}

		res := tmp & 127
		b, err = r.ReadByte()
		if err != nil {
			return 0, err
		}
		tmp = int(b)
		if tmp >= 0 {
			res |= tmp << 7
			return res, nil
		}

		res |= (tmp & 127) << 7
		b, err = r.ReadByte()
		if err != nil {
			return 0, err
		}
		tmp = int(b)
		if tmp >= 0 {
			res |= tmp << 14
			return res, nil
		}

		res |= (tmp & 127) << 14
		b, err = r.ReadByte()
		if err != nil {
			return 0, err
		}
		tmp = int(b)
		if tmp >= 0 {
			res |= tmp << 21
			return res, nil
		}

		res |= (tmp & 127) << 21
		b, err = r.ReadByte()
		if err != nil {
			return 0, err
		}
		tmp = int(b)
		if tmp >= 0 {
			res |= tmp << 28
			return res, nil
		}

		return 0, errors.New("malformed varint")
	}
	for {
		l, err := readRawVarint32()
		if err != nil {
			handleError(err)
			handleClose()
			return
		}
		b := make([]byte, l)
		_, err = io.ReadAtLeast(r, b, l)
		if err != nil {
			handleError(err)
			continue
		}
		handleMessage(b)
	}
}

func handleOpen() error {
	err := send(Authentication, []byte(options.Token))
	if err != nil {
		return err
	}
	options.OnOpen()
	return nil
}

func handleClose() {
	running = false
	conn.Close()
	if ticker != nil {
		ticker.Stop()
		ticker = nil
	}
	options.OnClose()
}

func handleError(err error) {
	options.OnError(err)
}

func handleMessage(data []byte) {
	var box Box
	err := proto.Unmarshal(data, &box)
	if err != nil {
		fmt.Printf("Parse box failed: %v\n%v\n", data, err)
		options.OnError(err)
		return
	}
	switch box.Code {
	case Heartbeat:
		options.OnHeartbeat()
	case KickedOut:
		options.OnKickOut()
	case Authenticated:
		options.OnAuthenticated()
		// Start heartbeat
		go func() {
			ticker = time.NewTicker(60 * time.Second)
			for range ticker.C {
				send(Heartbeat, nil)
			}
		}()
	case Unauthenticated:
		options.OnUnauthenticated()
	default:
		options.OnMessage(&box)
	}
}

// Code
const (
	Unknown         = 0
	Heartbeat       = 1
	KickedOut       = 2
	Authentication  = 3
	Authenticated   = 4
	Unauthenticated = 5

	MessageSendNotify        = 10 // sender    -> server
	MessageSendPush          = 11 // server    -> recipient
	MessageTakeNotify        = 12 // recipient -> server
	MessageTakePush          = 13 // server    -> sender
	MessageTakeOfflinePush   = 14 // server    -> sender
	MessageReadNotify        = 15 // recipient -> server
	MessageReadPush          = 16 // server    -> sender
	MessageReadBatchNotify   = 17 // recipient -> server
	MessageReadBatchPush     = 18 // server    -> sender
	MessageRevokeNotify      = 19 // sender    -> server
	MessageRevokePush        = 20 // server    -> recipient
	MessageRemoveNotify      = 21 // recipient -> server
	MessageRemovePush        = 22 // server    -> sender
	MessageRemoveOfflinePush = 23 // server    -> sender
	MessageSyncPush          = 24 // server    -> sender

	GroupMessageSendNotify        = 30 // sender    -> server
	GroupMessageSendPush          = 31 // server    -> recipient
	GroupMessageTakeNotify        = 32 // recipient -> server
	GroupMessageTakePush          = 33 // server    -> sender
	GroupMessageTakeOfflinePush   = 34 // server    -> sender
	GroupMessageReadNotify        = 35 // recipient -> server
	GroupMessageReadPush          = 36 // server    -> sender
	GroupMessageReadBatchNotify   = 37 // recipient -> server
	GroupMessageReadBatchPush     = 38 // server    -> sender
	GroupMessageRevokeNotify      = 39 // sender    -> server
	GroupMessageRevokePush        = 40 // server    -> recipient
	GroupMessageRemoveNotify      = 41 // recipient -> server
	GroupMessageRemovePush        = 42 // server    -> sender
	GroupMessageRemoveOfflinePush = 43 // server    -> sender
	GroupMessageSyncPush          = 44 // server    -> sender

	ContactRequestSendNotify      = 50 // sender    -> server
	ContactRequestSendPush        = 51 // server    -> recipient
	ContactRequestTakeNotify      = 52 // recipient -> server
	ContactRequestTakePush        = 53 // server    -> sender
	ContactRequestTakeOfflinePush = 54 // server    -> sender
	ContactEventNotify            = 55 // sender    -> server
	ContactEventPush              = 56 // server    -> recipient

	GroupEventNotify       = 60
	GroupEventPush         = 61
	GroupMemberEventNotify = 62
	GroupMemberEventPush   = 63
)
