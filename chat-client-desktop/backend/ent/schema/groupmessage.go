package schema

import (
	"entgo.io/ent"
	"entgo.io/ent/dialect/entsql"
	"entgo.io/ent/schema"
	"entgo.io/ent/schema/field"
	"entgo.io/ent/schema/index"
)

// GroupMessage holds the schema definition for the GroupMessage entity.
type GroupMessage struct {
	ent.Schema
}

// Annotations of the GroupMessage.
func (GroupMessage) Annotations() []schema.Annotation {
	return []schema.Annotation{
		entsql.Annotation{Table: "group_message"},
	}
}

// Fields of the GroupMessage.
func (GroupMessage) Fields() []ent.Field {
	return []ent.Field{
		field.Int64("id"),
		field.Int64("group_id"),
		field.Int64("sender"),
		field.Int64("recipient"),
		field.Int32("type"),
		field.String("content"),
		field.Int64("sent_time"),
		field.Int64("taken_time").Optional().Nillable(),
		field.Int64("seen_time").Optional().Nillable(),
		field.Int64("revoked_time").Optional().Nillable(),
	}
}

// Indexes of the GroupMessage.
func (GroupMessage) Indexes() []ent.Index {
	return []ent.Index{
		index.Fields("group_id", "sent_time"),
		index.Fields("sender", "sent_time"),
		index.Fields("recipient", "sent_time"),
	}
}
