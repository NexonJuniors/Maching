package NexonJuniors.Maching.utils;

import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.CharacterInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiUtil {

    private final Dotenv dotenv;
    private final ObjectMapper objectMapper;
    private WebClient webClient;
    private String API_KEY;
    @PostConstruct
    private void init(){
        API_KEY = dotenv.get("API_KEY");
        webClient = WebClient.builder()
                .defaultHeader("x-nxopen-api-key", API_KEY)
                .baseUrl("https://open.api.nexon.com/maplestory/v1")
                .build();
    }

    public CharacterInfo getCharacter(String characterName) {
        String ocid = getOcid(characterName); // ocid를 제일 먼저 가져오기
        String characterInfoJson = getCharacterInfo(ocid); // 캐릭터 basic api호출, date default

        CharacterInfo characterInfo;

        try{
            characterInfo = objectMapper.readValue(characterInfoJson, CharacterInfo.class); //model에 chracerInfo객체로 생성
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }

        return characterInfo;
    }

    private String getOcid(String characterName) {
        String response;

        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/id")
                        .queryParam("character_name", characterName)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.NOT_EXIST_USER)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.API_SERVER_ERROR)))
                .bodyToMono(String.class)
                .block();

        JsonNode json;
        try {
            json = objectMapper.readTree(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }

        return json.get("ocid").asText();
    }

    private String getCharacterInfo(String ocid) {
        String response;

        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/character/basic")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.NOT_EXIST_USER)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.DATA_PARSING_ERROR)))
                .bodyToMono(String.class)
                .block();

        return response;
    }
}