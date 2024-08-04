package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.model.PartyData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PartyController {

    @PostMapping("/create-party")
    public ResponseEntity<String> createParty(@RequestBody PartyData partyData) {
        System.out.println("Received party data: " + partyData);

        // 파티 데이터를 처리하는 로직은 utils에서 하나? 아무튼 만들면될듯

        return ResponseEntity.ok("Party created success!");
    }
}