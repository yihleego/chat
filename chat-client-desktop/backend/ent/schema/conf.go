package schema

import (
	"entgo.io/ent"
	"entgo.io/ent/dialect/entsql"
	"entgo.io/ent/schema"
	"entgo.io/ent/schema/field"
)

// Conf holds the schema definition for the Conf entity.
type Conf struct {
	ent.Schema
}

// Annotations of the Conf.
func (Conf) Annotations() []schema.Annotation {
	return []schema.Annotation{
		entsql.Annotation{Table: "config"},
	}
}

// Fields of the Conf.
func (Conf) Fields() []ent.Field {
	return []ent.Field{
		field.String("id"),
		field.String("value"),
	}
}
