package chat

import (
	"context"
	"errors"
	"github.com/wailsapp/wails/v2/pkg/runtime"
	"github.com/yihleego/chat/backend/api"
	"github.com/yihleego/chat/backend/repo"
	"github.com/yihleego/chat/backend/server"
	"google.golang.org/protobuf/encoding/prototext"
	"google.golang.org/protobuf/proto"
	"time"
)

// Chat struct
type Chat struct {
	ctx    context.Context
	api    *api.Options
	server *server.Options
	auth   *Authentication
	user   *User
	dev    bool
}

func (c *Chat) Dev() {
	c.dev = true
}

func (c *Chat) Startup(ctx context.Context) {
	c.ctx = ctx
	c.Debugf("Startup (mode=%s)", c.dev)
	// Options
	uri := config.ApiAddrProd
	if c.dev {
		uri = config.ApiAddrDev
	}
	c.api = &api.Options{
		Uri:   uri,
		Token: "",
	}
	c.server = &server.Options{
		Host:              "",
		Port:              -1,
		Token:             "",
		OnOpen:            c.onOpen,
		OnClose:           c.onClose,
		OnError:           c.onError,
		OnKickOut:         c.onKickOut,
		OnAuthenticated:   c.onAuthenticated,
		OnUnauthenticated: c.onUnauthenticated,
		OnHeartbeat:       c.onHeartbeat,
		OnMessage:         c.onMessage,
	}
	api.SetOptions(c.api)
	server.SetOptions(c.server)
	// Session
	s, _ := repo.FindSession()
	if s != nil {
		c.auth = &Authentication{
			AccessToken: s.AccessToken,
			TokenType:   s.TokenType,
			ExpiresIn:   s.ExpiresIn,
			ExpiredTime: s.ExpiredTime,
			RefreshTime: s.RefreshTime,
		}
		c.user = &User{
			UserId:   s.UserID,
			Username: s.Username,
			Nickname: s.Nickname,
			Avatar:   s.Avatar,
		}
	}
}

func (c *Chat) Shutdown() {
	c.Debug("Shutdown")
	c.Disconnect()
}

func (c *Chat) Connect() error {
	if c.auth == nil {
		return errors.New("请重新登录")
	}
	instance, err := api.GetInstance()
	if err != nil {
		runtime.MessageDialog(c.ctx, runtime.MessageDialogOptions{
			Type:          runtime.ErrorDialog,
			Title:         "Error",
			Message:       "服务器维护中",
			DefaultButton: "OK",
		})
		if err != nil {
			return err
		}
		return errors.New("服务器维护中")
	}
	c.server.Host = instance.Host
	c.server.Port = instance.RawPort
	c.server.Token = c.auth.AccessToken
	err = server.Startup()
	if err != nil {
		runtime.MessageDialog(c.ctx, runtime.MessageDialogOptions{
			Type:          runtime.ErrorDialog,
			Title:         "Error",
			Message:       "连接至服务器失败，请检查网络环境",
			DefaultButton: "OK",
		})
		return errors.New("连接至服务器失败，请检查网络环境")
	}
	return nil
}

func (c *Chat) Disconnect() error {
	err := server.Shutdown()
	if err != nil {
		c.Errorf("Disconnect failed: %v", err)
		return err
	}
	return nil
}

func (c *Chat) GetUser() *User {
	return c.user
}

func (c *Chat) SignUp(param SignUpInput) (*User, error) {
	res, err := api.SignUp(api.SignUpParam{
		Username:   param.Username,
		Password:   param.Password,
		Nickname:   param.Nickname,
		DeviceId:   config.DeviceId,
		DeviceType: config.DeviceType,
		ClientType: config.ClientType,
	})
	if err != nil {
		return nil, err
	}
	now := time.Now().UnixMilli()
	c.auth = &Authentication{
		AccessToken: res.AccessToken,
		TokenType:   res.TokenType,
		ExpiresIn:   res.ExpiresIn,
		LoginTime:   now,
		ExpiredTime: now + res.ExpiresIn*1000,
		RefreshTime: now,
	}
	return c.GetSession()
}

func (c *Chat) SignIn(param SignInInput) (*User, error) {
	res, err := api.SignIn(api.SignInParam{
		Username:   param.Username,
		Password:   param.Password,
		DeviceId:   config.DeviceId,
		DeviceType: config.DeviceType,
		ClientType: config.ClientType,
	})
	if err != nil {
		return nil, err
	}
	now := time.Now().UnixMilli()
	c.auth = &Authentication{
		AccessToken: res.AccessToken,
		TokenType:   res.TokenType,
		ExpiresIn:   res.ExpiresIn,
		LoginTime:   now,
		ExpiredTime: now + res.ExpiresIn*1000,
		RefreshTime: now,
	}
	return c.GetSession()
}

func (c *Chat) SignOut() error {
	c.auth = nil
	c.user = nil
	defer repo.DeleteSession()
	err := api.SignOut()
	if err != nil {
		return err
	}
	return nil
}

func (c *Chat) GetSession() (*User, error) {
	now := time.Now().UnixMilli()
	if now > c.auth.ExpiredTime {
		// Timeout
		return nil, errors.New("请重新登录")
	}
	c.api.Token = c.auth.AccessToken
	res, err := api.GetSession()
	if err != nil {
		return nil, errors.New("请重新登录")
	}
	c.user = &User{
		UserId:   res.UserId,
		Username: res.Username,
		Nickname: res.Nickname,
		Avatar:   res.Avatar,
	}
	repo.SaveSession(c.user.UserId, c.user.Username, c.user.Nickname, c.user.Avatar,
		c.auth.AccessToken, c.auth.TokenType, c.auth.ExpiresIn, c.auth.LoginTime, c.auth.ExpiredTime, c.auth.RefreshTime)
	return c.user, nil
}

func (c *Chat) SendMessage(param MessageSendInput) (*MessageOutput, error) {
	m, err := api.CreateMessage(api.MessageCreateParam{
		Recipient: param.Recipient,
		Type:      param.Type,
		Content:   param.Content,
	})
	if err != nil {
		c.Errorf("Create message error: %+v\n%+v", param, err)
		return nil, err
	}
	c.Debugf("Create message success: %+v", m)
	err = repo.SaveMessage(m.Id, c.user.UserId, param.Recipient,
		param.Type, param.Content, m.SentTime, false, false, false)
	if err != nil {
		c.Errorf("Create local message error: %+v\n%+v", m, err)
		return nil, err
	}
	c.Debugf("Create local message success: %+v", m.Id)
	err = server.Send(server.MessageSendNotify, &server.Message{Id: m.Id, Recipient: param.Recipient})
	if err != nil {
		c.Errorf("Send message failed: %+v\n%+v", m, err)
	}
	c.Debugf("Send message success: %+v", m.Id)
	return &MessageOutput{
		Id:        m.Id,
		Sender:    c.user.UserId,
		Recipient: param.Recipient,
		Type:      param.Type,
		Content:   param.Content,
		Taken:     false,
		Seen:      false,
		Revoked:   false,
		SentTime:  m.SentTime,
	}, nil
}

func (c *Chat) GetMessage(id int64) (*MessageOutput, error) {
	m, err := repo.GetMessage(id)
	if err != nil {
		c.Errorf("Get message error: %+v", err)
		return nil, err
	}
	return &MessageOutput{
		Id:        m.ID,
		Sender:    m.Sender,
		Recipient: m.Recipient,
		Type:      m.Type,
		Content:   m.Content,
		Taken:     m.TakenTime != nil,
		Seen:      m.SeenTime != nil,
		Revoked:   m.RevokedTime != nil,
		SentTime:  m.SentTime,
	}, nil
}

func (c *Chat) ReadMessages(ids []int64, recipient int64) error {
	err := api.ReadMessages(ids)
	if err != nil {
		c.Errorf("Read message error: %+v", err)
		return err
	}
	err = repo.UpdateMessageSeen(ids, time.Now().UnixMilli())
	if err != nil {
		c.Errorf("Read local message error: %+v", err)
		return err
	}
	err = server.Send(server.MessageReadNotify, &server.BulkMessage{Id: ids, Recipient: recipient})
	if err != nil {
		c.Errorf("Send message failed: %+v\n%+v", ids, err)
	}
	return nil
}

func (c *Chat) RevokeMessage(id int64) error {
	err := api.RevokeMessage(id)
	if err != nil {
		c.Errorf("Revoke message error: %+v", err)
		return err
	}
	err = repo.UpdateMessageRevoked([]int64{id}, time.Now().UnixMilli())
	if err != nil {
		c.Errorf("Revoke local message error: %+v", err)
		return err
	}
	return nil
}

func (c *Chat) SetLoginWindow() {
	runtime.WindowSetMinSize(c.ctx, config.LoginWindowWidth, config.LoginWindowHeight)
	runtime.WindowSetMaxSize(c.ctx, config.LoginWindowWidth, config.LoginWindowHeight)
	runtime.WindowSetSize(c.ctx, config.LoginWindowWidth, config.LoginWindowHeight)
	runtime.WindowCenter(c.ctx)
}

func (c *Chat) SetMainWindow() {
	runtime.WindowSetMinSize(c.ctx, 700, 500)
	runtime.WindowSetMaxSize(c.ctx, 0, 0)
	runtime.WindowSetSize(c.ctx, config.MainWindowWidth, config.MainWindowHeight)
	runtime.WindowCenter(c.ctx)
}

func (c *Chat) pullMessages() {
	c.Debug("pullMessages")
	sync := func(type1 int) {
		lastId := int64(0)
		for {
			ms, err := api.ListMessages(lastId, type1)
			if err != nil {
				c.Errorf("Query messages failed, id: %d, type: %d, err: %+v", lastId, type1, err)
				break
			}
			if len(ms) == 0 {
				c.Debugf("Query messages ended, id: %d, type: %d, err: %+v", lastId, type1, err)
				break
			}
			for _, m := range ms {
				err = repo.SaveMessage(m.Id, m.Sender, m.Recipient,
					m.Type, m.Content, m.SentTime, m.Taken, m.Seen, m.Revoked)
				if err != nil {
					c.Errorf("Save message failed: %+v, err: %+v", m, err)
					break
				}
				lastId = m.Id
			}
		}
	}
	sync(api.Sender)
	sync(api.Recipient)
}

func (c *Chat) pullGroupMessages() {
	c.Debug("pullGroupMessages")
	sync := func(type1 int, recipient int64) {
		lastId := int64(0)
		for {
			ms, err := api.ListGroupMessages(lastId, type1)
			if err != nil {
				c.Errorf("Query group messages failed, id: %d, type: %d, err: %+v", lastId, type1, err)
				break
			}
			if len(ms) == 0 {
				c.Debugf("Query group messages ended, id: %d, type: %d, err: %+v", lastId, type1, err)
				break
			}
			for _, m := range ms {
				err = repo.SaveGroupMessage(m.Id, m.GroupId, m.Sender, recipient,
					m.Type, m.Content, m.SentTime, m.Taken, m.Seen, m.Revoked)
				if err != nil {
					c.Errorf("Save group message failed: %+v, err: %+v", m, err)
					break
				}
				lastId = m.Id
			}
		}
	}
	sync(api.Sender, 0)
	sync(api.Recipient, c.user.UserId)
}

func (c *Chat) onOpen() {
	c.Debug("onOpen")
}

func (c *Chat) onClose() {
	c.Debug("onClose")
	runtime.EventsEmit(c.ctx, EventClose)
}

func (c *Chat) onError(err error) {
	c.Errorf("Error %+v", err)
}

func (c *Chat) onKickOut() {
	c.Debug("onKickOut")
	runtime.EventsEmit(c.ctx, EventKickOut)
	runtime.MessageDialog(c.ctx, runtime.MessageDialogOptions{
		Type:          runtime.ErrorDialog,
		Title:         "Kick Out",
		Message:       "您已在别处登录",
		DefaultButton: "OK",
	})
	runtime.Quit(c.ctx)
}

func (c *Chat) onAuthenticated() {
	c.Debug("onAuthenticated")
	c.pullMessages()
	c.pullGroupMessages()
	runtime.EventsEmit(c.ctx, EventReady)
}

func (c *Chat) onUnauthenticated() {
	runtime.MessageDialog(c.ctx, runtime.MessageDialogOptions{
		Type:          runtime.ErrorDialog,
		Title:         "Unauthenticated",
		Message:       "请重新登录",
		DefaultButton: "OK",
	})
	runtime.Quit(c.ctx)
}

func (c *Chat) onHeartbeat() {
	c.Debug("onHeartbeat")
}

func (c *Chat) onMessage(box *server.Box) {
	switch box.Code {
	case server.MessageSendPush:
		c.takeMessage(box)
	case server.MessageTakePush:
		c.setMessageAsTaken(box)
	case server.MessageTakeOfflinePush:
		c.setMessageAsTakenOffline(box)
	case server.MessageReadPush:
		c.setMessageAsSeen(box)
	case server.MessageReadBatchPush:
		c.setMessageAsSeenInBatch(box)
	case server.MessageRevokePush:
		c.removeMessage(box)
	case server.MessageRemovePush:
		c.setMessageAsRevoked(box)
	case server.MessageRemoveOfflinePush:
		c.setMessageAsRevokedOffline(box)
	case server.MessageSyncPush:
		c.syncMessage(box)
	case server.GroupMessageSendPush:
		c.Debugf("GroupMessageSendPush: %s", prototext.Format(box))
	case server.GroupMessageTakePush:
		c.Debugf("GroupMessageTakenPush: %s", prototext.Format(box))
	case server.GroupMessageTakeOfflinePush:
		c.Debugf("GroupMessageTakenOfflinePush: %s", prototext.Format(box))
	case server.GroupMessageReadPush:
		c.Debugf("GroupMessageSeenPush: %s", prototext.Format(box))
	case server.GroupMessageReadBatchPush:
		c.Debugf("GroupMessageSeenBatchPush: %s", prototext.Format(box))
	case server.GroupMessageRevokePush:
		c.Debugf("GroupMessageRevokePush: %s", prototext.Format(box))
	case server.GroupMessageRemovePush:
		c.Debugf("GroupMessageRevokedPush: %s", prototext.Format(box))
	case server.GroupMessageRemoveOfflinePush:
		c.Debugf("GroupMessageRevokedOfflinePush: %s", prototext.Format(box))
	case server.GroupMessageSyncPush:
		c.Debugf("GroupMessageSyncPush: %s", prototext.Format(box))
	case server.ContactRequestSendPush:
		c.Debugf("ContactRequestSendPush: %s", prototext.Format(box))
	case server.ContactRequestTakePush:
		c.Debugf("ContactRequestTakenPush: %s", prototext.Format(box))
	case server.ContactRequestTakeOfflinePush:
		c.Debugf("ContactRequestTakenOfflinePush: %s", prototext.Format(box))
	case server.ContactEventPush:
		c.Debugf("ContactEventPush: %s", prototext.Format(box))
	case server.GroupEventPush:
		c.Debugf("GroupEventPush: %s", prototext.Format(box))
	case server.GroupMemberEventPush:
		c.Debugf("GroupMemberEventPush: %s", prototext.Format(box))
	default:
		c.Errorf("Invalid message: %s", prototext.Format(box))
	}
}

func (c *Chat) takeMessage(box *server.Box) {
	c.Debugf("MessageSendPush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	m, err := api.GetMessage(msg.Id)
	if err != nil {
		c.Errorf("Get message failed: %+v, err: %+v", msg.Id, err)
		return
	}
	err = repo.SaveMessage(m.Id, m.Sender, m.Recipient,
		m.Type, m.Content, m.SentTime, m.Taken, m.Seen, m.Revoked)
	if err != nil {
		c.Errorf("Save message failed: %+v, err: %+v", m, err)
		return
	}
	err = server.Send(server.MessageTakeNotify, &server.Message{Id: m.Id, Sender: m.Sender})
	if err != nil {
		c.Errorf("Send message failed: %+v, err: %+v", m, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsTaken(box *server.Box) {
	c.Debugf("MessageTakenPush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	err = repo.UpdateMessageTaken([]int64{msg.Id}, time.Now().UnixMilli())
	if err != nil {
		c.Errorf("Take message failed: %+v, err: %+v", msg.Id, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsTakenOffline(box *server.Box) {
	c.Debugf("MessageTakenOfflinePush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsSeen(box *server.Box) {
	c.Debugf("MessageSeenPush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	err = repo.UpdateMessageSeen([]int64{msg.Id}, time.Now().UnixMilli())
	if err != nil {
		c.Errorf("Read message failed: %+v, err: %+v", msg.Id, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsSeenInBatch(box *server.Box) {
	c.Debugf("MessageSeenBatchPush: %s", prototext.Format(box))
	var msg server.BulkMessage
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	err = repo.UpdateMessageSeen(msg.Id, time.Now().UnixMilli())
	if err != nil {
		c.Errorf("Read message failed: %+v, err: %+v", msg.Id, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) removeMessage(box *server.Box) {
	c.Debugf("MessageRevokePush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	var id, sender int64
	lm, _ := repo.GetMessage(msg.Id)
	if lm != nil {
		err = repo.UpdateMessageRevoked([]int64{msg.Id}, time.Now().UnixMilli())
		if err != nil {
			c.Errorf("Revoke message failed: %+v, err: %+v", msg.Id, err)
			return
		}
		id, sender = lm.ID, lm.Sender
	} else {
		rm, err := api.GetMessage(msg.Id)
		if err != nil {
			c.Errorf("Get message failed: %+v, err: %+v", msg.Id, err)
			return
		}
		err = repo.SaveMessage(rm.Id, rm.Sender, rm.Recipient,
			rm.Type, rm.Content, rm.SentTime, rm.Taken, rm.Seen, rm.Revoked)
		if err != nil {
			c.Errorf("Save message failed: %+v, err: %+v", rm, err)
			return
		}
		id, sender = rm.Id, rm.Sender
	}
	err = server.Send(server.MessageRemoveNotify, &server.Message{Id: id, Sender: sender})
	if err != nil {
		c.Errorf("Send message failed: %+v, err: %+v", lm, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsRevoked(box *server.Box) {
	c.Debugf("MessageRevokedPush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) setMessageAsRevokedOffline(box *server.Box) {
	c.Debugf("MessageRevokedOfflinePush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) syncMessage(box *server.Box) {
	c.Debugf("MessageSyncPush: %s", prototext.Format(box))
	var msg server.Message
	err := proto.Unmarshal(box.Data.Value, &msg)
	if err != nil {
		c.Errorf("Parse message failed: %+v", err)
		return
	}
	m, err := api.GetMessage(msg.Id)
	if err != nil {
		c.Errorf("Get message failed: %+v, err: %+v", msg.Id, err)
		return
	}
	err = repo.SaveMessage(m.Id, m.Sender, m.Recipient,
		m.Type, m.Content, m.SentTime, m.Taken, m.Seen, m.Revoked)
	if err != nil {
		c.Errorf("Save message failed: %+v, err: %+v", m, err)
		return
	}
	runtime.EventsEmit(c.ctx, EventMessage, box.Code, msg.Id)
}

func (c *Chat) Debug(s string) {
	runtime.LogDebug(c.ctx, s)
}

func (c *Chat) Debugf(f string, a ...interface{}) {
	runtime.LogDebugf(c.ctx, f, a)
}

func (c *Chat) Warn(s string) {
	runtime.LogWarning(c.ctx, s)
}

func (c *Chat) Warnf(f string, a ...interface{}) {
	runtime.LogWarningf(c.ctx, f, a)
}

func (c *Chat) Error(s string) {
	runtime.LogError(c.ctx, s)
}

func (c *Chat) Errorf(f string, a ...interface{}) {
	runtime.LogErrorf(c.ctx, f, a)
}

// Authentication struct
type Authentication struct {
	AccessToken string `json:"accessToken"`
	TokenType   string `json:"tokenType"`
	ExpiresIn   int64  `json:"expiresIn"`
	LoginTime   int64  `json:"loginTime"`
	ExpiredTime int64  `json:"expiredTime"`
	RefreshTime int64  `json:"refreshTime"`
}

// User struct
type User struct {
	UserId   int64  `json:"userId"`
	Username string `json:"username"`
	Nickname string `json:"nickname"`
	Avatar   string `json:"avatar"`
}

type SignUpInput struct {
	Username string `json:"username"`
	Password string `json:"password"`
	Nickname string `json:"nickname"`
}

type SignInInput struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type MessageSendInput struct {
	Recipient int64    `json:"recipient"`
	Type      int32    `json:"type"`
	Content   string   `json:"content"`
	Files     []string `json:"files"`
}

type MessageOutput struct {
	Id        int64  `json:"id"`
	Sender    int64  `json:"sender"`
	Recipient int64  `json:"recipient"`
	Type      int32  `json:"type"`
	Content   string `json:"content"`
	Taken     bool   `json:"taken"`
	Seen      bool   `json:"seen"`
	Revoked   bool   `json:"revoked"`
	SentTime  int64  `json:"sentTime"`
}

// event
const (
	EventReady        string = "ready"
	EventClose        string = "close"
	EventKickOut      string = "kickout"
	EventContact      string = "contact"
	EventGroup        string = "group"
	EventMessage      string = "message"
	EventGroupMessage string = "groupmessage"
)
