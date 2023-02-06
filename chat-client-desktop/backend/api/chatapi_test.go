package api

import (
	"fmt"
	"github.com/yihleego/chat/backend/util"
	"os"
	"strings"
	"testing"
)

func init() {
	SetOptions(&Options{
		Uri:   "http://localhost:8888",
		Token: "23Z3I9qzSruDxK5mUYmLrw",
	})
}

func TestSignUp(t *testing.T) {
	param := SignUpParam{
		Username:   "chat",
		Password:   "chat",
		Nickname:   "chat",
		DeviceId:   0,
		DeviceType: util.GetDeviceType(),
		ClientType: util.GetClientType(),
	}
	res, err := SignUp(param)
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("SignUp: %+v", res)
}

func TestSignIn(t *testing.T) {
	param := SignInParam{
		Username:   "chat",
		Password:   "chat",
		DeviceId:   0,
		DeviceType: util.GetDeviceType(),
		ClientType: util.GetClientType(),
	}
	res, err := SignIn(param)
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("SignIn: %+v", res)
}

func TestGetSession(t *testing.T) {
	res, err := GetSession()
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("GetSession: %+v", res)
}

func TestListMessages(t *testing.T) {
	res, err := ListMessages(0, Sender)
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("ListMessages: %+v", res)
}

func TestUploadFile(t *testing.T) {
	file1, err := os.Open("chatapi.go")
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	defer file1.Close()
	file2, err := os.Open("chatapi_test.go")
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	defer file2.Close()
	res, err := UploadFile([]*os.File{file1, file2})
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("UploadFile: %+v", res)
}

func TestDownloadFile(t *testing.T) {
	err := os.Mkdir("temp", 0766)
	if err != nil {
		t.Logf("Failed: %v", err)
	}
	res, err := DownloadFile("hdTAa4ZsQ-GTAm5s5dXNWA", "temp")
	if err != nil {
		t.Fatalf("Failed: %v", err)
	}
	t.Logf("DownloadFile: %+v", res)
}

func TestInt64JoinString(t *testing.T) {
	a := []int64{1, 2, 3, 4, 5}
	s := strings.Trim(strings.Replace(fmt.Sprint(a), " ", ",", -1), "[]")
	t.Logf("%s", s)
}
