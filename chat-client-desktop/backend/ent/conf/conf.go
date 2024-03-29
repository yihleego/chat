// Code generated by ent, DO NOT EDIT.

package conf

const (
	// Label holds the string label denoting the conf type in the database.
	Label = "conf"
	// FieldID holds the string denoting the id field in the database.
	FieldID = "id"
	// FieldValue holds the string denoting the value field in the database.
	FieldValue = "value"
	// Table holds the table name of the conf in the database.
	Table = "config"
)

// Columns holds all SQL columns for conf fields.
var Columns = []string{
	FieldID,
	FieldValue,
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
