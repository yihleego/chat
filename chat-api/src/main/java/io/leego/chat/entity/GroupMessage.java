package io.leego.chat.entity;

import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.IOException;
import java.time.Instant;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
@Entity
@Table(name = "chat_group_message")
public class GroupMessage extends BaseEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long groupId;
    @Column(nullable = false, updatable = false)
    private Long sender;
    @Column(nullable = false, updatable = false)
    private Short type;
    @Column(nullable = true, updatable = false)
    private String content;
    @Column(nullable = true, updatable = false)
    @Convert(converter = MentionConverter.class)
    private Mention[] mentions;
    @Column(nullable = false, updatable = true)
    private Short status;
    @Column(nullable = false, updatable = true)
    private Instant eventTime;
    @Column(nullable = false, updatable = false)
    private Instant sentTime;
    @Column(nullable = true, insertable = false)
    private Instant revokedTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @FieldNameConstants
    public static class Mention {
        private Long userId;
        private Integer index;
    }

    @Converter(autoApply = true)
    public static class MentionConverter implements AttributeConverter<Mention[], String> {
        private static final JsonMapper mapper = new JsonMapper();

        @Override
        public String convertToDatabaseColumn(Mention[] mentions) {
            if (mentions == null || mentions.length == 0) {
                return null;
            }
            try {
                return mapper.writeValueAsString(mentions);
            } catch (IOException e) {
                throw new PersistenceException(e);
            }
        }

        @Override
        public Mention[] convertToEntityAttribute(String s) {
            if (s == null || s.isEmpty()) {
                return null;
            }
            try {
                return mapper.readValue(s, Mention[].class);
            } catch (IOException e) {
                throw new PersistenceException(e);
            }
        }
    }
}
