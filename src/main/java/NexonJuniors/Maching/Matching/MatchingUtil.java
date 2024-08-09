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
    private final List<CharacterInfo> participants; // 파티 찾는 사람 리스트
    private final HashMap<Long, List<CharacterInfo>> rooms; // 생성된 채팅방 리스트
    private final ObjectMapper objectMapper;

    public void createParty(
                        String strBasicInfo,
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
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException(ApiExceptionCode.DATA_PARSING_ERROR);
        }
    }

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

    private void findRoom(CharacterInfo characterInfo){

    }
}
