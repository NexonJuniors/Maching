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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class CharacterSearchController {

    private final ApiUtil apiUtil;
    private final WebSocketUtil webSocketUtil;

    @GetMapping("/character")
    // 닉네임으로 캐릭터 정보 검색
    public CharacterInfo getCharacter(
            @RequestParam("characterName") String characterName,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 날짜 파라미터가 없으면 null로 설정
        CharacterInfo characterInfo = apiUtil.getCharacter(characterName, date);
        apiUtil.setCharacterClassDetails(characterInfo); // 직업 정보와 주기/시너지 정보를 설정하는 메서드

        return characterInfo;
    }

    @GetMapping("/matching-status")
    // 해당 캐릭터가 매칭중인지 리턴
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
    // 보스 파티 갯수 리턴
    public ResponseEntity<HashMap<String, Long>> getRoomCountByBossName(){
        HashMap<String, Long> result = webSocketUtil.getRoomCountByBossName();
        return ResponseEntity.ok().body(result);
    }
}