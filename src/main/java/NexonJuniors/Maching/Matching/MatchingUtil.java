package NexonJuniors.Maching.Matching;

import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

// 예외 처리 구현 필요
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingUtil {
    private final List<MatchingUser> participants; // 파티 찾는 사람 리스트 ( 매칭 안된 유저 대기 큐 ) TODO ConcurrentList 로 변경해야 할 수도 있음
    private final HashMap<Long, PartyInfo> rooms; // 생성된 채팅방 리스트 TODO PartyInfo 클래스 내부에 Users 리스트 자료형을 Mathcinguser 로 변경 후 관련 메소드 변경
    private final HashSet<String> totalUser; // 전체 이용자 ( 닉네임 ) TODO 이후에 CharacterInfo 형태로 저장해서 캐릭터 정보 전체를 저장해서 처리하는 방법 연구
    private final ObjectMapper objectMapper;
    private Long roomId = 1L;

    // 파티 생성 요청 시 실행되는 로직
    public HashMap<Long, List<String>> createParty(
            String uuid,
            int maximumPeople,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo,
            String classMinutesInfo,
            String classMainStatInfo,
            PartyRequirementInfo partyRequirementInfo //파티의 서버, 파티장, 그외 조건들 다수 포함
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

        // TODO MathcingException 클래스로 예외 처리, 예외 발생 시 채팅방으로 넘어가지 않도록 구현
        if (totalUser.contains(basicInfo.getCharacterName())) throw new RuntimeException("이미 매칭에 참여중인 유저");

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setClassMinutesInfo(classMinutesInfo);
        characterInfo.setClassMainStatInfo(classMainStatInfo);

        // 전체 이용자 리스트에 내 캐릭터 정보 추가
        totalUser.add(characterInfo.getBasicInfo().getCharacterName());

        // 파티 정보에 보스이름, 방 최대 인원 수, 방장 캐릭터 정보 추가 TODO 생성자로 교체 요망
        PartyInfo partyInfo = new PartyInfo();
        partyInfo.setBossName(bossName);
        partyInfo.setMaximumPeople(maximumPeople);
        partyInfo.setPartyRequirementInfo(partyRequirementInfo);
        partyInfo.getUsers().add(characterInfo);

        // 새로운 방을 만들어 파티 정보 등록
        rooms.put(roomId, partyInfo);

        HashMap<Long, List<String>> result = new HashMap<>();

        // 매칭 대기 큐에서 방 조건에 부합하는 유저를 찾아서 추가
        result.put(roomId, findUser(partyInfo));
        result.get(roomId++).add(uuid);

        log.info("[파티생성] | {} | [{}번][{}] | 방장 {} 님 | 최대 인원 {} 인 | 현재 인원 {} 명 | 전체 파티 {} 개 | {} 극딜 주기 | 최소 전투력 {} | 비숍 {}",
                partyInfo.getPartyRequirementInfo().getPartyWorldName(),
                roomId - 1,
                partyInfo.getBossName(),
                characterInfo.getBasicInfo().getCharacterName(),
                partyInfo.getMaximumPeople(),
                partyInfo.getUsers().size(),
                rooms.size(),
                partyInfo.getPartyRequirementInfo().getPartyNeedClassMinutesInfo(),
                partyInfo.getPartyRequirementInfo().getPartyNeedPower(),
                partyInfo.getPartyRequirementInfo().getPartyNeedBishop()
        );

        return result;
    }

    // 매칭 참여 요청 시 실행되는 로직
    public long joinParty(
            String uuId,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo,
            String classMinutesInfo,
            String classMainStatInfo,
            int maximumPeople,
            int power
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

        // TODO MathcingException 클래스로 예외 처리, 예외 발생 시 채팅방으로 넘어가지 않도록 구현
        if (totalUser.contains(basicInfo.getCharacterName())) throw new RuntimeException("이미 매칭에 참여중인 유저");

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setClassMinutesInfo(classMinutesInfo);
        characterInfo.setClassMainStatInfo(classMainStatInfo);

        // 전체 이용자 리스트에 내 캐릭터 정보 추가
        totalUser.add(characterInfo.getBasicInfo().getCharacterName());

        // 매칭 유저 정보에 보스 이름, uuid, 캐릭터 정보 설정 TODO 생성자로 교체 요망
        MatchingUser matchingUser = new MatchingUser();
        matchingUser.setBossName(bossName);
        matchingUser.setUuId(uuId);
        matchingUser.setCharacterInfo(characterInfo);
        matchingUser.setMaximumPeople(maximumPeople);
        matchingUser.setPower(power);

        //전투력도 로그에 남길지 고민
        log.info("[매칭참여] | {} 님 | [{}] 매칭 큐 참여.",
                basicInfo.getCharacterName(),
                bossName
        );

        // 참여 가능 한 방 중 조건에 맞는 방을 찾음
        return findRoom(matchingUser);
    }


    // TODO 채팅방 입장 시 채팅 방에 있는 사람들을 저장하고 있는 리스트를 뿌려줌
    public void enterRoom() {

    }

    // 매칭 대기 큐에 처음 참여 시 기존에 생성되어있는 방들 중 조건에 맞는 방 검색 후 참여
    private long findRoom(MatchingUser matchingUser) {
        for (Long roomId : rooms.keySet()) {
            PartyInfo partyInfo = rooms.get(roomId);
            // 조건에 맞는 파티가 있으면 방 번호 반환하여 파티 참여됨
            if (
                    partyInfo.getBossName().equals(matchingUser.getBossName()) //파티의 보스이름과 유저가 매칭돌린 보스 이름이 같은가?
                    /*&& partyInfo.getPartyRequirementInfo().getPartyWorldName().equals(matchingUser.getCharacterInfo.getBasicInfo().getWorldName()) // 방장의 서버와 같은 유저인가?*/
                    && partyInfo.getMaximumPeople() > partyInfo.getUsers().size() //파티의 최대인원수보다 현재 파티의 유저수가 더 적은가?
                    // 유저의 직업이 비숍인데 비숍이 더이상 필요한가? -> if matchingUser.getCharacterInfo.getCharacterClassInfo()=="비숍" 이면   partyInfo.getPartyRequirementInfo().getPartyNeedBishop() 이게 0이면 비숍 필요없음. 1이면 비숍 필요함.
                    // 지금 마지막자리가 비숍이 들어가야하는가? -> 이건위에꺼랑 연관되어야함. 파티원 리스트의 characterClassInfo를 순회하여 비숍이 있는지 확인하거나. 아니면 비숍이 들어올때 비숍이 있다고 플래그를 세워줄까?
                    // 전투력은 만족하는가? -> partyInfo.getPartyRequirementInfo().getPartyNeedPower()보다 matchingUser.getCharacterInfo.statInfo.여기서 값이 전투력인걸 찾아서그 값을 가져와야함
                    // 주기를 만족하는가?
            )
            {
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());

                // 기존 파티원들 목록을 가져오기
                StringBuilder partyMembers = new StringBuilder();
                for (CharacterInfo member : partyInfo.getUsers()) {
                    if (partyMembers.length() > 0) {
                        partyMembers.append(", ");
                    }
                    partyMembers.append(member.getBasicInfo().getCharacterName());
                }

                // 로그 메시지 출력, 조건도 로그에 달아줄까 생각중. 이후 관리자 페이지에서 확인 가능하도록 필터및 슬라이싱
                log.info("[파티참여] | {} 님 | [{}번][{}] | 현재 파티원 [{}] | 남은 자리 {} 인",
                        matchingUser.getCharacterInfo().getBasicInfo().getCharacterName(),
                        roomId,
                        partyInfo.getBossName(),
                        partyMembers.toString(),
                        partyInfo.getMaximumPeople() - partyInfo.getUsers().size()
                );

                return roomId;
            }
        }

        // 조건에 맞는 파티가 없으면 매칭 대기 큐에 유저 등록 후 -1 반환
        participants.add(matchingUser);
        return -1;
    }

    // 새로운 방 생성 시 매칭 대기 큐에 있는 유저 중 조건에 맞는 유저 검색 후 매칭
    private List<String> findUser(PartyInfo partyInfo) {
        List<String> uuidList = new ArrayList<>();

        Iterator<MatchingUser> iterator = participants.iterator();
        while(iterator.hasNext()){
            // 이미 최대 인원 만큼의 유저를 찾았을 경우 탐색 종료
            if(partyInfo.getUsers().size() >= partyInfo.getMaximumPeople()) break;

            MatchingUser matchingUser = iterator.next();
            // 방 조건에 부합한 유저를 추가
            if(matchingUser.getBossName().equals(partyInfo.getBossName()))
            {
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());
                uuidList.add(matchingUser.getUuId());
                log.info("{} 님의 파티에 대기큐의 {} 님이 참가",
                        partyInfo.getUsers().get(0).getBasicInfo().getCharacterName(),
                        matchingUser.getCharacterInfo().getBasicInfo().getCharacterName());
                // 매칭 대기큐에서 조건에 부합하는 유저 제거 ( 파티에 참가되기 때문 )
                iterator.remove();
            }
        }

//        혹시 쓸 수도 있으니 일단 대기
//        for(MatchingUser matchingUser: participants){
//            // 이미 최대 인원 만큼의 유저를 찾았을 경우 탐색 종료
//            if(partyInfo.getUsers().size() >= partyInfo.getMaximumPeople()) break;
//
//            // 방 조건에 부합한 유저를 추가
//            if(matchingUser.getBossName().equals(partyInfo.getBossName()))
//            {
//                partyInfo.getUsers().add(matchingUser.getCharacterInfo());
//                uuidList.add(matchingUser.getUuId());
//                // 매칭 대기큐에서 조건에 부합하는 유저 제거 ( 파티에 참가되기 때문 )
//                participants.remove(matchingUser);
//            }
//        }

        // 연결 되었다는 메세지를 전달하기 위해 연결된 클라이언트들의 uuid 반환
        return uuidList;
    }
}
