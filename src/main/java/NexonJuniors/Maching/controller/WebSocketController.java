package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.Matching.WebSocketUtil;
import NexonJuniors.Maching.chatting.ChatMessage;
import NexonJuniors.Maching.chatting.EnterRoomDto;
import NexonJuniors.Maching.model.PartyRequirementInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebSocketUtil webSocketUtil;

    @MessageMapping("/createParty")
    @SendTo("/room/{roomId}")
    public void createParty(
            @Header("uuId") String uuid,
            @Header("maximumPeople") int maximumPeople,
            @Header("bossName") String bossName,
            @Header("basicInfo") String basicInfo,
            @Header("hexaSkillInfo") String hexaSkillInfo,
            @Header("statInfo") String statInfo,
            @Header("unionInfo") String unionInfo,
            @Header("characterClassInfo") String characterClassInfo,
            @Header("classMinutesInfo") String classMinutesInfo,
            @Header("classMainStatInfo") String classMainStatInfo,
            @Header("partyLeader") String partyLeader,
            @Header("partyWorldName") String partyWorldName,
            @Header("partyNeedClassMinutesInfo") String partyNeedClassMinutesInfo,
            @Header("partyNeedPower") int partyNeedPower,
            @Header("partyNeedBishop") int partyNeedBishop,
            @Header("isMatchingStarted") boolean isMatchingStarted,
            @Header("bossImg") String bossImg
    ) {
        PartyRequirementInfo partyRequirementInfo = new PartyRequirementInfo();
        partyRequirementInfo.setPartyLeader(partyLeader);
        partyRequirementInfo.setPartyWorldName(partyWorldName);
        partyRequirementInfo.setPartyNeedClassMinutesInfo(partyNeedClassMinutesInfo);
        partyRequirementInfo.setPartyNeedPower(partyNeedPower);
        partyRequirementInfo.setPartyNeedBishop(partyNeedBishop);

        HashMap<Long, List<String>> uuidList = webSocketUtil.createParty(
                uuid,
                maximumPeople,
                bossName,
                basicInfo,
                hexaSkillInfo,
                statInfo,
                unionInfo,
                characterClassInfo,
                classMinutesInfo,
                classMainStatInfo,
                partyRequirementInfo,
                isMatchingStarted,
                bossImg
        );

        for (Long roomId : uuidList.keySet()) {
            for (String userId : uuidList.get(roomId)) {
                simpMessagingTemplate.convertAndSend(
                        String.format("/room/%s", userId),
                        roomId
                );
            }
        }
    }

    @MessageMapping("/joinParty")
    public void joinParty(
            @Header("uuId") String uuId,
            @Header("bossName") String bossName,
            @Header("basicInfo") String basicInfo,
            @Header("hexaSkillInfo") String hexaSkillInfo,
            @Header("statInfo") String statInfo,
            @Header("unionInfo") String unionInfo,
            @Header("classMinutesInfo") String classMinutesInfo,
            @Header("classMainStatInfo") String classMainStatInfo,
            @Header("className") String className,
            @Header("maximumPeople") int maximumPeople,
            @Header("power") int power,
            @Header("isMatchingStarted") boolean isMatchingStarted
    ) {
        if (isMatchingStarted) {
            long roomId = webSocketUtil.joinParty(uuId, bossName, basicInfo, hexaSkillInfo, statInfo, unionInfo, classMinutesInfo, classMainStatInfo, className, maximumPeople, power, isMatchingStarted);
            simpMessagingTemplate.convertAndSend(
                    String.format("/room/%s", uuId),
                    roomId
            );
        } else {
            // 매칭이 시작되지 않았다면
            simpMessagingTemplate.convertAndSend(
                    String.format("/room/%s", uuId),
                    -1 // 매칭 실패 시의 상태 코드
            );
        }
    }

    // 매칭 취소 기능 ToDO 이거 헤더에 UUID 세션스토리지에 저장해서 넘겨준거 가져오면 아마 채팅에서도 사용가능 하지 싶은데
    @MessageMapping("/cancelMatching")
    public void cancelMatching(@Header("characterName") String characterName) {
        if (characterName == null || characterName.isEmpty()) {
            log.error("Character name is null or empty");
            return;
        }

        try {
            webSocketUtil.removeParticipant(characterName); // 참가자 리스트에서 유저 제거
            simpMessagingTemplate.convertAndSend(
                    String.format("/room/%s", characterName),
                    "CANCELLED" // 매칭 취소 메시지 전송
            );
            /*log.info("[매칭취소] | {} 님의 매칭 취소 완료", characterName);*/
        } catch (Exception e) {
            log.error("Error canceling matching for character {}: {}", characterName, e.getMessage());
        }
    }

/*    // 클라이언트가 특정 캐릭터의 매칭 시작 여부를 요청할 수 있는 엔드포인트
    @MessageMapping("/isMatchingStarted")
    @SendTo("/user/queue/matchingStatus")
    public boolean isMatchingStarted(@Header("characterName") String characterName) {
        if (characterName == null || characterName.isEmpty()) {
            log.error("Character name is null or empty");
            return false; // 기본값을 false로 반환
        }

        // 해당 캐릭터가 매칭에 참여 중인지 확인
        Optional<MatchingUser> userOptional = matchingUtil.findParticipantByName(characterName);

        // 매칭에 참여 중이면 true, 아니면 false 반환
        return userOptional.map(MatchingUser::getIsMatchingStarted).orElse(false);
    }*/

    // 방 입장 시 인사말과 채팅방에 참여 중인 모든 인원 정보를 클라이언트에게 전달
    @MessageMapping("/enterRoom")
    public void enterRoom(@Header("roomId") Long roomId,
                          String nickname) {

        EnterRoomDto dto = webSocketUtil.enterRoom(roomId, nickname);

        simpMessagingTemplate.convertAndSend(
                String.format("/room/%d", roomId),
                dto
        );
    }

    // 채팅방에 채팅 입력 시 구독된 채팅방으로 메세지 전달
    @MessageMapping("/chatting")
    public void chatting(ChatMessage chatMessage){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        chatMessage.setTime(formatter.format(new Date()));

        simpMessagingTemplate.convertAndSend(
                String.format("/room/%d", chatMessage.getRoomId()),
                chatMessage
        );
    }

//    @MessageMapping("/exitRoom")
//    public void exitRoom(@Header("roomId") Long roomId, String nickname){
//
//    }

    // TODO 채팅 쳤을 때 해당 유저가 구독한 채팅방 URL로 메세지를 뿌려야함
//    @MessageMapping("/room")
//    public void sendChat(ChatMessage message){
//
//    }
}
