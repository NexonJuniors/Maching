package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.model.PartyData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PartyController {

    @PostMapping("/create-party")
    public ResponseEntity<String> createParty(@RequestBody PartyData partyData) {
        System.out.println("Received party data: " + partyData); // 버그 픽스용 콘솔 찍음, 후에 삭제할 코드
        // createParty 메소드 구현 요망
        // 파티 데이터를 처리하는 로직은 utils에서 하나? 아무튼 만들면될듯

        // 파티 데이터를 처리하는 로직을 추가

        Map<String, String> response = new HashMap<>();
        response.put("message", "Party created successfully!");

        return ResponseEntity.ok(response);
        // return new RedirectView("/chatroom"); //일단 클라이언트에서 처리
    }
}