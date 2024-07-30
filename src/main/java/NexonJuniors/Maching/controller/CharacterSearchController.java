package NexonJuniors.Maching.controller;

import NexonJuniors.Maching.model.CharacterInfo;
import NexonJuniors.Maching.utils.ApiUtil;
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
        return apiUtil.getCharacter(characterName);
    }
}