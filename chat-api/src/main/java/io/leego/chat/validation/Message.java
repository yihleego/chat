package io.leego.chat.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.MessageType;
import io.leego.chat.dto.AbstractMessageDTO;
import io.leego.chat.dto.AudioDTO;
import io.leego.chat.dto.FileDTO;
import io.leego.chat.dto.LocationDTO;
import io.leego.chat.dto.ShareDTO;
import io.leego.chat.dto.VideoDTO;
import io.leego.chat.dto.VoiceDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Leego Yih
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {Message.Validator.class})
public @interface Message {

    String message() default "{jakarta.validation.constraints.Message.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<Message, AbstractMessageDTO> {
        private static final Logger logger = LoggerFactory.getLogger(Validator.class);
        private final ChatProperties properties;
        private final ObjectMapper mapper;

        public Validator(ChatProperties properties, ObjectMapper mapper) {
            this.properties = properties;
            this.mapper = mapper;
        }

        @Override
        public boolean isValid(AbstractMessageDTO value, ConstraintValidatorContext context) {
            if (value == null || value.getType() == null) {
                return false;
            }
            MessageType type = MessageType.get(value.getType());
            if (type == null) {
                logger.error("Invalid message type: {}", value.getType());
                return false;
            }
            return switch (type) {
                case TEXT -> text(value.getContent());
                case IMAGE -> validate(value, this::image, String[].class);
                case VIDEO -> validate(value, this::video, VideoDTO.class);
                case AUDIO -> validate(value, this::audio, AudioDTO.class);
                case VOICE -> validate(value, this::voice, VoiceDTO.class);
                case FILE -> validate(value, this::file, FileDTO.class);
                case STICKER -> sticker(value.getContent());
                case LOCATION -> validate(value, this::location, LocationDTO.class);
                case SHARE -> validate(value, this::share, ShareDTO.class);
            };
        }

        <T> boolean validate(AbstractMessageDTO dto, Predicate<T> predicate, Class<T> clazz) {
            T o = read(dto.getContent(), clazz);
            boolean b = predicate.test(o);
            if (b) {
                dto.setContent(write(o));
            }
            return b;
        }

        boolean text(String o) {
            int max = properties.getMessage().getContentSize().get(MessageType.TEXT);
            return o != null && o.length() > 0 && o.length() <= max;
        }

        boolean image(String[] o) {
            int max = properties.getMessage().getContentSize().get(MessageType.IMAGE);
            return o != null && o.length > 0 && o.length <= max;
        }

        boolean video(VideoDTO o) {
            return o != null
                    && hasText(o.getUrl())
                    && hasText(o.getFilename())
                    && o.getSize() != null
                    && o.getDuration() != null;
        }

        boolean audio(AudioDTO o) {
            return o != null
                    && hasText(o.getUrl())
                    && hasText(o.getFilename())
                    && o.getSize() != null
                    && o.getDuration() != null;
        }

        boolean voice(VoiceDTO o) {
            return o != null
                    && hasText(o.getUrl())
                    && o.getSize() != null
                    && o.getDuration() != null;
        }

        boolean file(FileDTO o) {
            return o != null
                    && hasText(o.getUrl())
                    && hasText(o.getFilename())
                    && o.getSize() != null;
        }

        boolean sticker(String o) {
            int max = properties.getMessage().getContentSize().get(MessageType.STICKER);
            return o != null && o.length() > 0 && o.length() <= max;
        }

        boolean location(LocationDTO o) {
            return o != null
                    && o.getLongitude() >= -180 && o.getLongitude() <= 180
                    && o.getLatitude() >= 0 && o.getLatitude() <= 90;
        }

        boolean share(ShareDTO o) {
            int max = properties.getMessage().getContentSize().get(MessageType.SHARE);
            return o != null
                    && o.getUrl() != null
                    && o.getUrl().length() > 0
                    && o.getUrl().length() <= max;
        }

        boolean hasText(String s) {
            return s != null && !s.isEmpty();
        }

        <T> T read(String s, Class<T> clazz) {
            try {
                return mapper.readValue(s, clazz);
            } catch (JsonProcessingException e) {
                logger.error("Invalid message format: {}", s, e);
                return null;
            }
        }

        String write(Object o) {
            try {
                return mapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                logger.error("Invalid message format: {}", o, e);
                return null;
            }
        }
    }
}

