package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.Matching.MatchingUtil;
import NexonJuniors.Maching.chatting.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MatchingUtil matchingUtil;

    @MessageMapping("/createParty")
    @SendTo("/room/{roomId}")
    public void createParty(@Header("maximumPeople") int maximumPeople,
                            @Header("bossName") String bossName,
                            @Header("basicInfo") String basicInfo,
                            @Header("hexaSkillInfo") String hexaSkillInfo,
                            @Header("statInfo") String statInfo,
                            @Header("unionInfo") String unionInfo,
                            @Header("classMinutesInfo") String classMinutesInfo,
                            @Header("classMainStatInfo") String classMainStatInfo
    )
    {
        matchingUtil.createParty(maximumPeople, bossName, basicInfo, hexaSkillInfo, statInfo, unionInfo, classMinutesInfo, classMainStatInfo);

        // return "파티 생성완료"
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
            @Header("classMainStatInfo") String classMainStatInfo
    )
    {
        long roomId = matchingUtil.joinParty(uuId, bossName, basicInfo, hexaSkillInfo, statInfo, unionInfo, classMinutesInfo, classMainStatInfo);
        simpMessagingTemplate.convertAndSend(
                String.format("/room/%s", uuId),
                roomId
        );
    }

    @MessageMapping("/enterRoom")
    public void enterRoom(@Header("roomId") Long roomId){
    }

    // TODO 채팅 쳤을 때 해당 유저가 구독한 채팅방 URL로 메세지를 뿌려야함
//    @MessageMapping("/room")
//    public void sendChat(ChatMessage message){
//
//    }
}
