package schema

import (
	"entgo.io/ent"
	"entgo.io/ent/dialect/entsql"
	"entgo.io/ent/schema"
	"entgo.io/ent/schema/field"
)

// Session holds the schema definition for the Session entity.
type Session struct {
	ent.Schema
}

// Annotations of the Session.
func (Session) Annotations() []schema.Annotation {
	return []schema.Annotation{
		entsql.Annotation{Table: "session"},
	}
}

// Fields of the Session.
func (Session) Fields() []ent.Field {
	return []ent.Field{
		field.Int64("id"),
		field.Int64("user_id"),
		field.String("username"),
		field.String("nickname"),
		field.String("avatar"),
		field.String("access_token"),
		field.String("token_type"),
		field.Int64("expires_in"),
		field.Int64("login_time"),
		field.Int64("expired_time"),
		field.Int64("refresh_time"),
	}
}
