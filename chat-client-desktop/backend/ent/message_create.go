// Code generated by ent, DO NOT EDIT.

package ent

import (
	"context"
	"errors"
	"fmt"

	"entgo.io/ent/dialect/sql"
	"entgo.io/ent/dialect/sql/sqlgraph"
	"entgo.io/ent/schema/field"
	"github.com/yihleego/chat/backend/ent/message"
)

// MessageCreate is the builder for creating a Message entity.
type MessageCreate struct {
	config
	mutation *MessageMutation
	hooks    []Hook
	conflict []sql.ConflictOption
}

// SetSender sets the "sender" field.
func (mc *MessageCreate) SetSender(i int64) *MessageCreate {
	mc.mutation.SetSender(i)
	return mc
}

// SetRecipient sets the "recipient" field.
func (mc *MessageCreate) SetRecipient(i int64) *MessageCreate {
	mc.mutation.SetRecipient(i)
	return mc
}

// SetType sets the "type" field.
func (mc *MessageCreate) SetType(i int32) *MessageCreate {
	mc.mutation.SetType(i)
	return mc
}

// SetContent sets the "content" field.
func (mc *MessageCreate) SetContent(s string) *MessageCreate {
	mc.mutation.SetContent(s)
	return mc
}

// SetSentTime sets the "sent_time" field.
func (mc *MessageCreate) SetSentTime(i int64) *MessageCreate {
	mc.mutation.SetSentTime(i)
	return mc
}

// SetTakenTime sets the "taken_time" field.
func (mc *MessageCreate) SetTakenTime(i int64) *MessageCreate {
	mc.mutation.SetTakenTime(i)
	return mc
}

// SetNillableTakenTime sets the "taken_time" field if the given value is not nil.
func (mc *MessageCreate) SetNillableTakenTime(i *int64) *MessageCreate {
	if i != nil {
		mc.SetTakenTime(*i)
	}
	return mc
}

// SetSeenTime sets the "seen_time" field.
func (mc *MessageCreate) SetSeenTime(i int64) *MessageCreate {
	mc.mutation.SetSeenTime(i)
	return mc
}

// SetNillableSeenTime sets the "seen_time" field if the given value is not nil.
func (mc *MessageCreate) SetNillableSeenTime(i *int64) *MessageCreate {
	if i != nil {
		mc.SetSeenTime(*i)
	}
	return mc
}

// SetRevokedTime sets the "revoked_time" field.
func (mc *MessageCreate) SetRevokedTime(i int64) *MessageCreate {
	mc.mutation.SetRevokedTime(i)
	return mc
}

// SetNillableRevokedTime sets the "revoked_time" field if the given value is not nil.
func (mc *MessageCreate) SetNillableRevokedTime(i *int64) *MessageCreate {
	if i != nil {
		mc.SetRevokedTime(*i)
	}
	return mc
}

// SetID sets the "id" field.
func (mc *MessageCreate) SetID(i int64) *MessageCreate {
	mc.mutation.SetID(i)
	return mc
}

// Mutation returns the MessageMutation object of the builder.
func (mc *MessageCreate) Mutation() *MessageMutation {
	return mc.mutation
}

// Save creates the Message in the database.
func (mc *MessageCreate) Save(ctx context.Context) (*Message, error) {
	var (
		err  error
		node *Message
	)
	if len(mc.hooks) == 0 {
		if err = mc.check(); err != nil {
			return nil, err
		}
		node, err = mc.sqlSave(ctx)
	} else {
		var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
			mutation, ok := m.(*MessageMutation)
			if !ok {
				return nil, fmt.Errorf("unexpected mutation type %T", m)
			}
			if err = mc.check(); err != nil {
				return nil, err
			}
			mc.mutation = mutation
			if node, err = mc.sqlSave(ctx); err != nil {
				return nil, err
			}
			mutation.id = &node.ID
			mutation.done = true
			return node, err
		})
		for i := len(mc.hooks) - 1; i >= 0; i-- {
			if mc.hooks[i] == nil {
				return nil, fmt.Errorf("ent: uninitialized hook (forgotten import ent/runtime?)")
			}
			mut = mc.hooks[i](mut)
		}
		v, err := mut.Mutate(ctx, mc.mutation)
		if err != nil {
			return nil, err
		}
		nv, ok := v.(*Message)
		if !ok {
			return nil, fmt.Errorf("unexpected node type %T returned from MessageMutation", v)
		}
		node = nv
	}
	return node, err
}

// SaveX calls Save and panics if Save returns an error.
func (mc *MessageCreate) SaveX(ctx context.Context) *Message {
	v, err := mc.Save(ctx)
	if err != nil {
		panic(err)
	}
	return v
}

// Exec executes the query.
func (mc *MessageCreate) Exec(ctx context.Context) error {
	_, err := mc.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (mc *MessageCreate) ExecX(ctx context.Context) {
	if err := mc.Exec(ctx); err != nil {
		panic(err)
	}
}

// check runs all checks and user-defined validators on the builder.
func (mc *MessageCreate) check() error {
	if _, ok := mc.mutation.Sender(); !ok {
		return &ValidationError{Name: "sender", err: errors.New(`ent: missing required field "Message.sender"`)}
	}
	if _, ok := mc.mutation.Recipient(); !ok {
		return &ValidationError{Name: "recipient", err: errors.New(`ent: missing required field "Message.recipient"`)}
	}
	if _, ok := mc.mutation.GetType(); !ok {
		return &ValidationError{Name: "type", err: errors.New(`ent: missing required field "Message.type"`)}
	}
	if _, ok := mc.mutation.Content(); !ok {
		return &ValidationError{Name: "content", err: errors.New(`ent: missing required field "Message.content"`)}
	}
	if _, ok := mc.mutation.SentTime(); !ok {
		return &ValidationError{Name: "sent_time", err: errors.New(`ent: missing required field "Message.sent_time"`)}
	}
	return nil
}

func (mc *MessageCreate) sqlSave(ctx context.Context) (*Message, error) {
	_node, _spec := mc.createSpec()
	if err := sqlgraph.CreateNode(ctx, mc.driver, _spec); err != nil {
		if sqlgraph.IsConstraintError(err) {
			err = &ConstraintError{msg: err.Error(), wrap: err}
		}
		return nil, err
	}
	if _spec.ID.Value != _node.ID {
		id := _spec.ID.Value.(int64)
		_node.ID = int64(id)
	}
	return _node, nil
}

func (mc *MessageCreate) createSpec() (*Message, *sqlgraph.CreateSpec) {
	var (
		_node = &Message{config: mc.config}
		_spec = &sqlgraph.CreateSpec{
			Table: message.Table,
			ID: &sqlgraph.FieldSpec{
				Type:   field.TypeInt64,
				Column: message.FieldID,
			},
		}
	)
	_spec.OnConflict = mc.conflict
	if id, ok := mc.mutation.ID(); ok {
		_node.ID = id
		_spec.ID.Value = id
	}
	if value, ok := mc.mutation.Sender(); ok {
		_spec.SetField(message.FieldSender, field.TypeInt64, value)
		_node.Sender = value
	}
	if value, ok := mc.mutation.Recipient(); ok {
		_spec.SetField(message.FieldRecipient, field.TypeInt64, value)
		_node.Recipient = value
	}
	if value, ok := mc.mutation.GetType(); ok {
		_spec.SetField(message.FieldType, field.TypeInt32, value)
		_node.Type = value
	}
	if value, ok := mc.mutation.Content(); ok {
		_spec.SetField(message.FieldContent, field.TypeString, value)
		_node.Content = value
	}
	if value, ok := mc.mutation.SentTime(); ok {
		_spec.SetField(message.FieldSentTime, field.TypeInt64, value)
		_node.SentTime = value
	}
	if value, ok := mc.mutation.TakenTime(); ok {
		_spec.SetField(message.FieldTakenTime, field.TypeInt64, value)
		_node.TakenTime = &value
	}
	if value, ok := mc.mutation.SeenTime(); ok {
		_spec.SetField(message.FieldSeenTime, field.TypeInt64, value)
		_node.SeenTime = &value
	}
	if value, ok := mc.mutation.RevokedTime(); ok {
		_spec.SetField(message.FieldRevokedTime, field.TypeInt64, value)
		_node.RevokedTime = &value
	}
	return _node, _spec
}

// OnConflict allows configuring the `ON CONFLICT` / `ON DUPLICATE KEY` clause
// of the `INSERT` statement. For example:
//
//	client.Message.Create().
//		SetSender(v).
//		OnConflict(
//			// Update the row with the new values
//			// the was proposed for insertion.
//			sql.ResolveWithNewValues(),
//		).
//		// Override some of the fields with custom
//		// update values.
//		Update(func(u *ent.MessageUpsert) {
//			SetSender(v+v).
//		}).
//		Exec(ctx)
func (mc *MessageCreate) OnConflict(opts ...sql.ConflictOption) *MessageUpsertOne {
	mc.conflict = opts
	return &MessageUpsertOne{
		create: mc,
	}
}

// OnConflictColumns calls `OnConflict` and configures the columns
// as conflict target. Using this option is equivalent to using:
//
//	client.Message.Create().
//		OnConflict(sql.ConflictColumns(columns...)).
//		Exec(ctx)
func (mc *MessageCreate) OnConflictColumns(columns ...string) *MessageUpsertOne {
	mc.conflict = append(mc.conflict, sql.ConflictColumns(columns...))
	return &MessageUpsertOne{
		create: mc,
	}
}

type (
	// MessageUpsertOne is the builder for "upsert"-ing
	//  one Message node.
	MessageUpsertOne struct {
		create *MessageCreate
	}

	// MessageUpsert is the "OnConflict" setter.
	MessageUpsert struct {
		*sql.UpdateSet
	}
)

// SetSender sets the "sender" field.
func (u *MessageUpsert) SetSender(v int64) *MessageUpsert {
	u.Set(message.FieldSender, v)
	return u
}

// UpdateSender sets the "sender" field to the value that was provided on create.
func (u *MessageUpsert) UpdateSender() *MessageUpsert {
	u.SetExcluded(message.FieldSender)
	return u
}

// AddSender adds v to the "sender" field.
func (u *MessageUpsert) AddSender(v int64) *MessageUpsert {
	u.Add(message.FieldSender, v)
	return u
}

// SetRecipient sets the "recipient" field.
func (u *MessageUpsert) SetRecipient(v int64) *MessageUpsert {
	u.Set(message.FieldRecipient, v)
	return u
}

// UpdateRecipient sets the "recipient" field to the value that was provided on create.
func (u *MessageUpsert) UpdateRecipient() *MessageUpsert {
	u.SetExcluded(message.FieldRecipient)
	return u
}

// AddRecipient adds v to the "recipient" field.
func (u *MessageUpsert) AddRecipient(v int64) *MessageUpsert {
	u.Add(message.FieldRecipient, v)
	return u
}

// SetType sets the "type" field.
func (u *MessageUpsert) SetType(v int32) *MessageUpsert {
	u.Set(message.FieldType, v)
	return u
}

// UpdateType sets the "type" field to the value that was provided on create.
func (u *MessageUpsert) UpdateType() *MessageUpsert {
	u.SetExcluded(message.FieldType)
	return u
}

// AddType adds v to the "type" field.
func (u *MessageUpsert) AddType(v int32) *MessageUpsert {
	u.Add(message.FieldType, v)
	return u
}

// SetContent sets the "content" field.
func (u *MessageUpsert) SetContent(v string) *MessageUpsert {
	u.Set(message.FieldContent, v)
	return u
}

// UpdateContent sets the "content" field to the value that was provided on create.
func (u *MessageUpsert) UpdateContent() *MessageUpsert {
	u.SetExcluded(message.FieldContent)
	return u
}

// SetSentTime sets the "sent_time" field.
func (u *MessageUpsert) SetSentTime(v int64) *MessageUpsert {
	u.Set(message.FieldSentTime, v)
	return u
}

// UpdateSentTime sets the "sent_time" field to the value that was provided on create.
func (u *MessageUpsert) UpdateSentTime() *MessageUpsert {
	u.SetExcluded(message.FieldSentTime)
	return u
}

// AddSentTime adds v to the "sent_time" field.
func (u *MessageUpsert) AddSentTime(v int64) *MessageUpsert {
	u.Add(message.FieldSentTime, v)
	return u
}

// SetTakenTime sets the "taken_time" field.
func (u *MessageUpsert) SetTakenTime(v int64) *MessageUpsert {
	u.Set(message.FieldTakenTime, v)
	return u
}

// UpdateTakenTime sets the "taken_time" field to the value that was provided on create.
func (u *MessageUpsert) UpdateTakenTime() *MessageUpsert {
	u.SetExcluded(message.FieldTakenTime)
	return u
}

// AddTakenTime adds v to the "taken_time" field.
func (u *MessageUpsert) AddTakenTime(v int64) *MessageUpsert {
	u.Add(message.FieldTakenTime, v)
	return u
}

// ClearTakenTime clears the value of the "taken_time" field.
func (u *MessageUpsert) ClearTakenTime() *MessageUpsert {
	u.SetNull(message.FieldTakenTime)
	return u
}

// SetSeenTime sets the "seen_time" field.
func (u *MessageUpsert) SetSeenTime(v int64) *MessageUpsert {
	u.Set(message.FieldSeenTime, v)
	return u
}

// UpdateSeenTime sets the "seen_time" field to the value that was provided on create.
func (u *MessageUpsert) UpdateSeenTime() *MessageUpsert {
	u.SetExcluded(message.FieldSeenTime)
	return u
}

// AddSeenTime adds v to the "seen_time" field.
func (u *MessageUpsert) AddSeenTime(v int64) *MessageUpsert {
	u.Add(message.FieldSeenTime, v)
	return u
}

// ClearSeenTime clears the value of the "seen_time" field.
func (u *MessageUpsert) ClearSeenTime() *MessageUpsert {
	u.SetNull(message.FieldSeenTime)
	return u
}

// SetRevokedTime sets the "revoked_time" field.
func (u *MessageUpsert) SetRevokedTime(v int64) *MessageUpsert {
	u.Set(message.FieldRevokedTime, v)
	return u
}

// UpdateRevokedTime sets the "revoked_time" field to the value that was provided on create.
func (u *MessageUpsert) UpdateRevokedTime() *MessageUpsert {
	u.SetExcluded(message.FieldRevokedTime)
	return u
}

// AddRevokedTime adds v to the "revoked_time" field.
func (u *MessageUpsert) AddRevokedTime(v int64) *MessageUpsert {
	u.Add(message.FieldRevokedTime, v)
	return u
}

// ClearRevokedTime clears the value of the "revoked_time" field.
func (u *MessageUpsert) ClearRevokedTime() *MessageUpsert {
	u.SetNull(message.FieldRevokedTime)
	return u
}

// UpdateNewValues updates the mutable fields using the new values that were set on create except the ID field.
// Using this option is equivalent to using:
//
//	client.Message.Create().
//		OnConflict(
//			sql.ResolveWithNewValues(),
//			sql.ResolveWith(func(u *sql.UpdateSet) {
//				u.SetIgnore(message.FieldID)
//			}),
//		).
//		Exec(ctx)
func (u *MessageUpsertOne) UpdateNewValues() *MessageUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithNewValues())
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(s *sql.UpdateSet) {
		if _, exists := u.create.mutation.ID(); exists {
			s.SetIgnore(message.FieldID)
		}
	}))
	return u
}

// Ignore sets each column to itself in case of conflict.
// Using this option is equivalent to using:
//
//	client.Message.Create().
//	    OnConflict(sql.ResolveWithIgnore()).
//	    Exec(ctx)
func (u *MessageUpsertOne) Ignore() *MessageUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithIgnore())
	return u
}

// DoNothing configures the conflict_action to `DO NOTHING`.
// Supported only by SQLite and PostgreSQL.
func (u *MessageUpsertOne) DoNothing() *MessageUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.DoNothing())
	return u
}

// Update allows overriding fields `UPDATE` values. See the MessageCreate.OnConflict
// documentation for more info.
func (u *MessageUpsertOne) Update(set func(*MessageUpsert)) *MessageUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(update *sql.UpdateSet) {
		set(&MessageUpsert{UpdateSet: update})
	}))
	return u
}

// SetSender sets the "sender" field.
func (u *MessageUpsertOne) SetSender(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetSender(v)
	})
}

// AddSender adds v to the "sender" field.
func (u *MessageUpsertOne) AddSender(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddSender(v)
	})
}

// UpdateSender sets the "sender" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateSender() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSender()
	})
}

// SetRecipient sets the "recipient" field.
func (u *MessageUpsertOne) SetRecipient(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetRecipient(v)
	})
}

// AddRecipient adds v to the "recipient" field.
func (u *MessageUpsertOne) AddRecipient(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddRecipient(v)
	})
}

// UpdateRecipient sets the "recipient" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateRecipient() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateRecipient()
	})
}

// SetType sets the "type" field.
func (u *MessageUpsertOne) SetType(v int32) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetType(v)
	})
}

// AddType adds v to the "type" field.
func (u *MessageUpsertOne) AddType(v int32) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddType(v)
	})
}

// UpdateType sets the "type" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateType() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateType()
	})
}

// SetContent sets the "content" field.
func (u *MessageUpsertOne) SetContent(v string) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetContent(v)
	})
}

// UpdateContent sets the "content" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateContent() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateContent()
	})
}

// SetSentTime sets the "sent_time" field.
func (u *MessageUpsertOne) SetSentTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetSentTime(v)
	})
}

// AddSentTime adds v to the "sent_time" field.
func (u *MessageUpsertOne) AddSentTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddSentTime(v)
	})
}

// UpdateSentTime sets the "sent_time" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateSentTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSentTime()
	})
}

// SetTakenTime sets the "taken_time" field.
func (u *MessageUpsertOne) SetTakenTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetTakenTime(v)
	})
}

// AddTakenTime adds v to the "taken_time" field.
func (u *MessageUpsertOne) AddTakenTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddTakenTime(v)
	})
}

// UpdateTakenTime sets the "taken_time" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateTakenTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateTakenTime()
	})
}

// ClearTakenTime clears the value of the "taken_time" field.
func (u *MessageUpsertOne) ClearTakenTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.ClearTakenTime()
	})
}

// SetSeenTime sets the "seen_time" field.
func (u *MessageUpsertOne) SetSeenTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetSeenTime(v)
	})
}

// AddSeenTime adds v to the "seen_time" field.
func (u *MessageUpsertOne) AddSeenTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddSeenTime(v)
	})
}

// UpdateSeenTime sets the "seen_time" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateSeenTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSeenTime()
	})
}

// ClearSeenTime clears the value of the "seen_time" field.
func (u *MessageUpsertOne) ClearSeenTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.ClearSeenTime()
	})
}

// SetRevokedTime sets the "revoked_time" field.
func (u *MessageUpsertOne) SetRevokedTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.SetRevokedTime(v)
	})
}

// AddRevokedTime adds v to the "revoked_time" field.
func (u *MessageUpsertOne) AddRevokedTime(v int64) *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.AddRevokedTime(v)
	})
}

// UpdateRevokedTime sets the "revoked_time" field to the value that was provided on create.
func (u *MessageUpsertOne) UpdateRevokedTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateRevokedTime()
	})
}

// ClearRevokedTime clears the value of the "revoked_time" field.
func (u *MessageUpsertOne) ClearRevokedTime() *MessageUpsertOne {
	return u.Update(func(s *MessageUpsert) {
		s.ClearRevokedTime()
	})
}

// Exec executes the query.
func (u *MessageUpsertOne) Exec(ctx context.Context) error {
	if len(u.create.conflict) == 0 {
		return errors.New("ent: missing options for MessageCreate.OnConflict")
	}
	return u.create.Exec(ctx)
}

// ExecX is like Exec, but panics if an error occurs.
func (u *MessageUpsertOne) ExecX(ctx context.Context) {
	if err := u.create.Exec(ctx); err != nil {
		panic(err)
	}
}

// Exec executes the UPSERT query and returns the inserted/updated ID.
func (u *MessageUpsertOne) ID(ctx context.Context) (id int64, err error) {
	node, err := u.create.Save(ctx)
	if err != nil {
		return id, err
	}
	return node.ID, nil
}

// IDX is like ID, but panics if an error occurs.
func (u *MessageUpsertOne) IDX(ctx context.Context) int64 {
	id, err := u.ID(ctx)
	if err != nil {
		panic(err)
	}
	return id
}

// MessageCreateBulk is the builder for creating many Message entities in bulk.
type MessageCreateBulk struct {
	config
	builders []*MessageCreate
	conflict []sql.ConflictOption
}

// Save creates the Message entities in the database.
func (mcb *MessageCreateBulk) Save(ctx context.Context) ([]*Message, error) {
	specs := make([]*sqlgraph.CreateSpec, len(mcb.builders))
	nodes := make([]*Message, len(mcb.builders))
	mutators := make([]Mutator, len(mcb.builders))
	for i := range mcb.builders {
		func(i int, root context.Context) {
			builder := mcb.builders[i]
			var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
				mutation, ok := m.(*MessageMutation)
				if !ok {
					return nil, fmt.Errorf("unexpected mutation type %T", m)
				}
				if err := builder.check(); err != nil {
					return nil, err
				}
				builder.mutation = mutation
				nodes[i], specs[i] = builder.createSpec()
				var err error
				if i < len(mutators)-1 {
					_, err = mutators[i+1].Mutate(root, mcb.builders[i+1].mutation)
				} else {
					spec := &sqlgraph.BatchCreateSpec{Nodes: specs}
					spec.OnConflict = mcb.conflict
					// Invoke the actual operation on the latest mutation in the chain.
					if err = sqlgraph.BatchCreate(ctx, mcb.driver, spec); err != nil {
						if sqlgraph.IsConstraintError(err) {
							err = &ConstraintError{msg: err.Error(), wrap: err}
						}
					}
				}
				if err != nil {
					return nil, err
				}
				mutation.id = &nodes[i].ID
				if specs[i].ID.Value != nil && nodes[i].ID == 0 {
					id := specs[i].ID.Value.(int64)
					nodes[i].ID = int64(id)
				}
				mutation.done = true
				return nodes[i], nil
			})
			for i := len(builder.hooks) - 1; i >= 0; i-- {
				mut = builder.hooks[i](mut)
			}
			mutators[i] = mut
		}(i, ctx)
	}
	if len(mutators) > 0 {
		if _, err := mutators[0].Mutate(ctx, mcb.builders[0].mutation); err != nil {
			return nil, err
		}
	}
	return nodes, nil
}

// SaveX is like Save, but panics if an error occurs.
func (mcb *MessageCreateBulk) SaveX(ctx context.Context) []*Message {
	v, err := mcb.Save(ctx)
	if err != nil {
		panic(err)
	}
	return v
}

// Exec executes the query.
func (mcb *MessageCreateBulk) Exec(ctx context.Context) error {
	_, err := mcb.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (mcb *MessageCreateBulk) ExecX(ctx context.Context) {
	if err := mcb.Exec(ctx); err != nil {
		panic(err)
	}
}

// OnConflict allows configuring the `ON CONFLICT` / `ON DUPLICATE KEY` clause
// of the `INSERT` statement. For example:
//
//	client.Message.CreateBulk(builders...).
//		OnConflict(
//			// Update the row with the new values
//			// the was proposed for insertion.
//			sql.ResolveWithNewValues(),
//		).
//		// Override some of the fields with custom
//		// update values.
//		Update(func(u *ent.MessageUpsert) {
//			SetSender(v+v).
//		}).
//		Exec(ctx)
func (mcb *MessageCreateBulk) OnConflict(opts ...sql.ConflictOption) *MessageUpsertBulk {
	mcb.conflict = opts
	return &MessageUpsertBulk{
		create: mcb,
	}
}

// OnConflictColumns calls `OnConflict` and configures the columns
// as conflict target. Using this option is equivalent to using:
//
//	client.Message.Create().
//		OnConflict(sql.ConflictColumns(columns...)).
//		Exec(ctx)
func (mcb *MessageCreateBulk) OnConflictColumns(columns ...string) *MessageUpsertBulk {
	mcb.conflict = append(mcb.conflict, sql.ConflictColumns(columns...))
	return &MessageUpsertBulk{
		create: mcb,
	}
}

// MessageUpsertBulk is the builder for "upsert"-ing
// a bulk of Message nodes.
type MessageUpsertBulk struct {
	create *MessageCreateBulk
}

// UpdateNewValues updates the mutable fields using the new values that
// were set on create. Using this option is equivalent to using:
//
//	client.Message.Create().
//		OnConflict(
//			sql.ResolveWithNewValues(),
//			sql.ResolveWith(func(u *sql.UpdateSet) {
//				u.SetIgnore(message.FieldID)
//			}),
//		).
//		Exec(ctx)
func (u *MessageUpsertBulk) UpdateNewValues() *MessageUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithNewValues())
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(s *sql.UpdateSet) {
		for _, b := range u.create.builders {
			if _, exists := b.mutation.ID(); exists {
				s.SetIgnore(message.FieldID)
			}
		}
	}))
	return u
}

// Ignore sets each column to itself in case of conflict.
// Using this option is equivalent to using:
//
//	client.Message.Create().
//		OnConflict(sql.ResolveWithIgnore()).
//		Exec(ctx)
func (u *MessageUpsertBulk) Ignore() *MessageUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithIgnore())
	return u
}

// DoNothing configures the conflict_action to `DO NOTHING`.
// Supported only by SQLite and PostgreSQL.
func (u *MessageUpsertBulk) DoNothing() *MessageUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.DoNothing())
	return u
}

// Update allows overriding fields `UPDATE` values. See the MessageCreateBulk.OnConflict
// documentation for more info.
func (u *MessageUpsertBulk) Update(set func(*MessageUpsert)) *MessageUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(update *sql.UpdateSet) {
		set(&MessageUpsert{UpdateSet: update})
	}))
	return u
}

// SetSender sets the "sender" field.
func (u *MessageUpsertBulk) SetSender(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetSender(v)
	})
}

// AddSender adds v to the "sender" field.
func (u *MessageUpsertBulk) AddSender(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddSender(v)
	})
}

// UpdateSender sets the "sender" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateSender() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSender()
	})
}

// SetRecipient sets the "recipient" field.
func (u *MessageUpsertBulk) SetRecipient(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetRecipient(v)
	})
}

// AddRecipient adds v to the "recipient" field.
func (u *MessageUpsertBulk) AddRecipient(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddRecipient(v)
	})
}

// UpdateRecipient sets the "recipient" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateRecipient() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateRecipient()
	})
}

// SetType sets the "type" field.
func (u *MessageUpsertBulk) SetType(v int32) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetType(v)
	})
}

// AddType adds v to the "type" field.
func (u *MessageUpsertBulk) AddType(v int32) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddType(v)
	})
}

// UpdateType sets the "type" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateType() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateType()
	})
}

// SetContent sets the "content" field.
func (u *MessageUpsertBulk) SetContent(v string) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetContent(v)
	})
}

// UpdateContent sets the "content" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateContent() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateContent()
	})
}

// SetSentTime sets the "sent_time" field.
func (u *MessageUpsertBulk) SetSentTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetSentTime(v)
	})
}

// AddSentTime adds v to the "sent_time" field.
func (u *MessageUpsertBulk) AddSentTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddSentTime(v)
	})
}

// UpdateSentTime sets the "sent_time" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateSentTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSentTime()
	})
}

// SetTakenTime sets the "taken_time" field.
func (u *MessageUpsertBulk) SetTakenTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetTakenTime(v)
	})
}

// AddTakenTime adds v to the "taken_time" field.
func (u *MessageUpsertBulk) AddTakenTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddTakenTime(v)
	})
}

// UpdateTakenTime sets the "taken_time" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateTakenTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateTakenTime()
	})
}

// ClearTakenTime clears the value of the "taken_time" field.
func (u *MessageUpsertBulk) ClearTakenTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.ClearTakenTime()
	})
}

// SetSeenTime sets the "seen_time" field.
func (u *MessageUpsertBulk) SetSeenTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetSeenTime(v)
	})
}

// AddSeenTime adds v to the "seen_time" field.
func (u *MessageUpsertBulk) AddSeenTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddSeenTime(v)
	})
}

// UpdateSeenTime sets the "seen_time" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateSeenTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateSeenTime()
	})
}

// ClearSeenTime clears the value of the "seen_time" field.
func (u *MessageUpsertBulk) ClearSeenTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.ClearSeenTime()
	})
}

// SetRevokedTime sets the "revoked_time" field.
func (u *MessageUpsertBulk) SetRevokedTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.SetRevokedTime(v)
	})
}

// AddRevokedTime adds v to the "revoked_time" field.
func (u *MessageUpsertBulk) AddRevokedTime(v int64) *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.AddRevokedTime(v)
	})
}

// UpdateRevokedTime sets the "revoked_time" field to the value that was provided on create.
func (u *MessageUpsertBulk) UpdateRevokedTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.UpdateRevokedTime()
	})
}

// ClearRevokedTime clears the value of the "revoked_time" field.
func (u *MessageUpsertBulk) ClearRevokedTime() *MessageUpsertBulk {
	return u.Update(func(s *MessageUpsert) {
		s.ClearRevokedTime()
	})
}

// Exec executes the query.
func (u *MessageUpsertBulk) Exec(ctx context.Context) error {
	for i, b := range u.create.builders {
		if len(b.conflict) != 0 {
			return fmt.Errorf("ent: OnConflict was set for builder %d. Set it on the MessageCreateBulk instead", i)
		}
	}
	if len(u.create.conflict) == 0 {
		return errors.New("ent: missing options for MessageCreateBulk.OnConflict")
	}
	return u.create.Exec(ctx)
}

// ExecX is like Exec, but panics if an error occurs.
func (u *MessageUpsertBulk) ExecX(ctx context.Context) {
	if err := u.create.Exec(ctx); err != nil {
		panic(err)
	}
}
