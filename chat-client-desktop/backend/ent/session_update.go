// Code generated by ent, DO NOT EDIT.

package ent

import (
	"context"
	"errors"
	"fmt"

	"entgo.io/ent/dialect/sql"
	"entgo.io/ent/dialect/sql/sqlgraph"
	"entgo.io/ent/schema/field"
	"github.com/yihleego/chat/backend/ent/predicate"
	"github.com/yihleego/chat/backend/ent/session"
)

// SessionUpdate is the builder for updating Session entities.
type SessionUpdate struct {
	config
	hooks    []Hook
	mutation *SessionMutation
}

// Where appends a list predicates to the SessionUpdate builder.
func (su *SessionUpdate) Where(ps ...predicate.Session) *SessionUpdate {
	su.mutation.Where(ps...)
	return su
}

// SetUserID sets the "user_id" field.
func (su *SessionUpdate) SetUserID(i int64) *SessionUpdate {
	su.mutation.ResetUserID()
	su.mutation.SetUserID(i)
	return su
}

// AddUserID adds i to the "user_id" field.
func (su *SessionUpdate) AddUserID(i int64) *SessionUpdate {
	su.mutation.AddUserID(i)
	return su
}

// SetUsername sets the "username" field.
func (su *SessionUpdate) SetUsername(s string) *SessionUpdate {
	su.mutation.SetUsername(s)
	return su
}

// SetNickname sets the "nickname" field.
func (su *SessionUpdate) SetNickname(s string) *SessionUpdate {
	su.mutation.SetNickname(s)
	return su
}

// SetAvatar sets the "avatar" field.
func (su *SessionUpdate) SetAvatar(s string) *SessionUpdate {
	su.mutation.SetAvatar(s)
	return su
}

// SetAccessToken sets the "access_token" field.
func (su *SessionUpdate) SetAccessToken(s string) *SessionUpdate {
	su.mutation.SetAccessToken(s)
	return su
}

// SetTokenType sets the "token_type" field.
func (su *SessionUpdate) SetTokenType(s string) *SessionUpdate {
	su.mutation.SetTokenType(s)
	return su
}

// SetExpiresIn sets the "expires_in" field.
func (su *SessionUpdate) SetExpiresIn(i int64) *SessionUpdate {
	su.mutation.ResetExpiresIn()
	su.mutation.SetExpiresIn(i)
	return su
}

// AddExpiresIn adds i to the "expires_in" field.
func (su *SessionUpdate) AddExpiresIn(i int64) *SessionUpdate {
	su.mutation.AddExpiresIn(i)
	return su
}

// SetLoginTime sets the "login_time" field.
func (su *SessionUpdate) SetLoginTime(i int64) *SessionUpdate {
	su.mutation.ResetLoginTime()
	su.mutation.SetLoginTime(i)
	return su
}

// AddLoginTime adds i to the "login_time" field.
func (su *SessionUpdate) AddLoginTime(i int64) *SessionUpdate {
	su.mutation.AddLoginTime(i)
	return su
}

// SetExpiredTime sets the "expired_time" field.
func (su *SessionUpdate) SetExpiredTime(i int64) *SessionUpdate {
	su.mutation.ResetExpiredTime()
	su.mutation.SetExpiredTime(i)
	return su
}

// AddExpiredTime adds i to the "expired_time" field.
func (su *SessionUpdate) AddExpiredTime(i int64) *SessionUpdate {
	su.mutation.AddExpiredTime(i)
	return su
}

// SetRefreshTime sets the "refresh_time" field.
func (su *SessionUpdate) SetRefreshTime(i int64) *SessionUpdate {
	su.mutation.ResetRefreshTime()
	su.mutation.SetRefreshTime(i)
	return su
}

// AddRefreshTime adds i to the "refresh_time" field.
func (su *SessionUpdate) AddRefreshTime(i int64) *SessionUpdate {
	su.mutation.AddRefreshTime(i)
	return su
}

// Mutation returns the SessionMutation object of the builder.
func (su *SessionUpdate) Mutation() *SessionMutation {
	return su.mutation
}

// Save executes the query and returns the number of nodes affected by the update operation.
func (su *SessionUpdate) Save(ctx context.Context) (int, error) {
	var (
		err      error
		affected int
	)
	if len(su.hooks) == 0 {
		affected, err = su.sqlSave(ctx)
	} else {
		var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
			mutation, ok := m.(*SessionMutation)
			if !ok {
				return nil, fmt.Errorf("unexpected mutation type %T", m)
			}
			su.mutation = mutation
			affected, err = su.sqlSave(ctx)
			mutation.done = true
			return affected, err
		})
		for i := len(su.hooks) - 1; i >= 0; i-- {
			if su.hooks[i] == nil {
				return 0, fmt.Errorf("ent: uninitialized hook (forgotten import ent/runtime?)")
			}
			mut = su.hooks[i](mut)
		}
		if _, err := mut.Mutate(ctx, su.mutation); err != nil {
			return 0, err
		}
	}
	return affected, err
}

// SaveX is like Save, but panics if an error occurs.
func (su *SessionUpdate) SaveX(ctx context.Context) int {
	affected, err := su.Save(ctx)
	if err != nil {
		panic(err)
	}
	return affected
}

// Exec executes the query.
func (su *SessionUpdate) Exec(ctx context.Context) error {
	_, err := su.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (su *SessionUpdate) ExecX(ctx context.Context) {
	if err := su.Exec(ctx); err != nil {
		panic(err)
	}
}

func (su *SessionUpdate) sqlSave(ctx context.Context) (n int, err error) {
	_spec := &sqlgraph.UpdateSpec{
		Node: &sqlgraph.NodeSpec{
			Table:   session.Table,
			Columns: session.Columns,
			ID: &sqlgraph.FieldSpec{
				Type:   field.TypeInt64,
				Column: session.FieldID,
			},
		},
	}
	if ps := su.mutation.predicates; len(ps) > 0 {
		_spec.Predicate = func(selector *sql.Selector) {
			for i := range ps {
				ps[i](selector)
			}
		}
	}
	if value, ok := su.mutation.UserID(); ok {
		_spec.SetField(session.FieldUserID, field.TypeInt64, value)
	}
	if value, ok := su.mutation.AddedUserID(); ok {
		_spec.AddField(session.FieldUserID, field.TypeInt64, value)
	}
	if value, ok := su.mutation.Username(); ok {
		_spec.SetField(session.FieldUsername, field.TypeString, value)
	}
	if value, ok := su.mutation.Nickname(); ok {
		_spec.SetField(session.FieldNickname, field.TypeString, value)
	}
	if value, ok := su.mutation.Avatar(); ok {
		_spec.SetField(session.FieldAvatar, field.TypeString, value)
	}
	if value, ok := su.mutation.AccessToken(); ok {
		_spec.SetField(session.FieldAccessToken, field.TypeString, value)
	}
	if value, ok := su.mutation.TokenType(); ok {
		_spec.SetField(session.FieldTokenType, field.TypeString, value)
	}
	if value, ok := su.mutation.ExpiresIn(); ok {
		_spec.SetField(session.FieldExpiresIn, field.TypeInt64, value)
	}
	if value, ok := su.mutation.AddedExpiresIn(); ok {
		_spec.AddField(session.FieldExpiresIn, field.TypeInt64, value)
	}
	if value, ok := su.mutation.LoginTime(); ok {
		_spec.SetField(session.FieldLoginTime, field.TypeInt64, value)
	}
	if value, ok := su.mutation.AddedLoginTime(); ok {
		_spec.AddField(session.FieldLoginTime, field.TypeInt64, value)
	}
	if value, ok := su.mutation.ExpiredTime(); ok {
		_spec.SetField(session.FieldExpiredTime, field.TypeInt64, value)
	}
	if value, ok := su.mutation.AddedExpiredTime(); ok {
		_spec.AddField(session.FieldExpiredTime, field.TypeInt64, value)
	}
	if value, ok := su.mutation.RefreshTime(); ok {
		_spec.SetField(session.FieldRefreshTime, field.TypeInt64, value)
	}
	if value, ok := su.mutation.AddedRefreshTime(); ok {
		_spec.AddField(session.FieldRefreshTime, field.TypeInt64, value)
	}
	if n, err = sqlgraph.UpdateNodes(ctx, su.driver, _spec); err != nil {
		if _, ok := err.(*sqlgraph.NotFoundError); ok {
			err = &NotFoundError{session.Label}
		} else if sqlgraph.IsConstraintError(err) {
			err = &ConstraintError{msg: err.Error(), wrap: err}
		}
		return 0, err
	}
	return n, nil
}

// SessionUpdateOne is the builder for updating a single Session entity.
type SessionUpdateOne struct {
	config
	fields   []string
	hooks    []Hook
	mutation *SessionMutation
}

// SetUserID sets the "user_id" field.
func (suo *SessionUpdateOne) SetUserID(i int64) *SessionUpdateOne {
	suo.mutation.ResetUserID()
	suo.mutation.SetUserID(i)
	return suo
}

// AddUserID adds i to the "user_id" field.
func (suo *SessionUpdateOne) AddUserID(i int64) *SessionUpdateOne {
	suo.mutation.AddUserID(i)
	return suo
}

// SetUsername sets the "username" field.
func (suo *SessionUpdateOne) SetUsername(s string) *SessionUpdateOne {
	suo.mutation.SetUsername(s)
	return suo
}

// SetNickname sets the "nickname" field.
func (suo *SessionUpdateOne) SetNickname(s string) *SessionUpdateOne {
	suo.mutation.SetNickname(s)
	return suo
}

// SetAvatar sets the "avatar" field.
func (suo *SessionUpdateOne) SetAvatar(s string) *SessionUpdateOne {
	suo.mutation.SetAvatar(s)
	return suo
}

// SetAccessToken sets the "access_token" field.
func (suo *SessionUpdateOne) SetAccessToken(s string) *SessionUpdateOne {
	suo.mutation.SetAccessToken(s)
	return suo
}

// SetTokenType sets the "token_type" field.
func (suo *SessionUpdateOne) SetTokenType(s string) *SessionUpdateOne {
	suo.mutation.SetTokenType(s)
	return suo
}

// SetExpiresIn sets the "expires_in" field.
func (suo *SessionUpdateOne) SetExpiresIn(i int64) *SessionUpdateOne {
	suo.mutation.ResetExpiresIn()
	suo.mutation.SetExpiresIn(i)
	return suo
}

// AddExpiresIn adds i to the "expires_in" field.
func (suo *SessionUpdateOne) AddExpiresIn(i int64) *SessionUpdateOne {
	suo.mutation.AddExpiresIn(i)
	return suo
}

// SetLoginTime sets the "login_time" field.
func (suo *SessionUpdateOne) SetLoginTime(i int64) *SessionUpdateOne {
	suo.mutation.ResetLoginTime()
	suo.mutation.SetLoginTime(i)
	return suo
}

// AddLoginTime adds i to the "login_time" field.
func (suo *SessionUpdateOne) AddLoginTime(i int64) *SessionUpdateOne {
	suo.mutation.AddLoginTime(i)
	return suo
}

// SetExpiredTime sets the "expired_time" field.
func (suo *SessionUpdateOne) SetExpiredTime(i int64) *SessionUpdateOne {
	suo.mutation.ResetExpiredTime()
	suo.mutation.SetExpiredTime(i)
	return suo
}

// AddExpiredTime adds i to the "expired_time" field.
func (suo *SessionUpdateOne) AddExpiredTime(i int64) *SessionUpdateOne {
	suo.mutation.AddExpiredTime(i)
	return suo
}

// SetRefreshTime sets the "refresh_time" field.
func (suo *SessionUpdateOne) SetRefreshTime(i int64) *SessionUpdateOne {
	suo.mutation.ResetRefreshTime()
	suo.mutation.SetRefreshTime(i)
	return suo
}

// AddRefreshTime adds i to the "refresh_time" field.
func (suo *SessionUpdateOne) AddRefreshTime(i int64) *SessionUpdateOne {
	suo.mutation.AddRefreshTime(i)
	return suo
}

// Mutation returns the SessionMutation object of the builder.
func (suo *SessionUpdateOne) Mutation() *SessionMutation {
	return suo.mutation
}

// Select allows selecting one or more fields (columns) of the returned entity.
// The default is selecting all fields defined in the entity schema.
func (suo *SessionUpdateOne) Select(field string, fields ...string) *SessionUpdateOne {
	suo.fields = append([]string{field}, fields...)
	return suo
}

// Save executes the query and returns the updated Session entity.
func (suo *SessionUpdateOne) Save(ctx context.Context) (*Session, error) {
	var (
		err  error
		node *Session
	)
	if len(suo.hooks) == 0 {
		node, err = suo.sqlSave(ctx)
	} else {
		var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
			mutation, ok := m.(*SessionMutation)
			if !ok {
				return nil, fmt.Errorf("unexpected mutation type %T", m)
			}
			suo.mutation = mutation
			node, err = suo.sqlSave(ctx)
			mutation.done = true
			return node, err
		})
		for i := len(suo.hooks) - 1; i >= 0; i-- {
			if suo.hooks[i] == nil {
				return nil, fmt.Errorf("ent: uninitialized hook (forgotten import ent/runtime?)")
			}
			mut = suo.hooks[i](mut)
		}
		v, err := mut.Mutate(ctx, suo.mutation)
		if err != nil {
			return nil, err
		}
		nv, ok := v.(*Session)
		if !ok {
			return nil, fmt.Errorf("unexpected node type %T returned from SessionMutation", v)
		}
		node = nv
	}
	return node, err
}

// SaveX is like Save, but panics if an error occurs.
func (suo *SessionUpdateOne) SaveX(ctx context.Context) *Session {
	node, err := suo.Save(ctx)
	if err != nil {
		panic(err)
	}
	return node
}

// Exec executes the query on the entity.
func (suo *SessionUpdateOne) Exec(ctx context.Context) error {
	_, err := suo.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (suo *SessionUpdateOne) ExecX(ctx context.Context) {
	if err := suo.Exec(ctx); err != nil {
		panic(err)
	}
}

func (suo *SessionUpdateOne) sqlSave(ctx context.Context) (_node *Session, err error) {
	_spec := &sqlgraph.UpdateSpec{
		Node: &sqlgraph.NodeSpec{
			Table:   session.Table,
			Columns: session.Columns,
			ID: &sqlgraph.FieldSpec{
				Type:   field.TypeInt64,
				Column: session.FieldID,
			},
		},
	}
	id, ok := suo.mutation.ID()
	if !ok {
		return nil, &ValidationError{Name: "id", err: errors.New(`ent: missing "Session.id" for update`)}
	}
	_spec.Node.ID.Value = id
	if fields := suo.fields; len(fields) > 0 {
		_spec.Node.Columns = make([]string, 0, len(fields))
		_spec.Node.Columns = append(_spec.Node.Columns, session.FieldID)
		for _, f := range fields {
			if !session.ValidColumn(f) {
				return nil, &ValidationError{Name: f, err: fmt.Errorf("ent: invalid field %q for query", f)}
			}
			if f != session.FieldID {
				_spec.Node.Columns = append(_spec.Node.Columns, f)
			}
		}
	}
	if ps := suo.mutation.predicates; len(ps) > 0 {
		_spec.Predicate = func(selector *sql.Selector) {
			for i := range ps {
				ps[i](selector)
			}
		}
	}
	if value, ok := suo.mutation.UserID(); ok {
		_spec.SetField(session.FieldUserID, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.AddedUserID(); ok {
		_spec.AddField(session.FieldUserID, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.Username(); ok {
		_spec.SetField(session.FieldUsername, field.TypeString, value)
	}
	if value, ok := suo.mutation.Nickname(); ok {
		_spec.SetField(session.FieldNickname, field.TypeString, value)
	}
	if value, ok := suo.mutation.Avatar(); ok {
		_spec.SetField(session.FieldAvatar, field.TypeString, value)
	}
	if value, ok := suo.mutation.AccessToken(); ok {
		_spec.SetField(session.FieldAccessToken, field.TypeString, value)
	}
	if value, ok := suo.mutation.TokenType(); ok {
		_spec.SetField(session.FieldTokenType, field.TypeString, value)
	}
	if value, ok := suo.mutation.ExpiresIn(); ok {
		_spec.SetField(session.FieldExpiresIn, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.AddedExpiresIn(); ok {
		_spec.AddField(session.FieldExpiresIn, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.LoginTime(); ok {
		_spec.SetField(session.FieldLoginTime, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.AddedLoginTime(); ok {
		_spec.AddField(session.FieldLoginTime, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.ExpiredTime(); ok {
		_spec.SetField(session.FieldExpiredTime, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.AddedExpiredTime(); ok {
		_spec.AddField(session.FieldExpiredTime, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.RefreshTime(); ok {
		_spec.SetField(session.FieldRefreshTime, field.TypeInt64, value)
	}
	if value, ok := suo.mutation.AddedRefreshTime(); ok {
		_spec.AddField(session.FieldRefreshTime, field.TypeInt64, value)
	}
	_node = &Session{config: suo.config}
	_spec.Assign = _node.assignValues
	_spec.ScanValues = _node.scanValues
	if err = sqlgraph.UpdateNode(ctx, suo.driver, _spec); err != nil {
		if _, ok := err.(*sqlgraph.NotFoundError); ok {
			err = &NotFoundError{session.Label}
		} else if sqlgraph.IsConstraintError(err) {
			err = &ConstraintError{msg: err.Error(), wrap: err}
		}
		return nil, err
	}
	return _node, nil
}
