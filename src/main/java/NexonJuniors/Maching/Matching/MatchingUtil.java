package NexonJuniors.Maching.Matching;

import NexonJuniors.Maching.chatting.EnterRoomDto;
import NexonJuniors.Maching.excption.api.ApiException;
import NexonJuniors.Maching.excption.api.ApiExceptionCode;
import NexonJuniors.Maching.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.Message;

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
    private final SimpMessagingTemplate simpMessagingTemplate;
    private Long roomId = 1L;

/*    @Autowired
    // 생성자를 통해 모든 final 필드 초기화
    public MatchingUtil(List<MatchingUser> participants,
                        HashMap<Long, PartyInfo> rooms,
                        HashSet<String> totalUser,
                        ObjectMapper objectMapper,
                        SimpMessagingTemplate simpMessagingTemplate) {
        this.participants = participants;
        this.rooms = rooms;
        this.totalUser = totalUser;
        this.objectMapper = objectMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }*/

    // 파티 생성 요청 시 실행되는 로직
    public HashMap<Long, List<String>> createParty(
            String uuid,
            int maximumPeople,
            String bossName,
            String strBasicInfo,
            String strHexaSkillInfo,
            String strStatInfo,
            String strUnionInfo,
            String characterClassInfo,
            String classMinutesInfo,
            String classMainStatInfo,
            PartyRequirementInfo partyRequirementInfo, //파티의 서버, 파티장, 그외 조건들 다수 포함
            Boolean isMatchingStarted // 파티 생성되면 이사람은 매칭이 시작된거
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
        // 매칭 참여중인 유저가 매칭을 시도하거나 방을만들려고하면 서버에서의 에러로 클라이언트에 경고및 리다이랙트를 구현해야지
        if (totalUser.contains(basicInfo.getCharacterName())) {
            throw new MatchingException("이미 매칭에 참여중인 유저입니다.");
        }

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setCharacterClassInfo(characterClassInfo);
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
            String className,
            int maximumPeople,
            int power,
            Boolean isMatchingStarted // 여기선 무조건 True임 매칭참가를 한거니까
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
        // 매칭 참여중인 유저가 매칭을 시도하거나 방을만들려고하면 서버에서의 에러로 클라이언트에 경고및 리다이랙트를 구현해야지
        if (totalUser.contains(basicInfo.getCharacterName())) {
            throw new MatchingException("이미 매칭에 참여중인 유저입니다.");
        }

        // 캐릭터 정보 통합 객체로 통합 TODO 생성자로 교체 요망
        CharacterInfo characterInfo = new CharacterInfo();
        characterInfo.setBasicInfo(basicInfo);
        characterInfo.setHexaSkillInfo(hexaSkillInfo);
        characterInfo.setStatInfo(statInfo);
        characterInfo.setUnionInfo(unionInfo);
        characterInfo.setCharacterClassInfo(className);
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


    // 채팅방 입장 시 EnterRoomDto 데이터 뿌림 ( {인사말, 채팅방에 참가한 유저리스트} 로 구성 )
    public EnterRoomDto enterRoom(Long roomId, String nickname) {
        List<CharacterInfo> users = rooms.get(roomId).getUsers();

        return new EnterRoomDto(nickname, users);
    }

    // 매칭 대기 큐에 처음 참여 시 기존에 생성되어있는 방들 중 조건에 맞는 방 검색 후 참여
    // 또한 fondRoom에서 못찾으면 로딩화면으로 이동해줘야함. 로딩화면에서는 취소버튼이 존재해야함.
    private long findRoom(MatchingUser matchingUser) {
        for (Long roomId : rooms.keySet()) {
            PartyInfo partyInfo = rooms.get(roomId);

            // 파티 조건 검사를 함수로 대체
            if (isUserMatchPartyRequirement(matchingUser, partyInfo)) {
                // 모든 조건을 충족하면 파티에 유저 추가
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());

                // 파티원 목록을 로그에 출력
                StringBuilder partyMembers = new StringBuilder();
                for (CharacterInfo member : partyInfo.getUsers()) {
                    if (partyMembers.length() > 0) {
                        partyMembers.append(", ");
                    }
                    partyMembers.append(member.getBasicInfo().getCharacterName());
                }

                // 로그 메시지 출력
                log.info("[파티참여] | {} 님 | [{}번][{}] | 현재 파티원 [{}] | 남은 자리 {} 인",
                        matchingUser.getCharacterInfo().getBasicInfo().getCharacterName(),
                        roomId,
                        partyInfo.getBossName(),
                        partyMembers.toString(),
                        partyInfo.getMaximumPeople() - partyInfo.getUsers().size()
                );

                return roomId;
            }
            // isUserMatchPartyRequirement를 만족못하면 다음방으로
        }

        // 조건에 맞는 파티가 없으면 매칭 대기 큐에 유저 등록 후 -1 반환
        participants.add(matchingUser);
        return -1;
    }

    // 새로운 방 생성 시 매칭 대기 큐에 있는 유저 중 조건에 맞는 유저 검색 후 매칭
    private List<String> findUser(PartyInfo partyInfo) {
        List<String> uuidList = new ArrayList<>();

        Iterator<MatchingUser> iterator = participants.iterator();
        while (iterator.hasNext()) {
            // 이미 최대 인원 만큼의 유저를 찾았을 경우 탐색 종료
            if (partyInfo.getUsers().size() >= partyInfo.getMaximumPeople()) break;

            MatchingUser matchingUser = iterator.next();

            // 방 조건에 부합한 유저를 추가
            if (isUserMatchPartyRequirement(matchingUser, partyInfo)) {
                partyInfo.getUsers().add(matchingUser.getCharacterInfo());
                uuidList.add(matchingUser.getUuId());
                // 로그메세지 출력
                log.info("[파티참여] | [대기큐] {} 님 | {}님 파티에 참가",
                        matchingUser.getCharacterInfo().getBasicInfo().getCharacterName(),
                        partyInfo.getUsers().get(0).getBasicInfo().getCharacterName()
                );
                // 매칭 대기큐에서 조건에 부합하는 유저 제거 (파티에 참가되기 때문)
                iterator.remove();
            }
        }
        // 연결되었다는 메세지를 전달하기 위해 연결된 클라이언트들의 uuid 반환
        return uuidList;
    }

    // 매칭 취소 메소드
    public void removeParticipant(String characterName) {
        // participants 리스트에서 해당 유저를 찾고, 상태를 업데이트한 후 리스트에서 제거
        participants.removeIf(user -> {
            if (user.getCharacterInfo().getBasicInfo().getCharacterName().equals(characterName)) {
                user.setIsMatchingStarted(false);
                totalUser.remove(characterName);
                return true;
            }
            return false;
        });
        log.info("[매칭취소] | [대기큐] {} 님 | 매칭 취소 ", characterName);
    }

    public boolean findParticipantByName(String characterName) {
        return totalUser.contains(characterName);
    }


    // 파티조건 알고리즘 함수 -> 이걸로 findUser와 findRoom에서 유저가 방에 들어갈 수 있는지 체크를 해준다
    private boolean isUserMatchPartyRequirement(MatchingUser matchingUser, PartyInfo partyInfo) {

        // 0. 파티의 서버와 유저의 서버가 같은지 확인
        boolean isServerMatch = partyInfo.getPartyRequirementInfo().getPartyWorldName()
                .equals(matchingUser.getCharacterInfo().getBasicInfo().getWorldName());
        if (!isServerMatch) {
            System.out.println("서버 불일치: 유저 " + matchingUser.getCharacterInfo().getBasicInfo().getWorldName() + " / 파티 " + partyInfo.getPartyRequirementInfo().getPartyWorldName());
            return false; // 서버가 다르면 다음 방으로
        }

        // 1. 파티의 보스 이름과 유저가 매칭 돌린 보스 이름이 같은지 확인
        boolean isBossNameMatch = partyInfo.getBossName().equals(matchingUser.getBossName());
        if (!isBossNameMatch) {
            System.out.println("보스 이름 불일치: 유저 " + matchingUser.getBossName() + " / 파티 " + partyInfo.getBossName());
            return false; // 보스 이름이 다르면 다음 방으로
        }

        // 2. 파티의 최대 인원수를 확인
        boolean isMaxPeopleNotReached = partyInfo.getMaximumPeople() > partyInfo.getUsers().size();
        if (!isMaxPeopleNotReached) {
            System.out.println("최대 인원 초과: 현재 인원수 " + partyInfo.getUsers().size() + " / 최대 인원수 " + partyInfo.getMaximumPeople());
            return false; // 인원이 가득 찼으면 다음 방으로
        }

        // 3. 유저가 원한 최대 파티 인원수보다 파티의 최대 인원수가 작거나 같은가
        boolean isUserWantPartyPeopleNotReached = partyInfo.getMaximumPeople() <= matchingUser.getMaximumPeople();
        if (!isUserWantPartyPeopleNotReached) {
            System.out.println("유저가 원하는 최대 파티 인원보다 파티의 최대 인원수가 큽니다.");
            return false;
        }


        // 4. 유저의 전투력이 파티 요구 전투력 이상인지 확인
        boolean isPowerSufficient = matchingUser.getPower() >= partyInfo.getPartyRequirementInfo().getPartyNeedPower();
        if (!isPowerSufficient) {
            System.out.println("전투력 부족: 유저 " + matchingUser.getPower() + " / 파티 요구 " + partyInfo.getPartyRequirementInfo().getPartyNeedPower());
            return false; // 전투력이 부족하면 다음 방으로
        }

        // 5. 유저의 직업이 비숍일 경우 비숍이 파티에 필요한지 확인
        boolean isBishopNeeded = matchingUser.getCharacterInfo().getCharacterClassInfo().equals("비숍")
                ? partyInfo.getPartyRequirementInfo().getPartyNeedBishop() == 1 &&
                partyInfo.getUsers().stream().noneMatch(member -> member.getCharacterClassInfo().equals("비숍"))
                : true;
        if (!isBishopNeeded) {
            System.out.println("비숍 필요 여부 불일치: 유저 " + matchingUser.getCharacterInfo().getCharacterClassInfo() + " / 비숍 필요 " + partyInfo.getPartyRequirementInfo().getPartyNeedBishop());
            return false; // 비숍이 필요하지 않으면 다음 방으로
        }

        // 6. 유저의 주기와 파티의 요구 주기 일치 여부 확인 (free인 경우 항상 true)
        String requiredMinutes = partyInfo.getPartyRequirementInfo().getPartyNeedClassMinutesInfo();
        String userMinutes = matchingUser.getCharacterInfo().getClassMinutesInfo();

        boolean isClassMinutesMatch;
        if (requiredMinutes.equals("free") || userMinutes.equals("free")) {
            isClassMinutesMatch = true;
        } else {
            isClassMinutesMatch = requiredMinutes.equals(userMinutes);
        }

        if (!isClassMinutesMatch) {
            System.out.println("주기 불일치: 유저 " + userMinutes + " / 파티 요구 " + requiredMinutes);
            return false; // 주기가 맞지 않으면 다음 방으로
        }

        return isClassMinutesMatch;
    }

    // 예외 처리 클래스
    public class MatchingException extends RuntimeException {
        public MatchingException(String message) {
            super(message);
        }
    }

    @MessageExceptionHandler(MatchingException.class)
    public void handleMatchingException(MatchingException exception, Message<?> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/user/queue/errors",
                exception.getMessage()
        );

        log.error("Exception occurred: {}", exception.getMessage());
    }

/*    // 공통 로그 출력 함수
    private void logPartyJoin(PartyInfo partyInfo, MatchingUser matchingUser, Long roomId, boolean fromQueue) {
        // 파티원 목록을 로그에 출력
        StringBuilder partyMembers = new StringBuilder();
        for (CharacterInfo member : partyInfo.getUsers()) {
            if (partyMembers.length() > 0) {
                partyMembers.append(", ");
            }
            partyMembers.append(member.getBasicInfo().getCharacterName());
        }

        String queueIndicator = fromQueue ? "[대기큐]" : "";

        // 로그 메시지 출력
        log.info("{} [파티참여] | {} 님 | [{}번][{}] | 현재 파티원 [{}] | 남은 자리 {} 인",
                queueIndicator,
                matchingUser.getCharacterInfo().getBasicInfo().getCharacterName(),
                roomId,
                partyInfo.getBossName(),
                partyMembers.toString(),
                partyInfo.getMaximumPeople() - partyInfo.getUsers().size()
        );
    }*/

}
