package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.Matching.WebSocketUtil;
import NexonJuniors.Maching.model.CharacterInfo;
import NexonJuniors.Maching.utils.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class CharacterSearchController {

    private final ApiUtil apiUtil;
    private final WebSocketUtil webSocketUtil;

    @GetMapping("/character")
    public CharacterInfo getCharacter(@RequestParam("characterName") String characterName) {
        CharacterInfo characterInfo = apiUtil.getCharacter(characterName);
        apiUtil.setCharacterClassDetails(characterInfo); // 직업 정보와 주기/시너지 정보를 설정하는 메서드

        return characterInfo;
    }

    @GetMapping("/matching-status")
    public ResponseEntity<?> getMatchingStatus(@RequestParam("characterName") String characterName) {
        if (characterName == null || characterName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        boolean userOptional = webSocketUtil.findParticipantByName(characterName);
        HashMap<String,Boolean> response = new HashMap<>();
        response.put("isMatchingStarted",userOptional);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/rooms/count")
    public ResponseEntity<HashMap<String, Long>> getRoomCountByBossName(){
        HashMap<String, Long> result = webSocketUtil.getRoomCountByBossName();
        return ResponseEntity.ok().body(result);
    }
}