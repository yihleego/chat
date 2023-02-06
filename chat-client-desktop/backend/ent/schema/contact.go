package schema

import (
	"entgo.io/ent"
	"entgo.io/ent/dialect/entsql"
	"entgo.io/ent/schema"
	"entgo.io/ent/schema/field"
)

// Contact holds the schema definition for the Contact entity.
type Contact struct {
	ent.Schema
}

// Annotations of the Contact.
func (Contact) Annotations() []schema.Annotation {
	return []schema.Annotation{
		entsql.Annotation{Table: "contact"},
	}
}

// Fields of the Contact.
func (Contact) Fields() []ent.Field {
	return []ent.Field{
		field.Int64("id"),
		field.Int64("user_id"),
		field.String("username"),
		field.String("nickname"),
		field.String("avatar"),
	}
}
