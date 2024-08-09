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
    public void createParty(@Header("partyInfo") String partyInfo)
    {
        matchingUtil.createParty(partyInfo);

        // return "파티 생성완료"
    }

    @MessageMapping("/joinParty")
    @SendTo("/room/{roomId}")
    public void joinParty(@Header("basicInfo") String basicInfo,
                            @Header("hexaSkillInfo") String hexaSkillInfo,
                            @Header("statInfo") String statInfo,
                            @Header("unionInfo") String unionInfo){

        matchingUtil.joinParty(basicInfo, hexaSkillInfo, statInfo, unionInfo);

        // return "파티 생성완료"
    }

//    @MessageMapping("/chat")
//    public void sendMessage(ChatMessage chatMessage){
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatMessage.setTime(time);
//        simpMessagingTemplate.convertAndSend(
//                String.format("/room/%s", chatMessage.getRoomId()),
//                chatMessage
//        );
//    }
//
//
//    @SubscribeMapping("/chat/1")
//    public ChatMessage sendGreet(){
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setRoomId(Long.valueOf(1));
//        chatMessage.setSender("admin");
//        chatMessage.setMessage("입장");
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatMessage.setTime(time);
//
//        return chatMessage;
//    }
}
