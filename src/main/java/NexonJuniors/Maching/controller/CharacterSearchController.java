package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.model.CharacterInfo;
import NexonJuniors.Maching.utils.ApiUtil;
import NexonJuniors.Maching.utils.CharacterClassMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CharacterSearchController {

    private final ApiUtil apiUtil;

    @GetMapping("/character")
    public CharacterInfo getCharacter(@RequestParam("characterName") String characterName) {
        CharacterInfo characterInfo = apiUtil.getCharacter(characterName);
        apiUtil.setCharacterClassDetails(characterInfo); // 직업 정보와 주기/시너지 정보를 설정하는 메서드

        return characterInfo;
    }
}