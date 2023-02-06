package schema

import (
	"entgo.io/ent"
	"entgo.io/ent/dialect/entsql"
	"entgo.io/ent/schema"
	"entgo.io/ent/schema/field"
)

// Chatmate holds the schema definition for the Chatmate entity.
type Chatmate struct {
	ent.Schema
}

// Annotations of the Chatmate.
func (Chatmate) Annotations() []schema.Annotation {
	return []schema.Annotation{
		entsql.Annotation{Table: "chatmate"},
	}
}

// Fields of the Chatmate.
func (Chatmate) Fields() []ent.Field {
	return []ent.Field{
		field.Int64("id"),
		field.Int64("biz_id"),
		field.Int32("type"),
		field.String("name"),
		field.String("avatar"),
		field.Int64("last_message_id"),
		field.Int64("last_chat_time"),
	}
}
