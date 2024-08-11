package NexonJuniors.Maching.Matching;

import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// 예외 처리 구현 필요
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingUtil {
    private final List<MatchingUser> participants; // 파티 찾는 사람 리스트 ( 매칭 안된 유저 대기 큐 )
    private final HashMap<Long, PartyInfo> rooms; // 생성된 채팅방 리스트
    private final HashSet<String> totalUser; // 전체 이용자 ( 닉네임 ) TODO 이후에 CharacterInfo 형태로 저장해서 캐릭터 정보 전체를 저장해서 처리하는 방법 연구
    private final ObjectMapper objectMapper;
    private Long roomId = 1L;

    // 파티 생성 요청 시 실행되는 로직
    public void createParty(
            int maximumPeople,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo,
            String strMinutesCharacterClassInfo
    ) {
        BasicInfo basicInfo;
        HexaSkillInfo hexaSkillInfo;
        StatInfo statInfo;
        UnionInfo unionInfo;

        try {
            // JSON 형태의 캐릭터의 모든 정보 자바 객체로 변환
            basicInfo = objectMapper.readValue(strBasicInfo, BasicInfo.class);
            hexaSkillInfo = objectMapper.readValue(strHexaSkillInfo, HexaSkillInfo.class);
            statInfo = objectMapper.readValue(strStatInfo, StatInfo.class);
            unionInfo = objectMapper.readValue(strUnionInfo, UnionInfo.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            // TODO MathcingException 클래스로 예외 처리
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }

        // TODO MathcingException 클래스로 예외 처리
        if(totalUser.contains(basicInfo.getCharacterName())) throw new RuntimeException("이미 매칭에 참여중인 유저");

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setCharacterClassInfo(strMinutesCharacterClassInfo);

        // 전체 이용자 리스트에 내 캐릭터 정보 추가
        totalUser.add(characterInfo.getBasicInfo().getCharacterName());

        // 파티 정보에 보스이름, 방 최대 인원 수, 방장 캐릭터 정보 추가 TODO 생성자로 교체 요망
        PartyInfo partyInfo = new PartyInfo();
        partyInfo.setBossName(bossName);
        partyInfo.setMaximumPeople(maximumPeople);
        partyInfo.getUsers().add(characterInfo);

        // 새로운 방을 만들어 파티 정보 등록
        rooms.put(roomId++, partyInfo);
        log.info("{}번방 [{}] 파티원 수: {} 생성완료, 총 파티 수: {}", roomId - 1, partyInfo.getBossName(), partyInfo.getMaximumPeople(), rooms.size());

//            findUser();
    }

    // 매칭 참여 요청 시 실행되는 로직
    public long joinParty(
            String uuId,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo,
            String strMinutesCharacterClassInfo
            )
    {
        BasicInfo basicInfo;
        HexaSkillInfo hexaSkillInfo;
        StatInfo statInfo;
        UnionInfo unionInfo;

        try {
            // JSON 형태의 캐릭터의 모든 정보 자바 객체로 변환
            basicInfo = objectMapper.readValue(strBasicInfo, BasicInfo.class);
            hexaSkillInfo = objectMapper.readValue(strHexaSkillInfo, HexaSkillInfo.class);
            statInfo = objectMapper.readValue(strStatInfo, StatInfo.class);
            unionInfo = objectMapper.readValue(strUnionInfo, UnionInfo.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            // TODO MathcingException 클래스로 예외 처리
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }

        // TODO MathcingException 클래스로 예외 처리
        if(totalUser.contains(basicInfo.getCharacterName())) throw new RuntimeException("이미 매칭에 참여중인 유저");

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setCharacterClassInfo(strMinutesCharacterClassInfo);

        // 전체 이용자 리스트에 내 캐릭터 정보 추가
        totalUser.add(characterInfo.getBasicInfo().getCharacterName());

        // 매칭 유저 정보에 보스 이름, uuid, 캐릭터 정보 설정 TODO 생성자로 교체 요망
        MatchingUser matchingUser = new MatchingUser();
        matchingUser.setBossName(bossName);
        matchingUser.setUuId(uuId);
        matchingUser.setCharacterInfo(characterInfo);

        log.info("{} 님이 매칭 대기 큐에 참여했습니다.", basicInfo.getCharacterName());

        // 참여 가능 한 방 중 조건에 맞는 방을 찾음
        return findRoom(matchingUser);
    }


    // TODO 채팅방 입장 시 채팅 방에 있는 사람들을 저장하고 있는 리스트를 뿌려줌
    public void enterRoom(){

    }

    // 매칭 대기 큐에 처음 참여 시 기존에 생성되어있는 방들 중 조건에 맞는 방 검색 후 참여
    private long findRoom(MatchingUser matchingUser){
        for(Long roomId : rooms.keySet()){
            PartyInfo partyInfo = rooms.get(roomId);

            // 조건에 맞는 파티가 존재하면 방 번호 반환
            if(
                    partyInfo.getBossName().equals(matchingUser.getBossName())
                            && partyInfo.getMaximumPeople() > partyInfo.getUsers().size())
            {
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());
                log.info("{} 님이 {} 번 방에 입장했습니다.", matchingUser.getCharacterInfo().getBasicInfo().getCharacterName(), roomId);
                log.info("{}번 방 파티원 수 : {}",roomId, partyInfo.getUsers().size());
                return roomId;
            }
        }

        // 조건에 맞는 파티가 없으면 매칭 대기 큐에 유저 등록 후 -1 반환
        participants.add(matchingUser);
        return -1;
    }

    // TODO 새로운 방 생성 시 매칭 대기 큐에 있는 유저 중 조건에 맞는 유저 검색 후 매칭
    private void findUser(PartyInfo partyInfo){

    }
}
