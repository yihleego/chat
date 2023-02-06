package repo

import (
	"context"
	_ "github.com/mattn/go-sqlite3"
	"github.com/yihleego/chat/backend/ent"
	"github.com/yihleego/chat/backend/ent/message"
	"github.com/yihleego/chat/backend/ent/session"
	"log"
	"time"
)

var db *ent.Client

// init
func init() {
	var err error
	db, err = ent.Open("sqlite3", "file:chat.db?mode=rwc&cache=shared&_fk=1")
	if err != nil {
		log.Fatalf("failed opening connection to sqlite: %v", err)
	}
	// Run the auto migration tool.
	if err := db.Schema.Create(context.Background()); err != nil {
		log.Fatalf("failed creating schema resources: %v", err)
	}
}

func SaveConfig(key, value string) error {
	return db.Conf.
		Create().
		SetID(key).
		SetValue(value).
		OnConflict().
		UpdateNewValues().
		Exec(context.Background())
}

func FindAllConfig() (map[string]string, error) {
	c, err := db.Conf.
		Query().
		All(context.Background())
	if err != nil {
		return nil, err
	}
	res := make(map[string]string)
	for _, v := range c {
		res[v.ID] = v.Value
	}
	return res, nil
}

func SaveSession(userId int64, username, nickname, avatar string,
	accessToken, tokenType string, expiresIn int64,
	LoginTime, expiredTime, refreshTime int64) error {
	return db.Session.
		Create().
		SetID(1).
		SetUserID(userId).
		SetUsername(username).
		SetNickname(nickname).
		SetAvatar(avatar).
		SetAccessToken(accessToken).
		SetTokenType(tokenType).
		SetExpiresIn(expiresIn).
		SetLoginTime(LoginTime).
		SetExpiredTime(expiredTime).
		SetRefreshTime(refreshTime).
		OnConflict().
		UpdateNewValues().
		Exec(context.Background())
}

func DeleteSession() (int, error) {
	return db.Session.
		Delete().
		Where(session.ID(1)).
		Exec(context.Background())
}

func RefreshSession(expiredTime, refreshTime int64) error {
	return db.Session.
		UpdateOneID(1).
		SetExpiredTime(expiredTime).
		SetRefreshTime(refreshTime).
		Exec(context.Background())
}

func FindSession() (*ent.Session, error) {
	return db.Session.
		Query().
		Where(session.ID(1)).
		First(context.Background())
}

func SaveMessage(id, sender, recipient int64,
	type1 int32, content string, sentTime int64, taken, seen, revoked bool) error {
	now := time.Now().UnixMilli()
	m, err := db.Message.
		Query().
		Where(message.ID(id)).
		First(context.Background())
	if err != nil {
		return err
	}
	if m == nil {
		var takenTime, seenTime, revokedTime *int64
		if taken {
			takenTime = &now
		}
		if seen {
			seenTime = &now
		}
		if revoked {
			revokedTime = &now
		}
		return db.Message.
			Create().
			SetID(id).
			SetSender(sender).
			SetRecipient(recipient).
			SetType(type1).
			SetContent(content).
			SetSentTime(sentTime).
			SetNillableTakenTime(takenTime).
			SetNillableSeenTime(seenTime).
			SetNillableRevokedTime(revokedTime).
			Exec(context.Background())
	} else {
		if m.TakenTime == nil && taken {
			m.TakenTime = &now
		}
		if m.SeenTime == nil && seen {
			m.SeenTime = &now
		}
		if m.RevokedTime == nil && seen {
			m.RevokedTime = &now
		}
		return db.Message.
			UpdateOneID(id).
			SetNillableTakenTime(m.TakenTime).
			SetNillableSeenTime(m.SeenTime).
			SetNillableRevokedTime(m.RevokedTime).
			Exec(context.Background())
	}
}

func GetMessage(id int64) (*ent.Message, error) {
	return db.Message.
		Query().
		Where(message.ID(id)).
		First(context.Background())
}

func UpdateMessageTaken(ids []int64, time int64) error {
	return db.Message.
		Update().
		SetTakenTime(time).
		Where(message.IDIn(ids...), message.TakenTimeIsNil()).
		Exec(context.Background())
}

func UpdateMessageSeen(ids []int64, time int64) error {
	err := db.Message.
		Update().
		SetTakenTime(time).
		SetSeenTime(time).
		Where(message.IDIn(ids...), message.TakenTimeIsNil(), message.SeenTimeIsNil()).
		Exec(context.Background())
	if err != nil {
		return err
	}
	return db.Message.
		Update().
		SetSeenTime(time).
		Where(message.IDIn(ids...), message.SeenTimeIsNil()).
		Exec(context.Background())
}

func UpdateMessageRevoked(ids []int64, time int64) error {
	return db.Message.
		Update().
		SetTakenTime(time).
		Where(message.IDIn(ids...), message.TakenTimeIsNil()).
		Exec(context.Background())
}

func SaveGroupMessage(id, groupId, sender, recipient int64,
	type1 int32, content string, sentTime int64, taken, seen, revoked bool) error {
	return nil
}
