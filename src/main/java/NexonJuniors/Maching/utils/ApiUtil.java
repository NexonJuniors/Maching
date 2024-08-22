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

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;
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
/*            log.info("날짜"+date);*/
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
            /*log.info("[캐릭터 검색] | {} | 특수반지: {}{}", characterInfo, item.getItemName(), item.getSpecialRingLevel())*/

            // 현재 프리셋에 시드링이있나요???
            List<CharacterEquipmentInfo.ItemEquipment> specialRings = findSpecialRings(characterInfo.getCharacterEquipmentInfo().getItemEquipment());
            //  없으면 탐색해드립니다
            if (specialRings.isEmpty()) {
                int[] presetSpecialRingLevels = new int[3];

                checkPresetSpecialRingLevels(characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset1(), presetSpecialRingLevels, 0);
                checkPresetSpecialRingLevels(characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset2(), presetSpecialRingLevels, 1);
                checkPresetSpecialRingLevels(characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset3(), presetSpecialRingLevels, 2);

                int bestPreset = findBestPresetForSpecialRings(presetSpecialRingLevels);
                if (bestPreset != -1) {
                    List<CharacterEquipmentInfo.ItemEquipment> presetRings = switch (bestPreset) {
                        case 1 -> characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset1();
                        case 2 -> characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset2();
                        case 3 -> characterInfo.getCharacterEquipmentInfo().getItemEquipmentPreset3();
                        default -> Collections.emptyList();
                    };

                    logSpecialRings(characterInfo, findSpecialRings(presetRings)); //탐색끝, 클라이언트로 시드링 보내주세요
                } else {
                    characterInfo.getCharacterEquipmentInfo().setUserHasNotSpecialRing(true); //시드링이 아예없는 유저입니다
                    log.info("[캐릭터 검색] | {} | 특수반지가 아예 없음", characterInfo); // 아예 시드링을 못찾았으니까 뱃지 없음 너는
                }
            }else {
                //있으니까 가세요~
                characterInfo.getCharacterEquipmentInfo().setNowUserHasSpecialRing(true); // 시드링을 지금 끼고있는 유저입니다.
                log.info("지금 유저가 시드링을 끼고있음");
                logSpecialRings(characterInfo, specialRings); // 클라이언트로 시드링 보내주세요
            }

            return characterInfo;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
    }

    // 특수반지 레벨이 0 이상인 장비를 필터링하여 리스트로 반환하는 메서드입니다.
    private List<CharacterEquipmentInfo.ItemEquipment> findSpecialRings(List<CharacterEquipmentInfo.ItemEquipment> items) {
        return items.stream()
                .filter(item -> item.getSpecialRingLevel() > 0)
                .collect(Collectors.toList());
    }
    // 프리셋에서 특수스킬 반지 필터링하여 반환
    private void checkPresetSpecialRingLevels(List<CharacterEquipmentInfo.ItemEquipment> presetItems, int[] levels, int index) {
        if (presetItems != null) {
            presetItems.stream()
                    .filter(item -> item.getSpecialRingLevel() > 0)
                    .forEach(item -> {
                        if (item.getSpecialRingLevel() > levels[index]) {
                            levels[index] = item.getSpecialRingLevel();
                        }
                    });
        } else {
            log.warn("프리셋 {}의 장비 리스트가 null입니다.", index + 1); //에러급임
        }
    }
    // 특수반지 레벨이 가장 높은 프리셋을 찾는 메서드
    private int findBestPresetForSpecialRings(int[] levels) {
        int maxLevel = 0;
        int bestPreset = -1;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] > maxLevel) {
                maxLevel = levels[i];
                bestPreset = i + 1;
            }
        }
        return bestPreset;
    }
    // 특수반지의 이름과 레벨을 로깅하고 CharacterInfo에 저장하는 메서드
    private void logSpecialRings(CharacterInfo characterInfo, List<CharacterEquipmentInfo.ItemEquipment> specialRings) {
        if (!specialRings.isEmpty()) {
            CharacterEquipmentInfo.ItemEquipment highestRing = specialRings.stream()
                    .max(Comparator.comparingInt(CharacterEquipmentInfo.ItemEquipment::getSpecialRingLevel))
                    .orElse(null);

            if (highestRing != null) {
                log.info("[캐릭터 검색] | {} | 특수반지: {} 레벨: {}", characterInfo, highestRing.getItemName(), highestRing.getSpecialRingLevel());
                characterInfo.getCharacterEquipmentInfo().setSpecialRingName(highestRing.getItemName());
                characterInfo.getCharacterEquipmentInfo().setSpecialRingLevel(highestRing.getSpecialRingLevel());
            }
        } else {
            log.info("[캐릭터 검색] | {} | 특수반지 없음", characterInfo);
        }
    }

    /*            log.info("API 응답: " + characterEquipmentInfoJson);*/
/*            // 장착중인 장비의 이름 출력 실험 완료
            List<CharacterEquipmentInfo.ItemEquipment> equipmentList = characterEquipmentInfo.getItemEquipment();
            for (CharacterEquipmentInfo.ItemEquipment item : equipmentList) {
                if(item.getSpecialRingLevel()>0){
                    log.info("[캐릭터 장비] | 특수반지:{} 레벨:{}", item.getItemName(), item.getSpecialRingLevel());
                }else{
                    log.info("[캐릭터 장비] | 특수반지 없음");
                }
            }*/

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
