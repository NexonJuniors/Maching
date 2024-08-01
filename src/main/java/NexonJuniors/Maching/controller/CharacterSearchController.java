package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.model.CharacterInfo;
import NexonJuniors.Maching.utils.ApiUtil;
import NexonJuniors.Maching.utils.CharacterClassMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CharacterSearchController {

    private final ApiUtil apiUtil;

    @GetMapping("/character")
    public CharacterInfo getCharacter(@RequestParam("characterName") String characterName) {
        CharacterInfo characterInfo = apiUtil.getCharacter(characterName);

        // 캐릭터 클래스 정보를 가져오고 주기와 시너지 정보를 추가
        String characterClass = characterInfo.getStatInfo().getCharacterClass();
        Map<String, List<String>> mapping = CharacterClassMapping.getCharacterClassMapping();
        List<String> displayInfo = mapping.getOrDefault(characterClass, Collections.singletonList("Unknown"));

        characterInfo.setCharacterClassInfo(characterClass); // 백앤드에서 직업저장
        characterInfo.setMinutesCharacterClassInfo(String.join(", ", displayInfo)); // 주기 저장

        return characterInfo;
    }
}