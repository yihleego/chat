// Code generated by ent, DO NOT EDIT.

package contact

const (
	// Label holds the string label denoting the contact type in the database.
	Label = "contact"
	// FieldID holds the string denoting the id field in the database.
	FieldID = "id"
	// FieldUserID holds the string denoting the user_id field in the database.
	FieldUserID = "user_id"
	// FieldUsername holds the string denoting the username field in the database.
	FieldUsername = "username"
	// FieldNickname holds the string denoting the nickname field in the database.
	FieldNickname = "nickname"
	// FieldAvatar holds the string denoting the avatar field in the database.
	FieldAvatar = "avatar"
	// Table holds the table name of the contact in the database.
	Table = "contact"
)

// Columns holds all SQL columns for contact fields.
var Columns = []string{
	FieldID,
	FieldUserID,
	FieldUsername,
	FieldNickname,
	FieldAvatar,
}

// ValidColumn reports if the column name is valid (part of the table columns).
func ValidColumn(column string) bool {
	for i := range Columns {
		if column == Columns[i] {
			return true
		}
	}
	return false
}
