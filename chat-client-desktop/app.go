package main

import (
	"context"
	"github.com/wailsapp/wails/v2/pkg/runtime"
	chat "github.com/yihleego/chat/backend"
)

// App struct
type App struct {
	chat *chat.Chat
}

// NewApp creates a new App application struct
func NewApp() *App {
	return &App{chat: &chat.Chat{}}
}

// startup is called when the app starts.
func (a *App) startup(ctx context.Context) {
	if runtime.Environment(ctx).BuildType == "dev" {
		a.chat.Dev()
	}
	a.chat.Startup(ctx)
}

// shutdown is called when the app stops.
func (a *App) shutdown(_ context.Context) {
	a.chat.Shutdown()
}

// bind specifies which struct methods to expose to the frontend.
func (a *App) export() []any {
	return []any{a.chat}
}
