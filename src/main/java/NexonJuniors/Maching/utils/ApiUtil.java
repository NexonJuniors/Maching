package NexonJuniors.Maching.utils;


import NexonJuniors.Maching.model.*;
import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        String basicInfoJson = getCharacterInfo(ocid,"/character/basic"); // 캐릭터 basic api호출, date default
        String statInfoJson = getCharacterInfo(ocid,"/character/stat"); // 스텟창 api 호출, dete default
        String unionInfoJson = getCharacterInfo(ocid,"/user/union"); // 유니온 api 호출, dete default
        String hexaSkillInfoJson = getCharacterInfo(ocid,"/character/hexamatrix"); // 헥사스킬 api 호출, dete default

        CharacterInfo characterInfo = new CharacterInfo();
        try{
            // JSON을 객체로 변환
            BasicInfo basicInfo = objectMapper.readValue(basicInfoJson, BasicInfo.class);
            StatInfo statInfo = objectMapper.readValue(statInfoJson, StatInfo.class);
            UnionInfo unionInfo = objectMapper.readValue(unionInfoJson, UnionInfo.class);
            HexaSkillInfo hexaSkillInfo = objectMapper.readValue(hexaSkillInfoJson, HexaSkillInfo.class);

            // CharacterInfo 객체 생성 및 데이터 설정
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setStatInfo(statInfo);
            characterInfo.setUnionInfo(unionInfo);
            characterInfo.setHexaSkillInfo(hexaSkillInfo);
            // 직업 상세 정보 설정
            setCharacterClassDetails(characterInfo);
            /*log.info("[캐릭터검색] | " + characterInfo);*/
            return characterInfo;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
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

    private String getCharacterInfo(String ocid, String path) {
        String response;

        response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.NOT_EXIST_USER)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.just(new ApiException(ApiExceptionCode.DATA_PARSING_ERROR)))
                .bodyToMono(String.class)
                .block();

        return response;
    }

    public void setCharacterClassDetails(CharacterInfo characterInfo) {
        String characterClass = characterInfo.getBasicInfo().getCharacterClass(); // 캐릭터 클래스 정보를 가져옵니다.
        Map<String, List<String>> mapping = CharacterClassMapping.getCharacterClassMapping();

        // 매핑된 정보를 가져옴. 없을 경우 "Unknown" 리스트를 반환.
        List<String> classDetails = mapping.getOrDefault(characterClass, Arrays.asList("Unknown", "Unknown"));

        /*log.info("{}",classDetails); //[3, int]*/
        // 각각의 정보를 설정
        String classMinutesInfo = classDetails.get(0); // 주기 정보
        String classMainStatInfo = classDetails.get(1); // 주 스탯 정보
        /*log.info("{}|{}",classMinutesInfo,classMainStatInfo); //3 | int*/

        // CharacterInfo 객체에 설정
        characterInfo.setClassMinutesInfo(classMinutesInfo);
        characterInfo.setClassMainStatInfo(classMainStatInfo);

        // 백엔드에서 직업명도 저장
        characterInfo.setCharacterClassInfo(characterClass);
        /* 잘나오네 log.info("[직업 상세 설정] | 직업: {} | 주기: {} | 주 스탯: {}", characterClass, classMinutesInfo, classMainStatInfo);*/
    }
}
