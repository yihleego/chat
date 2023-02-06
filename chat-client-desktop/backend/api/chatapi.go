package api

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"google.golang.org/protobuf/proto"
	"io"
	"mime/multipart"
	"net/http"
	"net/url"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

var (
	opts *Options
)

// SetOptions sets the API uri and the access token.
func SetOptions(o *Options) {
	opts = o
}

// SignUp creates an account with the given username and password.
func SignUp(p SignUpParam) (r AccessTokenResult, err error) {
	err = NewAPI(opts).
		Post().
		Path("/accounts").
		JsonBody(p).
		Exec().
		Msg(Msg{
			http.StatusBadRequest: "用户名或密码格式不正确",
			http.StatusConflict:   "用户已存在"}).
		ToJson(&r)
	return
}

// SignIn creates a session with the given username and password.
func SignIn(p SignInParam) (r AccessTokenResult, err error) {
	err = NewAPI(opts).
		Post().
		Path("/sessions").
		JsonBody(p).
		Exec().
		Msg(Msg{
			http.StatusBadRequest:   "用户名或密码格式不正确",
			http.StatusUnauthorized: "用户名或密码错误"}).
		ToJson(&r)
	return
}

// SignOut removes the session with the given access token.
func SignOut() error {
	return NewAPI(opts).
		Delete().
		Path("/sessions").
		Exec().
		Msg(Msg{http.StatusUnauthorized: "已退出登录"}).
		End()
}

// GetSession returns the session with the given access token.
func GetSession() (r SessionResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/sessions").
		Exec().
		Msg(Msg{http.StatusUnauthorized: "请重新登录"}).
		ToJson(&r)
	return
}

// GetUser returns the user with the given username.
func GetUser(username string) (r UserResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/users/" + username).
		Exec().
		Msg(Msg{http.StatusNotFound: "用户不存在"}).
		ToJson(&r)
	return
}

// UploadFile uploads files.
func UploadFile(files []*os.File) (r []FileResult, err error) {
	b := bytes.Buffer{}
	w := multipart.NewWriter(&b)
	for _, file := range files {
		f, err := w.CreateFormFile("files", filepath.Base(file.Name()))
		if err != nil {
			return nil, err
		}
		_, err = io.Copy(f, file)
		if err != nil {
			return nil, err
		}
	}
	ct := w.FormDataContentType()
	err = w.Close()
	if err != nil {
		return
	}
	err = NewAPI(opts).
		Post().
		Path("/files").
		ContentType(ct).
		Body(&b).
		Exec().
		Msg(Msg{http.StatusInternalServerError: "上传文件失败"}).
		ToJson(&r)
	return
}

// DownloadFile downloads the file with the given ID.
func DownloadFile(id string, path string) (file *os.File, err error) {
	file, err = os.OpenFile(filepath.Join(path, id), os.O_RDWR|os.O_CREATE, 0766)
	defer file.Close()
	if err != nil {
		return nil, err
	}
	err = NewAPI(opts).
		Get().
		Path("/files/" + id).
		Exec().
		Msg(Msg{http.StatusInternalServerError: "下载文件失败"}).
		Write(file)
	return
}

// GetInstance returns an available instance.
func GetInstance() (r InstanceResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/instances").
		Exec().
		Msg(Msg{http.StatusNotFound: "服务不可用"}).
		ToJson(&r)
	return
}

// BlockContact blocks the contact with the given recipient.
func BlockContact(recipient int64) error {
	return NewAPI(opts).
		Patch().
		Pathf("/contacts/%d", recipient).
		Exec().
		Msg(Msg{http.StatusNotFound: "联系人不存在"}).
		End()
}

// RemoveContact removes the contact with the given recipient.
func RemoveContact(recipient int64) error {
	return NewAPI(opts).
		Delete().
		Pathf("/contacts/%d", recipient).
		Exec().
		Msg(Msg{http.StatusNotFound: "联系人不存在"}).
		End()
}

// GetContact returns the contact with the given recipient.
func GetContact(recipient int64) (r ContactResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/contacts/%d", recipient).
		Exec().
		Msg(Msg{http.StatusNotFound: "联系人不存在"}).
		ToJson(&r)
	return
}

// ListContacts returns all contacts of the current user.
func ListContacts(lastTime int64) (r []ContactResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/contacts").
		Params(Params{"lastTime": ltoa(lastTime)}).
		Exec().
		ToJson(&r)
	return
}

// CreateContactRequest creates a contact request.
func CreateContactRequest(p ContactRequestCreateParam) error {
	return NewAPI(opts).
		Post().
		Path("/contacts/requests").
		JsonBody(p).
		Exec().
		Msg(Msg{
			http.StatusNotAcceptable: "好友达到上限",
		}).
		End()
}

// AcceptContactRequest accepts the contact request with the given ID.
func AcceptContactRequest(id int64) error {
	return NewAPI(opts).
		Patch().
		Pathf("/contacts/requests/%d", id).
		Params(Params{"action": "accept"}).
		Exec().
		Msg(Msg{http.StatusNotFound: "好友请求不存在"}).
		End()
}

// RejectContactRequest rejects the contact request with the given ID.
func RejectContactRequest(id int64) error {
	return NewAPI(opts).
		Patch().
		Pathf("/contacts/requests/%d", id).
		Params(Params{"action": "reject"}).
		Exec().
		Msg(Msg{http.StatusNotFound: "好友请求不存在"}).
		End()
}

// GetContactRequest returns the contact request with the given ID.
func GetContactRequest(id int64) (r ContactRequestResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/contacts/requests/%d", id).
		Exec().
		Msg(Msg{http.StatusNotFound: "好友请求不存在"}).
		ToJson(&r)
	return
}

// ListContactRequests returns all contact requests of the current user.
func ListContactRequests(lastTime int64) (r []ContactRequestResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/contacts/requests").
		Params(Params{"lastTime": ltoa(lastTime)}).
		Exec().
		Msg(Msg{http.StatusNotFound: "好友请求不存在"}).
		ToJson(&r)
	return
}

// CreateGroup creates a group.
func CreateGroup(p GroupCreateParam) error {
	return NewAPI(opts).
		Post().
		Path("/groups").
		JsonBody(p).
		Exec().
		Msg(Msg{http.StatusNotAcceptable: "群聊达到上限"}).
		End()
}

// DeleteGroup deletes the group with the given ID.
func DeleteGroup(id int64) error {
	return NewAPI(opts).
		Post().
		Pathf("/groups/%d", id).
		Exec().
		Msg(Msg{
			http.StatusNotFound:  "群聊不存在",
			http.StatusForbidden: "群聊不存在"}).
		End()
}

// GetGroup returns the group with the given ID.
func GetGroup(id int64) (r GroupResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/groups/%d", id).
		Exec().
		Msg(Msg{
			http.StatusNotFound:  "群聊不存在",
			http.StatusForbidden: "群聊不存在"}).
		ToJson(&r)
	return
}

// ListGroups returns all groups that the current user has joined.
func ListGroups(lastTime int64) (r []GroupResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/groups").
		Params(Params{"lastTime": ltoa(lastTime)}).
		Exec().
		ToJson(&r)
	return
}

// CreateMember invites the user to join the group.
func CreateMember(p GroupMemberCreateParam) error {
	return NewAPI(opts).
		Post().
		Pathf("/groups/%d", (p.GroupId)).
		JsonBody(p).
		Exec().
		Msg(Msg{
			http.StatusNotFound:      "群聊不存在",
			http.StatusForbidden:     "群聊不存在",
			http.StatusNotAcceptable: "群成员达到上限"}).
		End()
}

// LeaveGroup leaves the group.
func LeaveGroup(groupId int64) error {
	return NewAPI(opts).
		Delete().
		Pathf("/groups/%d/members", groupId).
		Exec().
		Msg(Msg{
			http.StatusNotFound:  "群聊不存在",
			http.StatusForbidden: "不允许群主退出"}).
		End()
}

// RemoveMember removes the member from the group.
func RemoveMember(groupId int64, userId int64) error {
	return NewAPI(opts).
		Delete().
		Pathf("/groups/%d/members/%d", groupId, userId).
		Exec().
		Msg(Msg{
			http.StatusNotFound:  "群聊不存在",
			http.StatusForbidden: "非群成员"}).
		End()
}

// GetMember returns the member of the group.
func GetMember(groupId int64, userId int64) (r GroupMemberResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/groups/%d/members/%d", groupId, userId).
		Exec().
		Msg(Msg{
			http.StatusNotFound:  "群聊不存在",
			http.StatusForbidden: "非群成员"}).
		ToJson(&r)
	return
}

// ListMembers returns all members of the group.
func ListMembers(groupId int64, lastTime int64) (r []GroupMemberResult, err error) {
	err = NewAPI(opts).
		Get().
		Path(fmt.Sprintf("/groups/%d/members", groupId)).
		Params(Params{"lastTime": ltoa(lastTime)}).
		Exec().
		Msg(Msg{http.StatusForbidden: "无权限"}).
		ToJson(&r)
	return
}

// CreateMessage creates a message.
func CreateMessage(p MessageCreateParam) (r MessageResult, err error) {
	err = NewAPI(opts).
		Post().
		Path("/messages").
		JsonBody(p).
		Exec().
		Msg(Msg{http.StatusForbidden: "非好友"}).
		ToJson(&r)
	return
}

// TakeMessages takes the messages with the given IDs.
func TakeMessages(ids []int64) error {
	return NewAPI(opts).
		Patch().
		Path("/messages/" + join(ids)).
		Params(Params{"action": "take"}).
		Exec().
		End()
}

// ReadMessages reads the messages with the given IDs.
func ReadMessages(ids []int64) error {
	return NewAPI(opts).
		Patch().
		Path("/messages/" + join(ids)).
		Params(Params{"action": "read"}).
		Exec().
		End()
}

// RevokeMessage revokes the message with the given ID.
func RevokeMessage(id int64) error {
	return NewAPI(opts).
		Delete().
		Pathf("/messages/%d", id).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "消息无法撤回",
			http.StatusNotFound:  "消息不存在"}).
		End()
}

// GetMessage returns the message with the given ID.
func GetMessage(id int64) (r MessageResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/messages/%d", id).
		Exec().
		Msg(Msg{http.StatusNotFound: "消息不存在"}).
		ToJson(&r)
	return
}

// ListMessages returns the messages on the client of the current user.
// The messages may contain some that have already been fetched.
func ListMessages(id int64, type1 int) (r []MessageResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/messages").
		Params(Params{"id": ltoa(id), "type": strconv.Itoa(type1)}).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "客户端不支持",
			http.StatusNotFound:  "消息不存在"}).
		ToJson(&r)
	return
}

func GetMessagePB(id int64) (MessageResult, error) {
	m := Message{}
	err := NewAPI(opts).
		Get().
		Pathf("/messages/%d", id).
		Proto().
		Exec().
		Msg(Msg{http.StatusNotFound: "消息不存在"}).
		ToProto(&m)
	r := convertMessage(&m)
	return r, err
}

func ListMessagesPB(id int64, type1 int) ([]MessageResult, error) {
	m := Messages{}
	err := NewAPI(opts).
		Get().
		Path("/messages").
		Proto().
		Params(Params{"id": ltoa(id), "type": strconv.Itoa(type1)}).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "客户端不支持",
			http.StatusNotFound:  "消息不存在"}).
		ToProto(&m)
	r := convertMessages(&m)
	return r, err
}

// CreateGroupMessage creates a message.
func CreateGroupMessage(p GroupMessageCreateParam) (r GroupMessageResult, err error) {
	err = NewAPI(opts).
		Post().
		Path("/messages").
		Params(Params{"target": "group"}).
		JsonBody(p).
		Exec().
		Msg(Msg{http.StatusForbidden: "非群成员"}).
		ToJson(&r)
	return
}

// TakeGroupMessages takes the messages with the given IDs.
func TakeGroupMessages(ids []int64) error {
	return NewAPI(opts).
		Patch().
		Path("/messages/" + join(ids)).
		Params(Params{"target": "group", "action": "take"}).
		Exec().
		End()
}

// ReadGroupMessages reads the messages with the given IDs.
func ReadGroupMessages(ids []int64) error {
	return NewAPI(opts).
		Patch().
		Path("/messages/" + join(ids)).
		Params(Params{"target": "group", "action": "read"}).
		Exec().
		End()
}

// RevokeGroupMessage revokes the message with the given ID.
func RevokeGroupMessage(id int64) error {
	return NewAPI(opts).
		Delete().
		Pathf("/messages/%d", id).
		Params(Params{"target": "group"}).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "消息无法撤回",
			http.StatusNotFound:  "消息不存在"}).
		End()
}

// GetGroupMessage returns the message with the given ID.
func GetGroupMessage(id int64) (r GroupMessageResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/messages/%d", id).
		Params(Params{"target": "group"}).
		Exec().
		Msg(Msg{http.StatusNotFound: "消息不存在"}).
		ToJson(&r)
	return
}

// GetGroupMessageState returns the message state with the given ID.
func GetGroupMessageState(id int64, lastTime int64) (r GroupMessageStateResult, err error) {
	err = NewAPI(opts).
		Get().
		Pathf("/messages/%d/states", id).
		Params(Params{"target": "group", "lastTime": ltoa(lastTime)}).
		Exec().
		Msg(Msg{http.StatusNotFound: "消息不存在"}).
		ToJson(&r)
	return
}

// ListGroupMessages returns the messages on the client of the current user.
// The messages may contain some that have already been fetched.
func ListGroupMessages(id int64, type1 int) (r []GroupMessageResult, err error) {
	err = NewAPI(opts).
		Get().
		Path("/messages").
		Params(Params{"target": "group", "id": ltoa(id), "type": strconv.Itoa(type1)}).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "客户端不支持",
			http.StatusNotFound:  "消息不存在"}).
		ToJson(&r)
	return
}

func GetGroupMessagePB(id int64) (GroupMessageResult, error) {
	m := GroupMessage{}
	err := NewAPI(opts).
		Get().
		Pathf("/messages/%d", id).
		Params(Params{"target": "group"}).
		Proto().
		Exec().
		Msg(Msg{http.StatusNotFound: "消息不存在"}).
		ToProto(&m)
	r := convertGroupMessage(&m)
	return r, err
}

func ListGroupMessagesPB(id int64, type1 int) ([]GroupMessageResult, error) {
	m := GroupMessages{}
	err := NewAPI(opts).
		Get().
		Path("/messages").
		Proto().
		Params(Params{"target": "group", "id": ltoa(id), "type": strconv.Itoa(type1)}).
		Exec().
		Msg(Msg{
			http.StatusForbidden: "客户端不支持",
			http.StatusNotFound:  "消息不存在"}).
		ToProto(&m)
	r := convertGroupMessages(&m)
	return r, err
}

func convertMessage(m *Message) MessageResult {
	return MessageResult{
		Id:        m.Id,
		Sender:    m.Sender,
		Recipient: m.Recipient,
		Type:      m.Type,
		Content:   m.Content,
		Taken:     m.Taken,
		Seen:      m.Seen,
		Revoked:   m.Revoked,
		SentTime:  m.SentTime,
	}
}

func convertMessages(ms *Messages) []MessageResult {
	r := make([]MessageResult, len(ms.Message))
	for i := 0; i < len(ms.Message); i++ {
		r[i] = convertMessage(ms.Message[i])
	}
	return r
}

func convertGroupMessage(m *GroupMessage) GroupMessageResult {
	return GroupMessageResult{
		Id:       m.Id,
		GroupId:  m.GroupId,
		Sender:   m.Sender,
		Type:     m.Type,
		Content:  m.Content,
		Taken:    m.Taken,
		Seen:     m.Seen,
		Revoked:  m.Revoked,
		SentTime: m.SentTime,
	}
}

func convertGroupMessages(ms *GroupMessages) []GroupMessageResult {
	r := make([]GroupMessageResult, len(ms.Message))
	for i := 0; i < len(ms.Message); i++ {
		r[i] = convertGroupMessage(ms.Message[i])
	}
	return r
}

func ltoa(a int64) string {
	if a <= 0 {
		return ""
	}
	return strconv.FormatInt(a, 10)
}

func join(a []int64) string {
	if a == nil || len(a) == 0 {
		return ""
	}
	return strings.Trim(strings.Replace(fmt.Sprint(a), " ", ",", -1), "[]")
}

type SignUpParam struct {
	Username   string `json:"username"`
	Password   string `json:"password"`
	Nickname   string `json:"nickname"`
	DeviceId   int64  `json:"deviceId"`
	DeviceType int32  `json:"deviceType"`
	ClientType int32  `json:"clientType"`
}

type SignInParam struct {
	Username   string `json:"username"`
	Password   string `json:"password"`
	DeviceId   int64  `json:"deviceId"`
	DeviceType int32  `json:"deviceType"`
	ClientType int32  `json:"clientType"`
}

type AccessTokenResult struct {
	AccessToken string `json:"accessToken"`
	TokenType   string `json:"tokenType"`
	ExpiresIn   int64  `json:"expiresIn"`
}

type SessionResult struct {
	UserId   int64  `json:"userId"`
	Username string `json:"username"`
	Nickname string `json:"nickname"`
	Avatar   string `json:"avatar"`
}

type UserResult struct {
	Id       int64  `json:"userId"`
	Username string `json:"username"`
	Nickname string `json:"nickname"`
	Avatar   string `json:"avatar"`
}

type FileResult struct {
	Id       string `json:"id"`
	Filename string `json:"filename"`
	Size     int64  `json:"size"`
}

type InstanceResult struct {
	Host    string `json:"host"`
	RawPort int    `json:"rawPort"`
	WsPort  int    `json:"wsPort"`
}

type ContactResult struct {
	Id          int64  `json:"id"`
	Recipient   int64  `json:"recipient"`
	Nickname    string `json:"nickname"`
	Avatar      string `json:"avatar"`
	Alias       string `json:"alias"`
	CreatedTime int64  `json:"createdTime"`
	UpdatedTime int64  `json:"updatedTime"`
}

type ContactRequestCreateParam struct {
	Recipient int64  `json:"recipient"`
	Message   string `json:"message"`
}

type ContactRequestResult struct {
	Id          int64  `json:"id"`
	Nickname    string `json:"nickname"`
	Avatar      string `json:"avatar"`
	Message     string `json:"message"`
	CreatedTime int64  `json:"createdTime"`
	UpdatedTime int64  `json:"updatedTime"`
}

type GroupCreateParam struct {
	Name string `json:"name"`
}

type GroupResult struct {
	Id          int64  `json:"id"`
	Name        string `json:"name"`
	Owner       int64  `json:"owner"`
	CreatedTime int64  `json:"createdTime"`
	UpdatedTime int64  `json:"updatedTime"`
}

type GroupMemberCreateParam struct {
	GroupId int64 `json:"groupId"`
	UserId  int64 `json:"userId"`
}

type GroupMemberResult struct {
	UserId      int64  `json:"userId"`
	Nickname    string `json:"nickname"`
	Alias       int64  `json:"alias"`
	CreatedTime int64  `json:"createdTime"`
	UpdatedTime int64  `json:"updatedTime"`
}

type MessageCreateParam struct {
	Recipient int64  `json:"recipient"`
	Type      int32  `json:"type"`
	Content   string `json:"content"`
}

type MessageResult struct {
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

type GroupMessageCreateParam struct {
	GroupId  int64  `json:"groupId"`
	Type     int32  `json:"type"`
	Content  string `json:"content"`
	Mentions string `json:"mentions"`
}

type GroupMessageResult struct {
	Id       int64  `json:"id"`
	GroupId  int64  `json:"groupId"`
	Sender   int64  `json:"sender"`
	Type     int32  `json:"type"`
	Content  string `json:"content"`
	Mentions string `json:"mentions"`
	Status   int32  `json:"status"`
	Taken    bool   `json:"taken"`
	Seen     bool   `json:"seen"`
	Revoked  bool   `json:"revoked"`
	SentTime int64  `json:"sentTime"`
}

type GroupMessageStateResult struct {
	Taken   []int64 `json:"taken"`
	Seen    []int64 `json:"seen"`
	MaxTime int64   `json:"maxTime"`
}

// UserType
const (
	Sender int = iota
	Recipient
)

// MessageType
const (
	MessageTypeText int = iota
	MessageTypeImage
	MessageTypeVideo
	MessageTypeAudio
	MessageTypeVoice
	MessageTypeFile
	MessageTypeSticker
	MessageTypeLocation
	MessageTypeShare
)

// ContactStatus
const (
	ContactStatusAdded int = iota
	ContactStatusBroken
	ContactStatusBlocked
)

// RequestStatus
const (
	RequestStatusApplied int = iota
	RequestStatusAccepted
	RequestStatusRejected
)

// GroupStatus
const (
	GroupStatusActive int = iota
	GroupStatusDeleted
	GroupStatusBanned
)

// MemberStatus
const (
	MemberStatusJoined int = iota
	MemberStatusLeft
	MemberStatusRemoved
)

type API struct {
	uri         string
	method      string
	path        string
	accessToken string
	params      map[string]string
	body        io.Reader
	contentType string
}

type Resq struct {
	*http.Response
	err      error
	messages map[int]string
}

type Options struct {
	Uri   string
	Token string
}

type Params = map[string]string

type Msg = map[int]string

func NewAPI(opts *Options) *API {
	return &API{
		uri:         opts.Uri,
		accessToken: opts.Token,
		method:      "GET",
		path:        "/",
		contentType: "*/*",
	}
}

func (a *API) URI(s string) *API {
	a.uri = s
	return a
}
func (a *API) Method(s string) *API {
	a.method = s
	return a
}
func (a *API) Get() *API {
	a.method = http.MethodGet
	return a
}
func (a *API) Post() *API {
	a.method = http.MethodPost
	return a
}
func (a *API) Put() *API {
	a.method = http.MethodPut
	return a
}
func (a *API) Patch() *API {
	a.method = http.MethodPatch
	return a
}
func (a *API) Delete() *API {
	a.method = http.MethodDelete
	return a
}
func (a *API) Path(s string) *API {
	if strings.HasPrefix(s, "/") {
		a.path = s
	} else {
		a.path = "/" + s
	}
	return a
}
func (a *API) Pathf(format string, args ...any) *API {
	if args != nil && len(args) > 0 {
		return a.Path(fmt.Sprintf(format, args...))
	} else {
		return a.Path(format)
	}
}
func (a *API) AccessToken(s string) *API {
	a.accessToken = s
	return a
}
func (a *API) Params(p map[string]string) *API {
	a.params = p
	return a
}
func (a *API) ContentType(s string) *API {
	a.contentType = s
	return a
}
func (a *API) Json() *API {
	return a.ContentType("application/json")
}
func (a *API) Proto() *API {
	return a.ContentType("application/x-protobuf")
}
func (a *API) Body(v io.Reader) *API {
	a.body = v
	return a
}
func (a *API) JsonBody(v any) *API {
	b, _ := json.Marshal(v)
	a.body = bytes.NewBuffer(b)
	return a.ContentType("application/json")
}
func (a *API) ProtoBody(v proto.Message) *API {
	b, _ := proto.Marshal(v)
	a.body = bytes.NewBuffer(b)
	return a.ContentType("application/x-protobuf")
}
func (a *API) Exec() *Resq {
	// Build values
	values := url.Values{}
	if len(a.params) > 0 {
		for k, v := range a.params {
			values.Set(k, v)
		}
	}
	// Build headers
	headers := map[string][]string{
		"Content-Type":  {a.contentType},
		"Authorization": {"Bearer " + a.accessToken},
	}
	// Build url
	u := a.uri + a.path + "?" + values.Encode()
	// Build request
	req, err := http.NewRequest(a.method, u, a.body)
	if err != nil {
		fmt.Printf("HTTP Request '%s %s', headers: %+v, err: %+v\n", a.method, u, headers, err)
		return &Resq{Response: nil, err: err}
	}
	req.Header = headers
	// Do request
	res, err := http.DefaultClient.Do(req)
	fmt.Printf("HTTP Request '%s %s', headers: %+v, err: %+v\n", a.method, u, headers, err)
	return &Resq{Response: res, err: err}
}
func (r *Resq) Msg(m map[int]string) *Resq {
	r.messages = m
	return r
}
func (r *Resq) Write(w io.Writer) error {
	if r.err != nil {
		return r.newError(http.StatusServiceUnavailable)
	}
	defer r.Body.Close()
	code := r.StatusCode
	if code >= 200 && code < 300 {
		_, err := io.Copy(w, r.Body)
		if err != nil {
			return r.newError(http.StatusInternalServerError)
		}
		return nil
	}
	return r.newError(code)
}
func (r *Resq) End() error {
	if r.err != nil {
		return r.newError(http.StatusServiceUnavailable)
	}
	defer r.Body.Close()
	code := r.StatusCode
	if code >= 200 && code < 300 {
		return nil
	}
	return r.newError(code)
}
func (r *Resq) ToBytes() ([]byte, error) {
	if r.err != nil {
		return nil, r.newError(http.StatusServiceUnavailable)
	}
	defer r.Body.Close()
	code := r.StatusCode
	if code >= 200 && code < 300 {
		b, err := io.ReadAll(r.Body)
		if err != nil {
			return nil, r.newError(http.StatusInternalServerError)
		}
		return b, nil
	}
	return nil, r.newError(code)
}
func (r *Resq) ToJson(v any) error {
	b, err := r.ToBytes()
	if err != nil {
		return err
	}
	err = json.Unmarshal(b, v)
	if err != nil {
		return r.newError(http.StatusInternalServerError)
	}
	return nil
}
func (r *Resq) ToProto(v proto.Message) error {
	b, err := r.ToBytes()
	if err != nil {
		return err
	}
	err = json.Unmarshal(b, v)
	if err != nil {
		return r.newError(http.StatusInternalServerError)
	}
	return nil
}
func (r *Resq) newError(code int) error {
	if r.messages != nil {
		msg, ok := r.messages[code]
		if ok {
			return errors.New(msg)
		}
	}
	var m string
	switch code {
	case http.StatusBadRequest:
		m = "参数格式错误"
	case http.StatusUnauthorized:
		m = "请登录"
	case http.StatusForbidden:
		m = "您没有权限"
	case http.StatusNotFound:
		m = "数据不存在"
	case http.StatusRequestTimeout:
		m = "请求超时，请检查网络环境"
	case http.StatusConflict:
		m = "数据冲突"
	case http.StatusInternalServerError:
		m = "系统异常"
	case http.StatusBadGateway:
		m = "服务器拥挤，请稍后再试"
	case http.StatusServiceUnavailable:
		m = "服务器拥挤，请稍后再试"
	case http.StatusGatewayTimeout:
		m = "服务器拥挤，请稍后再试"
	default:
		m = "系统异常"
	}
	return errors.New(m)
}
