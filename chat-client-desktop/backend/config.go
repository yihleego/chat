package chat

import (
	"github.com/yihleego/chat/backend/repo"
	"github.com/yihleego/chat/backend/util"
	"strconv"
	"strings"
)

var config Config

// Config struct
type Config struct {
	Language          string
	DeviceId          int64
	DeviceType        int32
	ClientType        int32
	LoginWindowWidth  int
	LoginWindowHeight int
	MainWindowWidth   int
	MainWindowHeight  int
	ApiAddrDev        string
	ApiAddrProd       string
}

func init() {
	// Configs
	conf, _ := repo.FindAllConfig()
	// Find language
	lang, ok := conf[ConfigLanguage]
	if !ok {
		lang = DefaultLanguage
		repo.SaveConfig(ConfigLanguage, lang)
	}
	// Find device id
	deviceIdStr, ok := conf[ConfigDeviceId]
	if !ok {
		deviceIdStr = strconv.FormatInt(util.GetDeviceId(), 10)
		repo.SaveConfig(ConfigDeviceId, deviceIdStr)
	}
	deviceId, _ := strconv.ParseInt(deviceIdStr, 10, 64)
	// Find login window size
	loginWindowSize, ok := conf[ConfigLoginWindowSize]
	if !ok {
		loginWindowSize = DefaultLoginWindowSize
		repo.SaveConfig(ConfigLoginWindowSize, DefaultLoginWindowSize)
	}
	s1 := strings.Split(loginWindowSize, "_")
	loginWindowWidth, _ := strconv.Atoi(s1[0])
	loginWindowHeight, _ := strconv.Atoi(s1[1])
	// Find main window size
	mainWindowSize, ok := conf[ConfigMainWindowSize]
	if !ok {
		mainWindowSize = DefaultMainWindowSize
		repo.SaveConfig(ConfigMainWindowSize, DefaultMainWindowSize)
	}
	s2 := strings.Split(mainWindowSize, "_")
	mainWindowWidth, _ := strconv.Atoi(s2[0])
	mainWindowHeight, _ := strconv.Atoi(s2[1])
	// Find API addr
	apiAddrDev, ok := conf[ConfigApiAddrDev]
	if !ok {
		apiAddrDev = DefaultApiAddrDev
		repo.SaveConfig(ConfigApiAddrDev, DefaultApiAddrDev)
	}
	apiAddrProd, ok := conf[ConfigApiAddrProd]
	if !ok {
		apiAddrProd = DefaultApiAddrProd
		repo.SaveConfig(ConfigApiAddrProd, DefaultApiAddrProd)
	}
	config = Config{
		Language:          lang,
		DeviceId:          deviceId,
		DeviceType:        int32(util.GetDeviceType()),
		ClientType:        int32(util.GetClientType()),
		LoginWindowWidth:  loginWindowWidth,
		LoginWindowHeight: loginWindowHeight,
		MainWindowWidth:   mainWindowWidth,
		MainWindowHeight:  mainWindowHeight,
		ApiAddrDev:        apiAddrDev,
		ApiAddrProd:       apiAddrProd,
	}
}

// config
const (
	ConfigLanguage        string = "language"
	ConfigDeviceId        string = "device_id"
	ConfigLoginWindowSize string = "login_window_size"
	ConfigMainWindowSize  string = "main_window_size"
	ConfigApiAddrDev      string = "api_addr_dev"
	ConfigApiAddrProd     string = "api_addr_prod"
)

// default
const (
	DefaultLanguage        string = "zh-Hans"
	DefaultLoginWindowSize string = "300_400"
	DefaultMainWindowSize  string = "1024_768"
	DefaultApiAddrDev      string = "http://localhost:8888"
	DefaultApiAddrProd     string = "https://chat.leego.io"
)
