package aigor.rx;

import aigor.rx.dto.KeyWordsRequest;
import aigor.rx.twitter.dto.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by aigor on 13.10.16.
 */
public class MessageMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    public Optional<KeyWordsRequest> toKeyWordsRequest(String message){
        try {
            return Optional.of(objectMapper.readValue(message, KeyWordsRequest.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<String> toJson(Tweet tweet){
        try {
            return Optional.of(objectMapper.writeValueAsString(tweet));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
