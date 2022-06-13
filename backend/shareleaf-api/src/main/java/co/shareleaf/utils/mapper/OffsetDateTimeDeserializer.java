package co.shareleaf.utils.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Bizuwork Melesse
 * created on 2/13/21
 */
public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            LocalDateTime datetime = LocalDateTime.parse(jsonParser.getValueAsString(), OffsetDateTimeSerializer.FORMATTER);
            ZonedDateTime zoned = datetime.atZone(ZoneId.of("UTC"));
            return  zoned.toOffsetDateTime();
        } catch (Exception e) {
            return OffsetDateTime.parse(jsonParser.getValueAsString(), OffsetDateTimeSerializer.FORMATTER);
        }
    }
}