// Code generated by ent, DO NOT EDIT.

package ent

import (
	"context"
	"errors"
	"fmt"

	"entgo.io/ent/dialect/sql"
	"entgo.io/ent/dialect/sql/sqlgraph"
	"entgo.io/ent/schema/field"
	"github.com/yihleego/chat/backend/ent/session"
)

// SessionCreate is the builder for creating a Session entity.
type SessionCreate struct {
	config
	mutation *SessionMutation
	hooks    []Hook
	conflict []sql.ConflictOption
}

// SetUserID sets the "user_id" field.
func (sc *SessionCreate) SetUserID(i int64) *SessionCreate {
	sc.mutation.SetUserID(i)
	return sc
}

// SetUsername sets the "username" field.
func (sc *SessionCreate) SetUsername(s string) *SessionCreate {
	sc.mutation.SetUsername(s)
	return sc
}

// SetNickname sets the "nickname" field.
func (sc *SessionCreate) SetNickname(s string) *SessionCreate {
	sc.mutation.SetNickname(s)
	return sc
}

// SetAvatar sets the "avatar" field.
func (sc *SessionCreate) SetAvatar(s string) *SessionCreate {
	sc.mutation.SetAvatar(s)
	return sc
}

// SetAccessToken sets the "access_token" field.
func (sc *SessionCreate) SetAccessToken(s string) *SessionCreate {
	sc.mutation.SetAccessToken(s)
	return sc
}

// SetTokenType sets the "token_type" field.
func (sc *SessionCreate) SetTokenType(s string) *SessionCreate {
	sc.mutation.SetTokenType(s)
	return sc
}

// SetExpiresIn sets the "expires_in" field.
func (sc *SessionCreate) SetExpiresIn(i int64) *SessionCreate {
	sc.mutation.SetExpiresIn(i)
	return sc
}

// SetLoginTime sets the "login_time" field.
func (sc *SessionCreate) SetLoginTime(i int64) *SessionCreate {
	sc.mutation.SetLoginTime(i)
	return sc
}

// SetExpiredTime sets the "expired_time" field.
func (sc *SessionCreate) SetExpiredTime(i int64) *SessionCreate {
	sc.mutation.SetExpiredTime(i)
	return sc
}

// SetRefreshTime sets the "refresh_time" field.
func (sc *SessionCreate) SetRefreshTime(i int64) *SessionCreate {
	sc.mutation.SetRefreshTime(i)
	return sc
}

// SetID sets the "id" field.
func (sc *SessionCreate) SetID(i int64) *SessionCreate {
	sc.mutation.SetID(i)
	return sc
}

// Mutation returns the SessionMutation object of the builder.
func (sc *SessionCreate) Mutation() *SessionMutation {
	return sc.mutation
}

// Save creates the Session in the database.
func (sc *SessionCreate) Save(ctx context.Context) (*Session, error) {
	var (
		err  error
		node *Session
	)
	if len(sc.hooks) == 0 {
		if err = sc.check(); err != nil {
			return nil, err
		}
		node, err = sc.sqlSave(ctx)
	} else {
		var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
			mutation, ok := m.(*SessionMutation)
			if !ok {
				return nil, fmt.Errorf("unexpected mutation type %T", m)
			}
			if err = sc.check(); err != nil {
				return nil, err
			}
			sc.mutation = mutation
			if node, err = sc.sqlSave(ctx); err != nil {
				return nil, err
			}
			mutation.id = &node.ID
			mutation.done = true
			return node, err
		})
		for i := len(sc.hooks) - 1; i >= 0; i-- {
			if sc.hooks[i] == nil {
				return nil, fmt.Errorf("ent: uninitialized hook (forgotten import ent/runtime?)")
			}
			mut = sc.hooks[i](mut)
		}
		v, err := mut.Mutate(ctx, sc.mutation)
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

// SaveX calls Save and panics if Save returns an error.
func (sc *SessionCreate) SaveX(ctx context.Context) *Session {
	v, err := sc.Save(ctx)
	if err != nil {
		panic(err)
	}
	return v
}

// Exec executes the query.
func (sc *SessionCreate) Exec(ctx context.Context) error {
	_, err := sc.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (sc *SessionCreate) ExecX(ctx context.Context) {
	if err := sc.Exec(ctx); err != nil {
		panic(err)
	}
}

// check runs all checks and user-defined validators on the builder.
func (sc *SessionCreate) check() error {
	if _, ok := sc.mutation.UserID(); !ok {
		return &ValidationError{Name: "user_id", err: errors.New(`ent: missing required field "Session.user_id"`)}
	}
	if _, ok := sc.mutation.Username(); !ok {
		return &ValidationError{Name: "username", err: errors.New(`ent: missing required field "Session.username"`)}
	}
	if _, ok := sc.mutation.Nickname(); !ok {
		return &ValidationError{Name: "nickname", err: errors.New(`ent: missing required field "Session.nickname"`)}
	}
	if _, ok := sc.mutation.Avatar(); !ok {
		return &ValidationError{Name: "avatar", err: errors.New(`ent: missing required field "Session.avatar"`)}
	}
	if _, ok := sc.mutation.AccessToken(); !ok {
		return &ValidationError{Name: "access_token", err: errors.New(`ent: missing required field "Session.access_token"`)}
	}
	if _, ok := sc.mutation.TokenType(); !ok {
		return &ValidationError{Name: "token_type", err: errors.New(`ent: missing required field "Session.token_type"`)}
	}
	if _, ok := sc.mutation.ExpiresIn(); !ok {
		return &ValidationError{Name: "expires_in", err: errors.New(`ent: missing required field "Session.expires_in"`)}
	}
	if _, ok := sc.mutation.LoginTime(); !ok {
		return &ValidationError{Name: "login_time", err: errors.New(`ent: missing required field "Session.login_time"`)}
	}
	if _, ok := sc.mutation.ExpiredTime(); !ok {
		return &ValidationError{Name: "expired_time", err: errors.New(`ent: missing required field "Session.expired_time"`)}
	}
	if _, ok := sc.mutation.RefreshTime(); !ok {
		return &ValidationError{Name: "refresh_time", err: errors.New(`ent: missing required field "Session.refresh_time"`)}
	}
	return nil
}

func (sc *SessionCreate) sqlSave(ctx context.Context) (*Session, error) {
	_node, _spec := sc.createSpec()
	if err := sqlgraph.CreateNode(ctx, sc.driver, _spec); err != nil {
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

func (sc *SessionCreate) createSpec() (*Session, *sqlgraph.CreateSpec) {
	var (
		_node = &Session{config: sc.config}
		_spec = &sqlgraph.CreateSpec{
			Table: session.Table,
			ID: &sqlgraph.FieldSpec{
				Type:   field.TypeInt64,
				Column: session.FieldID,
			},
		}
	)
	_spec.OnConflict = sc.conflict
	if id, ok := sc.mutation.ID(); ok {
		_node.ID = id
		_spec.ID.Value = id
	}
	if value, ok := sc.mutation.UserID(); ok {
		_spec.SetField(session.FieldUserID, field.TypeInt64, value)
		_node.UserID = value
	}
	if value, ok := sc.mutation.Username(); ok {
		_spec.SetField(session.FieldUsername, field.TypeString, value)
		_node.Username = value
	}
	if value, ok := sc.mutation.Nickname(); ok {
		_spec.SetField(session.FieldNickname, field.TypeString, value)
		_node.Nickname = value
	}
	if value, ok := sc.mutation.Avatar(); ok {
		_spec.SetField(session.FieldAvatar, field.TypeString, value)
		_node.Avatar = value
	}
	if value, ok := sc.mutation.AccessToken(); ok {
		_spec.SetField(session.FieldAccessToken, field.TypeString, value)
		_node.AccessToken = value
	}
	if value, ok := sc.mutation.TokenType(); ok {
		_spec.SetField(session.FieldTokenType, field.TypeString, value)
		_node.TokenType = value
	}
	if value, ok := sc.mutation.ExpiresIn(); ok {
		_spec.SetField(session.FieldExpiresIn, field.TypeInt64, value)
		_node.ExpiresIn = value
	}
	if value, ok := sc.mutation.LoginTime(); ok {
		_spec.SetField(session.FieldLoginTime, field.TypeInt64, value)
		_node.LoginTime = value
	}
	if value, ok := sc.mutation.ExpiredTime(); ok {
		_spec.SetField(session.FieldExpiredTime, field.TypeInt64, value)
		_node.ExpiredTime = value
	}
	if value, ok := sc.mutation.RefreshTime(); ok {
		_spec.SetField(session.FieldRefreshTime, field.TypeInt64, value)
		_node.RefreshTime = value
	}
	return _node, _spec
}

// OnConflict allows configuring the `ON CONFLICT` / `ON DUPLICATE KEY` clause
// of the `INSERT` statement. For example:
//
//	client.Session.Create().
//		SetUserID(v).
//		OnConflict(
//			// Update the row with the new values
//			// the was proposed for insertion.
//			sql.ResolveWithNewValues(),
//		).
//		// Override some of the fields with custom
//		// update values.
//		Update(func(u *ent.SessionUpsert) {
//			SetUserID(v+v).
//		}).
//		Exec(ctx)
func (sc *SessionCreate) OnConflict(opts ...sql.ConflictOption) *SessionUpsertOne {
	sc.conflict = opts
	return &SessionUpsertOne{
		create: sc,
	}
}

// OnConflictColumns calls `OnConflict` and configures the columns
// as conflict target. Using this option is equivalent to using:
//
//	client.Session.Create().
//		OnConflict(sql.ConflictColumns(columns...)).
//		Exec(ctx)
func (sc *SessionCreate) OnConflictColumns(columns ...string) *SessionUpsertOne {
	sc.conflict = append(sc.conflict, sql.ConflictColumns(columns...))
	return &SessionUpsertOne{
		create: sc,
	}
}

type (
	// SessionUpsertOne is the builder for "upsert"-ing
	//  one Session node.
	SessionUpsertOne struct {
		create *SessionCreate
	}

	// SessionUpsert is the "OnConflict" setter.
	SessionUpsert struct {
		*sql.UpdateSet
	}
)

// SetUserID sets the "user_id" field.
func (u *SessionUpsert) SetUserID(v int64) *SessionUpsert {
	u.Set(session.FieldUserID, v)
	return u
}

// UpdateUserID sets the "user_id" field to the value that was provided on create.
func (u *SessionUpsert) UpdateUserID() *SessionUpsert {
	u.SetExcluded(session.FieldUserID)
	return u
}

// AddUserID adds v to the "user_id" field.
func (u *SessionUpsert) AddUserID(v int64) *SessionUpsert {
	u.Add(session.FieldUserID, v)
	return u
}

// SetUsername sets the "username" field.
func (u *SessionUpsert) SetUsername(v string) *SessionUpsert {
	u.Set(session.FieldUsername, v)
	return u
}

// UpdateUsername sets the "username" field to the value that was provided on create.
func (u *SessionUpsert) UpdateUsername() *SessionUpsert {
	u.SetExcluded(session.FieldUsername)
	return u
}

// SetNickname sets the "nickname" field.
func (u *SessionUpsert) SetNickname(v string) *SessionUpsert {
	u.Set(session.FieldNickname, v)
	return u
}

// UpdateNickname sets the "nickname" field to the value that was provided on create.
func (u *SessionUpsert) UpdateNickname() *SessionUpsert {
	u.SetExcluded(session.FieldNickname)
	return u
}

// SetAvatar sets the "avatar" field.
func (u *SessionUpsert) SetAvatar(v string) *SessionUpsert {
	u.Set(session.FieldAvatar, v)
	return u
}

// UpdateAvatar sets the "avatar" field to the value that was provided on create.
func (u *SessionUpsert) UpdateAvatar() *SessionUpsert {
	u.SetExcluded(session.FieldAvatar)
	return u
}

// SetAccessToken sets the "access_token" field.
func (u *SessionUpsert) SetAccessToken(v string) *SessionUpsert {
	u.Set(session.FieldAccessToken, v)
	return u
}

// UpdateAccessToken sets the "access_token" field to the value that was provided on create.
func (u *SessionUpsert) UpdateAccessToken() *SessionUpsert {
	u.SetExcluded(session.FieldAccessToken)
	return u
}

// SetTokenType sets the "token_type" field.
func (u *SessionUpsert) SetTokenType(v string) *SessionUpsert {
	u.Set(session.FieldTokenType, v)
	return u
}

// UpdateTokenType sets the "token_type" field to the value that was provided on create.
func (u *SessionUpsert) UpdateTokenType() *SessionUpsert {
	u.SetExcluded(session.FieldTokenType)
	return u
}

// SetExpiresIn sets the "expires_in" field.
func (u *SessionUpsert) SetExpiresIn(v int64) *SessionUpsert {
	u.Set(session.FieldExpiresIn, v)
	return u
}

// UpdateExpiresIn sets the "expires_in" field to the value that was provided on create.
func (u *SessionUpsert) UpdateExpiresIn() *SessionUpsert {
	u.SetExcluded(session.FieldExpiresIn)
	return u
}

// AddExpiresIn adds v to the "expires_in" field.
func (u *SessionUpsert) AddExpiresIn(v int64) *SessionUpsert {
	u.Add(session.FieldExpiresIn, v)
	return u
}

// SetLoginTime sets the "login_time" field.
func (u *SessionUpsert) SetLoginTime(v int64) *SessionUpsert {
	u.Set(session.FieldLoginTime, v)
	return u
}

// UpdateLoginTime sets the "login_time" field to the value that was provided on create.
func (u *SessionUpsert) UpdateLoginTime() *SessionUpsert {
	u.SetExcluded(session.FieldLoginTime)
	return u
}

// AddLoginTime adds v to the "login_time" field.
func (u *SessionUpsert) AddLoginTime(v int64) *SessionUpsert {
	u.Add(session.FieldLoginTime, v)
	return u
}

// SetExpiredTime sets the "expired_time" field.
func (u *SessionUpsert) SetExpiredTime(v int64) *SessionUpsert {
	u.Set(session.FieldExpiredTime, v)
	return u
}

// UpdateExpiredTime sets the "expired_time" field to the value that was provided on create.
func (u *SessionUpsert) UpdateExpiredTime() *SessionUpsert {
	u.SetExcluded(session.FieldExpiredTime)
	return u
}

// AddExpiredTime adds v to the "expired_time" field.
func (u *SessionUpsert) AddExpiredTime(v int64) *SessionUpsert {
	u.Add(session.FieldExpiredTime, v)
	return u
}

// SetRefreshTime sets the "refresh_time" field.
func (u *SessionUpsert) SetRefreshTime(v int64) *SessionUpsert {
	u.Set(session.FieldRefreshTime, v)
	return u
}

// UpdateRefreshTime sets the "refresh_time" field to the value that was provided on create.
func (u *SessionUpsert) UpdateRefreshTime() *SessionUpsert {
	u.SetExcluded(session.FieldRefreshTime)
	return u
}

// AddRefreshTime adds v to the "refresh_time" field.
func (u *SessionUpsert) AddRefreshTime(v int64) *SessionUpsert {
	u.Add(session.FieldRefreshTime, v)
	return u
}

// UpdateNewValues updates the mutable fields using the new values that were set on create except the ID field.
// Using this option is equivalent to using:
//
//	client.Session.Create().
//		OnConflict(
//			sql.ResolveWithNewValues(),
//			sql.ResolveWith(func(u *sql.UpdateSet) {
//				u.SetIgnore(session.FieldID)
//			}),
//		).
//		Exec(ctx)
func (u *SessionUpsertOne) UpdateNewValues() *SessionUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithNewValues())
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(s *sql.UpdateSet) {
		if _, exists := u.create.mutation.ID(); exists {
			s.SetIgnore(session.FieldID)
		}
	}))
	return u
}

// Ignore sets each column to itself in case of conflict.
// Using this option is equivalent to using:
//
//	client.Session.Create().
//	    OnConflict(sql.ResolveWithIgnore()).
//	    Exec(ctx)
func (u *SessionUpsertOne) Ignore() *SessionUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithIgnore())
	return u
}

// DoNothing configures the conflict_action to `DO NOTHING`.
// Supported only by SQLite and PostgreSQL.
func (u *SessionUpsertOne) DoNothing() *SessionUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.DoNothing())
	return u
}

// Update allows overriding fields `UPDATE` values. See the SessionCreate.OnConflict
// documentation for more info.
func (u *SessionUpsertOne) Update(set func(*SessionUpsert)) *SessionUpsertOne {
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(update *sql.UpdateSet) {
		set(&SessionUpsert{UpdateSet: update})
	}))
	return u
}

// SetUserID sets the "user_id" field.
func (u *SessionUpsertOne) SetUserID(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetUserID(v)
	})
}

// AddUserID adds v to the "user_id" field.
func (u *SessionUpsertOne) AddUserID(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.AddUserID(v)
	})
}

// UpdateUserID sets the "user_id" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateUserID() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateUserID()
	})
}

// SetUsername sets the "username" field.
func (u *SessionUpsertOne) SetUsername(v string) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetUsername(v)
	})
}

// UpdateUsername sets the "username" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateUsername() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateUsername()
	})
}

// SetNickname sets the "nickname" field.
func (u *SessionUpsertOne) SetNickname(v string) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetNickname(v)
	})
}

// UpdateNickname sets the "nickname" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateNickname() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateNickname()
	})
}

// SetAvatar sets the "avatar" field.
func (u *SessionUpsertOne) SetAvatar(v string) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetAvatar(v)
	})
}

// UpdateAvatar sets the "avatar" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateAvatar() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateAvatar()
	})
}

// SetAccessToken sets the "access_token" field.
func (u *SessionUpsertOne) SetAccessToken(v string) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetAccessToken(v)
	})
}

// UpdateAccessToken sets the "access_token" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateAccessToken() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateAccessToken()
	})
}

// SetTokenType sets the "token_type" field.
func (u *SessionUpsertOne) SetTokenType(v string) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetTokenType(v)
	})
}

// UpdateTokenType sets the "token_type" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateTokenType() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateTokenType()
	})
}

// SetExpiresIn sets the "expires_in" field.
func (u *SessionUpsertOne) SetExpiresIn(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetExpiresIn(v)
	})
}

// AddExpiresIn adds v to the "expires_in" field.
func (u *SessionUpsertOne) AddExpiresIn(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.AddExpiresIn(v)
	})
}

// UpdateExpiresIn sets the "expires_in" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateExpiresIn() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateExpiresIn()
	})
}

// SetLoginTime sets the "login_time" field.
func (u *SessionUpsertOne) SetLoginTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetLoginTime(v)
	})
}

// AddLoginTime adds v to the "login_time" field.
func (u *SessionUpsertOne) AddLoginTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.AddLoginTime(v)
	})
}

// UpdateLoginTime sets the "login_time" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateLoginTime() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateLoginTime()
	})
}

// SetExpiredTime sets the "expired_time" field.
func (u *SessionUpsertOne) SetExpiredTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetExpiredTime(v)
	})
}

// AddExpiredTime adds v to the "expired_time" field.
func (u *SessionUpsertOne) AddExpiredTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.AddExpiredTime(v)
	})
}

// UpdateExpiredTime sets the "expired_time" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateExpiredTime() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateExpiredTime()
	})
}

// SetRefreshTime sets the "refresh_time" field.
func (u *SessionUpsertOne) SetRefreshTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.SetRefreshTime(v)
	})
}

// AddRefreshTime adds v to the "refresh_time" field.
func (u *SessionUpsertOne) AddRefreshTime(v int64) *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.AddRefreshTime(v)
	})
}

// UpdateRefreshTime sets the "refresh_time" field to the value that was provided on create.
func (u *SessionUpsertOne) UpdateRefreshTime() *SessionUpsertOne {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateRefreshTime()
	})
}

// Exec executes the query.
func (u *SessionUpsertOne) Exec(ctx context.Context) error {
	if len(u.create.conflict) == 0 {
		return errors.New("ent: missing options for SessionCreate.OnConflict")
	}
	return u.create.Exec(ctx)
}

// ExecX is like Exec, but panics if an error occurs.
func (u *SessionUpsertOne) ExecX(ctx context.Context) {
	if err := u.create.Exec(ctx); err != nil {
		panic(err)
	}
}

// Exec executes the UPSERT query and returns the inserted/updated ID.
func (u *SessionUpsertOne) ID(ctx context.Context) (id int64, err error) {
	node, err := u.create.Save(ctx)
	if err != nil {
		return id, err
	}
	return node.ID, nil
}

// IDX is like ID, but panics if an error occurs.
func (u *SessionUpsertOne) IDX(ctx context.Context) int64 {
	id, err := u.ID(ctx)
	if err != nil {
		panic(err)
	}
	return id
}

// SessionCreateBulk is the builder for creating many Session entities in bulk.
type SessionCreateBulk struct {
	config
	builders []*SessionCreate
	conflict []sql.ConflictOption
}

// Save creates the Session entities in the database.
func (scb *SessionCreateBulk) Save(ctx context.Context) ([]*Session, error) {
	specs := make([]*sqlgraph.CreateSpec, len(scb.builders))
	nodes := make([]*Session, len(scb.builders))
	mutators := make([]Mutator, len(scb.builders))
	for i := range scb.builders {
		func(i int, root context.Context) {
			builder := scb.builders[i]
			var mut Mutator = MutateFunc(func(ctx context.Context, m Mutation) (Value, error) {
				mutation, ok := m.(*SessionMutation)
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
					_, err = mutators[i+1].Mutate(root, scb.builders[i+1].mutation)
				} else {
					spec := &sqlgraph.BatchCreateSpec{Nodes: specs}
					spec.OnConflict = scb.conflict
					// Invoke the actual operation on the latest mutation in the chain.
					if err = sqlgraph.BatchCreate(ctx, scb.driver, spec); err != nil {
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
		if _, err := mutators[0].Mutate(ctx, scb.builders[0].mutation); err != nil {
			return nil, err
		}
	}
	return nodes, nil
}

// SaveX is like Save, but panics if an error occurs.
func (scb *SessionCreateBulk) SaveX(ctx context.Context) []*Session {
	v, err := scb.Save(ctx)
	if err != nil {
		panic(err)
	}
	return v
}

// Exec executes the query.
func (scb *SessionCreateBulk) Exec(ctx context.Context) error {
	_, err := scb.Save(ctx)
	return err
}

// ExecX is like Exec, but panics if an error occurs.
func (scb *SessionCreateBulk) ExecX(ctx context.Context) {
	if err := scb.Exec(ctx); err != nil {
		panic(err)
	}
}

// OnConflict allows configuring the `ON CONFLICT` / `ON DUPLICATE KEY` clause
// of the `INSERT` statement. For example:
//
//	client.Session.CreateBulk(builders...).
//		OnConflict(
//			// Update the row with the new values
//			// the was proposed for insertion.
//			sql.ResolveWithNewValues(),
//		).
//		// Override some of the fields with custom
//		// update values.
//		Update(func(u *ent.SessionUpsert) {
//			SetUserID(v+v).
//		}).
//		Exec(ctx)
func (scb *SessionCreateBulk) OnConflict(opts ...sql.ConflictOption) *SessionUpsertBulk {
	scb.conflict = opts
	return &SessionUpsertBulk{
		create: scb,
	}
}

// OnConflictColumns calls `OnConflict` and configures the columns
// as conflict target. Using this option is equivalent to using:
//
//	client.Session.Create().
//		OnConflict(sql.ConflictColumns(columns...)).
//		Exec(ctx)
func (scb *SessionCreateBulk) OnConflictColumns(columns ...string) *SessionUpsertBulk {
	scb.conflict = append(scb.conflict, sql.ConflictColumns(columns...))
	return &SessionUpsertBulk{
		create: scb,
	}
}

// SessionUpsertBulk is the builder for "upsert"-ing
// a bulk of Session nodes.
type SessionUpsertBulk struct {
	create *SessionCreateBulk
}

// UpdateNewValues updates the mutable fields using the new values that
// were set on create. Using this option is equivalent to using:
//
//	client.Session.Create().
//		OnConflict(
//			sql.ResolveWithNewValues(),
//			sql.ResolveWith(func(u *sql.UpdateSet) {
//				u.SetIgnore(session.FieldID)
//			}),
//		).
//		Exec(ctx)
func (u *SessionUpsertBulk) UpdateNewValues() *SessionUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithNewValues())
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(s *sql.UpdateSet) {
		for _, b := range u.create.builders {
			if _, exists := b.mutation.ID(); exists {
				s.SetIgnore(session.FieldID)
			}
		}
	}))
	return u
}

// Ignore sets each column to itself in case of conflict.
// Using this option is equivalent to using:
//
//	client.Session.Create().
//		OnConflict(sql.ResolveWithIgnore()).
//		Exec(ctx)
func (u *SessionUpsertBulk) Ignore() *SessionUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWithIgnore())
	return u
}

// DoNothing configures the conflict_action to `DO NOTHING`.
// Supported only by SQLite and PostgreSQL.
func (u *SessionUpsertBulk) DoNothing() *SessionUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.DoNothing())
	return u
}

// Update allows overriding fields `UPDATE` values. See the SessionCreateBulk.OnConflict
// documentation for more info.
func (u *SessionUpsertBulk) Update(set func(*SessionUpsert)) *SessionUpsertBulk {
	u.create.conflict = append(u.create.conflict, sql.ResolveWith(func(update *sql.UpdateSet) {
		set(&SessionUpsert{UpdateSet: update})
	}))
	return u
}

// SetUserID sets the "user_id" field.
func (u *SessionUpsertBulk) SetUserID(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetUserID(v)
	})
}

// AddUserID adds v to the "user_id" field.
func (u *SessionUpsertBulk) AddUserID(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.AddUserID(v)
	})
}

// UpdateUserID sets the "user_id" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateUserID() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateUserID()
	})
}

// SetUsername sets the "username" field.
func (u *SessionUpsertBulk) SetUsername(v string) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetUsername(v)
	})
}

// UpdateUsername sets the "username" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateUsername() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateUsername()
	})
}

// SetNickname sets the "nickname" field.
func (u *SessionUpsertBulk) SetNickname(v string) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetNickname(v)
	})
}

// UpdateNickname sets the "nickname" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateNickname() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateNickname()
	})
}

// SetAvatar sets the "avatar" field.
func (u *SessionUpsertBulk) SetAvatar(v string) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetAvatar(v)
	})
}

// UpdateAvatar sets the "avatar" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateAvatar() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateAvatar()
	})
}

// SetAccessToken sets the "access_token" field.
func (u *SessionUpsertBulk) SetAccessToken(v string) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetAccessToken(v)
	})
}

// UpdateAccessToken sets the "access_token" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateAccessToken() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateAccessToken()
	})
}

// SetTokenType sets the "token_type" field.
func (u *SessionUpsertBulk) SetTokenType(v string) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetTokenType(v)
	})
}

// UpdateTokenType sets the "token_type" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateTokenType() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateTokenType()
	})
}

// SetExpiresIn sets the "expires_in" field.
func (u *SessionUpsertBulk) SetExpiresIn(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetExpiresIn(v)
	})
}

// AddExpiresIn adds v to the "expires_in" field.
func (u *SessionUpsertBulk) AddExpiresIn(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.AddExpiresIn(v)
	})
}

// UpdateExpiresIn sets the "expires_in" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateExpiresIn() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateExpiresIn()
	})
}

// SetLoginTime sets the "login_time" field.
func (u *SessionUpsertBulk) SetLoginTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetLoginTime(v)
	})
}

// AddLoginTime adds v to the "login_time" field.
func (u *SessionUpsertBulk) AddLoginTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.AddLoginTime(v)
	})
}

// UpdateLoginTime sets the "login_time" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateLoginTime() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateLoginTime()
	})
}

// SetExpiredTime sets the "expired_time" field.
func (u *SessionUpsertBulk) SetExpiredTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetExpiredTime(v)
	})
}

// AddExpiredTime adds v to the "expired_time" field.
func (u *SessionUpsertBulk) AddExpiredTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.AddExpiredTime(v)
	})
}

// UpdateExpiredTime sets the "expired_time" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateExpiredTime() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateExpiredTime()
	})
}

// SetRefreshTime sets the "refresh_time" field.
func (u *SessionUpsertBulk) SetRefreshTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.SetRefreshTime(v)
	})
}

// AddRefreshTime adds v to the "refresh_time" field.
func (u *SessionUpsertBulk) AddRefreshTime(v int64) *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.AddRefreshTime(v)
	})
}

// UpdateRefreshTime sets the "refresh_time" field to the value that was provided on create.
func (u *SessionUpsertBulk) UpdateRefreshTime() *SessionUpsertBulk {
	return u.Update(func(s *SessionUpsert) {
		s.UpdateRefreshTime()
	})
}

// Exec executes the query.
func (u *SessionUpsertBulk) Exec(ctx context.Context) error {
	for i, b := range u.create.builders {
		if len(b.conflict) != 0 {
			return fmt.Errorf("ent: OnConflict was set for builder %d. Set it on the SessionCreateBulk instead", i)
		}
	}
	if len(u.create.conflict) == 0 {
		return errors.New("ent: missing options for SessionCreateBulk.OnConflict")
	}
	return u.create.Exec(ctx)
}

// ExecX is like Exec, but panics if an error occurs.
func (u *SessionUpsertBulk) ExecX(ctx context.Context) {
	if err := u.create.Exec(ctx); err != nil {
		panic(err)
	}
}
