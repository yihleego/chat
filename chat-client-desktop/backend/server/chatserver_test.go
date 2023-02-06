package server

import (
	"google.golang.org/protobuf/encoding/prototext"
	"google.golang.org/protobuf/proto"
	"testing"
)

func TestSendMessage(t *testing.T) {
	res := make(chan string)
	SetOptions(&Options{
		Host:  "localhost",
		Port:  10000,
		Token: "0SB26uBeSKOAaobmDbUMIg",
		OnOpen: func() {
		},
		OnClose: func() {
		},
		OnError: func(err error) {
		},
		OnKickOut: func() {
		},
		OnAuthenticated: func() {
			err := Send(MessageSendNotify, &Message{Id: 1, Recipient: 2})
			if err != nil {
				t.Logf("%v", err)
			}
		},
		OnUnauthenticated: func() {
		},
		OnMessage: func(box *Box) {
			t.Logf("%v", prototext.Format(box))
			var msg Message
			err := proto.Unmarshal(box.Data.Value, &msg)
			if err != nil {
				t.Logf("Parse message failed: %v %v", box, err)
				return
			}
			res <- prototext.Format(&msg)
		},
	})
	err := Startup()
	if err != nil {
		t.Logf("%v", err)
	}
	t.Logf("%v", <-res)
}
