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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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


    public CharacterInfo getCharacter(String characterName, LocalDate date) {
        String ocid = getOcid(characterName); // ocid를 가져오기
        String basicInfoJson = getCharacterInfo(ocid, "/character/basic", null);
        String statInfoJson = getCharacterInfo(ocid, "/character/stat", date);
        String unionInfoJson = getCharacterInfo(ocid, "/user/union", null);
        String hexaSkillInfoJson = getCharacterInfo(ocid, "/character/hexamatrix", null);
        String characterEquipmentInfoJson = getCharacterInfo(ocid, "/character/item-equipment", date);

        CharacterInfo characterInfo = new CharacterInfo();
        try {
            // JSON을 객체로 변환
            BasicInfo basicInfo = objectMapper.readValue(basicInfoJson, BasicInfo.class);
            StatInfo statInfo = objectMapper.readValue(statInfoJson, StatInfo.class);
            UnionInfo unionInfo = objectMapper.readValue(unionInfoJson, UnionInfo.class);
            HexaSkillInfo hexaSkillInfo = objectMapper.readValue(hexaSkillInfoJson, HexaSkillInfo.class);
            CharacterEquipmentInfo characterEquipmentInfo = objectMapper.readValue(characterEquipmentInfoJson, CharacterEquipmentInfo.class);

            // 날짜와 실시간 여부 설정
            if (date == null) {
                statInfo.setSearchDate(LocalDate.now());
                characterEquipmentInfo.setSearchDate(LocalDate.now());
                statInfo.setIsRealTime(true);
                characterEquipmentInfo.setIsRealTime(true);
            } else {
                statInfo.setSearchDate(date);
                characterEquipmentInfo.setSearchDate(date);
                statInfo.setIsRealTime(false);
                characterEquipmentInfo.setIsRealTime(false);
            }

            // CharacterInfo 설정
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setStatInfo(statInfo);
            characterInfo.setUnionInfo(unionInfo);
            characterInfo.setHexaSkillInfo(hexaSkillInfo);
            characterInfo.setCharacterEquipmentInfo(characterEquipmentInfo);

            // 직업 상세 정보 설정
            setCharacterClassDetails(characterInfo);

            // 캐릭터 검색 로그 출력
            log.info("[캐릭터 검색] | " + characterInfo);

/*            log.info("API 응답: " + characterEquipmentInfoJson);*/
            // 장착중인 장비의 이름 출력 실험 완료
/*            List<CharacterEquipmentInfo.ItemEquipment> equipmentList = characterEquipmentInfo.getItemEquipment();
            for (CharacterEquipmentInfo.ItemEquipment item : equipmentList) {
                log.info("장착중인 장비: " + item.getItemName());
            }*/
            return characterInfo;
        } catch (Exception e) {
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

    private String getCharacterInfo(String ocid, String path, LocalDate date) {
        String response;

        // 날짜가 제공되면 쿼리 파라미터에 추가
        response = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    uriBuilder.queryParam("ocid", ocid);
                    if (path.equals("/character/stat") && date != null) {
                        uriBuilder.queryParam("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                    return uriBuilder.build();
                })
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
