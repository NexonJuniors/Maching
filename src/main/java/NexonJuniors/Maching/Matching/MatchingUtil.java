package NexonJuniors.Maching.Matching;

import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingUtil {
    private final List<MatchingUser> participants; // 파티 찾는 사람 리스트
    private final HashMap<Long, PartyInfo> rooms; // 생성된 채팅방 리스트
    private final ObjectMapper objectMapper;
    private Long roomId = 1L;

    // 파티 생성 요청 시 실행되는 로직
    public void createParty(
            int maximumPeople,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo
    )
    {
        try{
            // JSON 형태의 캐릭터의 모든 정보 자바 객체로 변환
            BasicInfo basicInfo = objectMapper.readValue(strBasicInfo, BasicInfo.class);
            HexaSkillInfo hexaSkillInfo = objectMapper.readValue(strHexaSkillInfo, HexaSkillInfo.class);
            StatInfo statInfo = objectMapper.readValue(strStatInfo, StatInfo.class);
            UnionInfo unionInfo = objectMapper.readValue(strUnionInfo, UnionInfo.class);

            log.info("{} {} {} {}",
                    basicInfo.getCharacterName(),
                    hexaSkillInfo.getCharacterHexaCoreEquipment().get(0).getHexaCoreName(),
                    statInfo.getFinalStat().get(0).getStatName(),
                    unionInfo.getUnion_grade());

            // 캐릭터 정보 통합 객체로 통합
            CharacterInfo characterInfo = new CharacterInfo();
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setHexaSkillInfo(hexaSkillInfo);
            characterInfo.setStatInfo(statInfo);
            characterInfo.setUnionInfo(unionInfo);

            // 파티 정보에 보스이름, 방 최대 인원 수, 방장 캐릭터 정보 추가
            PartyInfo partyInfo = new PartyInfo();
            partyInfo.setBossName(bossName);
            partyInfo.setMaximumPeople(maximumPeople);
            partyInfo.getUsers().add(characterInfo);

            // 새로운 방을 만들어 파티 정보 등록
            rooms.put(roomId++, partyInfo);
            log.info("{} {}", roomId, rooms.size());

//            findUser();
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
    }

    // 매칭 참여 요청 시 실행되는 로직
    public long joinParty(
            String uuId,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo)
    {
        try{
            // JSON 형태의 캐릭터의 모든 정보 자바 객체로 변환
            BasicInfo basicInfo = objectMapper.readValue(strBasicInfo, BasicInfo.class);
            HexaSkillInfo hexaSkillInfo = objectMapper.readValue(strHexaSkillInfo, HexaSkillInfo.class);
            StatInfo statInfo = objectMapper.readValue(strStatInfo, StatInfo.class);
            UnionInfo unionInfo = objectMapper.readValue(strUnionInfo, UnionInfo.class);

            log.info("{} {} {} {}",
                    basicInfo.getCharacterName(),
                    hexaSkillInfo.getCharacterHexaCoreEquipment().get(0).getHexaCoreName(),
                    statInfo.getFinalStat().get(0).getStatName(),
                    unionInfo.getUnion_grade());

            // 캐릭터 정보 통합 객체로 통합
            CharacterInfo characterInfo = new CharacterInfo();
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setHexaSkillInfo(hexaSkillInfo);
            characterInfo.setStatInfo(statInfo);
            characterInfo.setUnionInfo(unionInfo);

            // 매칭 유저 정보에 보스 이름, uuid, 캐릭터 정보 설정
            MatchingUser matchingUser = new MatchingUser();
            matchingUser.setBossName(bossName);
            matchingUser.setUuId(uuId);
            matchingUser.setCharacterInfo(characterInfo);

            for(MatchingUser participant: participants) log.info("참여자: {}", participant.getCharacterInfo().getBasicInfo().getCharacterName());

            // 참여 가능 한 방 중 조건에 맞는 방을 찾음
            return findRoom(matchingUser);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
    }

    // 매칭 대기 큐에 처음 참여 시 기존에 생성되어있는 방들 중 조건에 맞는 방 검색 후 참여
    private long findRoom(MatchingUser matchingUser){
        for(Long roomId : rooms.keySet()){
            PartyInfo partyInfo = rooms.get(roomId);

            // 조건에 맞는 파티가 존재하면 방 번호 반환
            if(partyInfo.getBossName().equals(matchingUser.getBossName())){
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());
                log.info("{}", partyInfo.getUsers().size());
                return roomId;
            }
        }

        // 조건에 맞는 파티가 없으면 매칭 대기 큐에 유저 등록 후 -1 반환
        participants.add(matchingUser);
        return -1;
    }

    // 새로운 방 생성 시 매칭 대기 큐에 있는 유저 중 조건에 맞는 유저 검색 후 매칭
    private void findUser(PartyInfo partyInfo){

    }
}
