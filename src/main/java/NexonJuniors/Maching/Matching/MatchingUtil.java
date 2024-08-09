package NexonJuniors.Maching.Matching;

import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingUtil {
    private final List<CharacterInfo> participants; // 파티 찾는 사람 리스트
    private final HashMap<Long, PartyInfo> rooms; // 생성된 채팅방 리스트
    private final ObjectMapper objectMapper;
    private Long roomId = 1L;

    // 파티 생성 요청 시 실행되는 로직
    public void createParty(String strPartyInfo)
    {
        try{
            PartyInfo partyInfo = objectMapper.readValue(strPartyInfo, PartyInfo.class);

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
    public void joinParty(String strBasicInfo,
                          String strHexaSkillInfo,
                          String strStatInfo,
                          String strUnionInfo)
    {
        try{
            BasicInfo basicInfo = objectMapper.readValue(strBasicInfo, BasicInfo.class);
            HexaSkillInfo hexaSkillInfo = objectMapper.readValue(strHexaSkillInfo, HexaSkillInfo.class);
            StatInfo statInfo = objectMapper.readValue(strStatInfo, StatInfo.class);
            UnionInfo unionInfo = objectMapper.readValue(strUnionInfo, UnionInfo.class);

            log.info("{} {} {} {}",
                    basicInfo.getCharacterName(),
                    hexaSkillInfo.getCharacterHexaCoreEquipment().get(0).getHexaCoreName(),
                    statInfo.getFinalStat().get(0).getStatName(),
                    unionInfo.getUnion_grade());

            CharacterInfo characterInfo = new CharacterInfo();
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setHexaSkillInfo(hexaSkillInfo);
            characterInfo.setStatInfo(statInfo);
            characterInfo.setUnionInfo(unionInfo);

            participants.add(characterInfo);

//            findRoom(characterInfo);
            for(CharacterInfo participant: participants) log.info("참여자: {}", participant.getBasicInfo().getCharacterName());
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
    }

    // 매칭 대기 큐에 처음 참여 시 기존에 생성되어있는 방들 중 조건에 맞는 방 검색 후 참여
    private void findRoom(CharacterInfo characterInfo){
        for(Long roomId : rooms.keySet()){
            PartyInfo partyInfo = rooms.get(roomId);

        }
    }

    // 새로운 방 생성 시 매칭 대기 큐에 있는 유저 중 조건에 맞는 유저 검색 후 매칭
    private void findUser(PartyInfo partyInfo){

    }
}
