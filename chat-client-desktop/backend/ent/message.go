// Code generated by ent, DO NOT EDIT.

package ent

import (
	"fmt"
	"strings"

	"entgo.io/ent/dialect/sql"
	"github.com/yihleego/chat/backend/ent/message"
)

// Message is the model entity for the Message schema.
type Message struct {
	config `json:"-"`
	// ID of the ent.
	ID int64 `json:"id,omitempty"`
	// Sender holds the value of the "sender" field.
	Sender int64 `json:"sender,omitempty"`
	// Recipient holds the value of the "recipient" field.
	Recipient int64 `json:"recipient,omitempty"`
	// Type holds the value of the "type" field.
	Type int32 `json:"type,omitempty"`
	// Content holds the value of the "content" field.
	Content string `json:"content,omitempty"`
	// SentTime holds the value of the "sent_time" field.
	SentTime int64 `json:"sent_time,omitempty"`
	// TakenTime holds the value of the "taken_time" field.
	TakenTime *int64 `json:"taken_time,omitempty"`
	// SeenTime holds the value of the "seen_time" field.
	SeenTime *int64 `json:"seen_time,omitempty"`
	// RevokedTime holds the value of the "revoked_time" field.
	RevokedTime *int64 `json:"revoked_time,omitempty"`
}

// scanValues returns the types for scanning values from sql.Rows.
func (*Message) scanValues(columns []string) ([]any, error) {
	values := make([]any, len(columns))
	for i := range columns {
		switch columns[i] {
		case message.FieldID, message.FieldSender, message.FieldRecipient, message.FieldType, message.FieldSentTime, message.FieldTakenTime, message.FieldSeenTime, message.FieldRevokedTime:
			values[i] = new(sql.NullInt64)
		case message.FieldContent:
			values[i] = new(sql.NullString)
		default:
			return nil, fmt.Errorf("unexpected column %q for type Message", columns[i])
		}
	}
	return values, nil
}

// assignValues assigns the values that were returned from sql.Rows (after scanning)
// to the Message fields.
func (m *Message) assignValues(columns []string, values []any) error {
	if m, n := len(values), len(columns); m < n {
		return fmt.Errorf("mismatch number of scan values: %d != %d", m, n)
	}
	for i := range columns {
		switch columns[i] {
		case message.FieldID:
			value, ok := values[i].(*sql.NullInt64)
			if !ok {
				return fmt.Errorf("unexpected type %T for field id", value)
			}
			m.ID = int64(value.Int64)
		case message.FieldSender:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field sender", values[i])
			} else if value.Valid {
				m.Sender = value.Int64
			}
		case message.FieldRecipient:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field recipient", values[i])
			} else if value.Valid {
				m.Recipient = value.Int64
			}
		case message.FieldType:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field type", values[i])
			} else if value.Valid {
				m.Type = int32(value.Int64)
			}
		case message.FieldContent:
			if value, ok := values[i].(*sql.NullString); !ok {
				return fmt.Errorf("unexpected type %T for field content", values[i])
			} else if value.Valid {
				m.Content = value.String
			}
		case message.FieldSentTime:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field sent_time", values[i])
			} else if value.Valid {
				m.SentTime = value.Int64
			}
		case message.FieldTakenTime:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field taken_time", values[i])
			} else if value.Valid {
				m.TakenTime = new(int64)
				*m.TakenTime = value.Int64
			}
		case message.FieldSeenTime:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field seen_time", values[i])
			} else if value.Valid {
				m.SeenTime = new(int64)
				*m.SeenTime = value.Int64
			}
		case message.FieldRevokedTime:
			if value, ok := values[i].(*sql.NullInt64); !ok {
				return fmt.Errorf("unexpected type %T for field revoked_time", values[i])
			} else if value.Valid {
				m.RevokedTime = new(int64)
				*m.RevokedTime = value.Int64
			}
		}
	}
	return nil
}

// Update returns a builder for updating this Message.
// Note that you need to call Message.Unwrap() before calling this method if this Message
// was returned from a transaction, and the transaction was committed or rolled back.
func (m *Message) Update() *MessageUpdateOne {
	return (&MessageClient{config: m.config}).UpdateOne(m)
}

// Unwrap unwraps the Message entity that was returned from a transaction after it was closed,
// so that all future queries will be executed through the driver which created the transaction.
func (m *Message) Unwrap() *Message {
	_tx, ok := m.config.driver.(*txDriver)
	if !ok {
		panic("ent: Message is not a transactional entity")
	}
	m.config.driver = _tx.drv
	return m
}

// String implements the fmt.Stringer.
func (m *Message) String() string {
	var builder strings.Builder
	builder.WriteString("Message(")
	builder.WriteString(fmt.Sprintf("id=%v, ", m.ID))
	builder.WriteString("sender=")
	builder.WriteString(fmt.Sprintf("%v", m.Sender))
	builder.WriteString(", ")
	builder.WriteString("recipient=")
	builder.WriteString(fmt.Sprintf("%v", m.Recipient))
	builder.WriteString(", ")
	builder.WriteString("type=")
	builder.WriteString(fmt.Sprintf("%v", m.Type))
	builder.WriteString(", ")
	builder.WriteString("content=")
	builder.WriteString(m.Content)
	builder.WriteString(", ")
	builder.WriteString("sent_time=")
	builder.WriteString(fmt.Sprintf("%v", m.SentTime))
	builder.WriteString(", ")
	if v := m.TakenTime; v != nil {
		builder.WriteString("taken_time=")
		builder.WriteString(fmt.Sprintf("%v", *v))
	}
	builder.WriteString(", ")
	if v := m.SeenTime; v != nil {
		builder.WriteString("seen_time=")
		builder.WriteString(fmt.Sprintf("%v", *v))
	}
	builder.WriteString(", ")
	if v := m.RevokedTime; v != nil {
		builder.WriteString("revoked_time=")
		builder.WriteString(fmt.Sprintf("%v", *v))
	}
	builder.WriteByte(')')
	return builder.String()
}

// Messages is a parsable slice of Message.
type Messages []*Message

func (m Messages) config(cfg config) {
	for _i := range m {
		m[_i].config = cfg
	}
}
