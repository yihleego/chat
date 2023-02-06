// Code generated by ent, DO NOT EDIT.

package message

const (
	// Label holds the string label denoting the message type in the database.
	Label = "message"
	// FieldID holds the string denoting the id field in the database.
	FieldID = "id"
	// FieldSender holds the string denoting the sender field in the database.
	FieldSender = "sender"
	// FieldRecipient holds the string denoting the recipient field in the database.
	FieldRecipient = "recipient"
	// FieldType holds the string denoting the type field in the database.
	FieldType = "type"
	// FieldContent holds the string denoting the content field in the database.
	FieldContent = "content"
	// FieldSentTime holds the string denoting the sent_time field in the database.
	FieldSentTime = "sent_time"
	// FieldTakenTime holds the string denoting the taken_time field in the database.
	FieldTakenTime = "taken_time"
	// FieldSeenTime holds the string denoting the seen_time field in the database.
	FieldSeenTime = "seen_time"
	// FieldRevokedTime holds the string denoting the revoked_time field in the database.
	FieldRevokedTime = "revoked_time"
	// Table holds the table name of the message in the database.
	Table = "message"
)

// Columns holds all SQL columns for message fields.
var Columns = []string{
	FieldID,
	FieldSender,
	FieldRecipient,
	FieldType,
	FieldContent,
	FieldSentTime,
	FieldTakenTime,
	FieldSeenTime,
	FieldRevokedTime,
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
