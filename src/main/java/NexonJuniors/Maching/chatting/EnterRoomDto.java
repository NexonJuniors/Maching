package NexonJuniors.Maching.chatting;

import NexonJuniors.Maching.model.CharacterInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnterRoomDto {
    private String greetingMessage;
    private List<CharacterInfo> users;

    public EnterRoomDto(String nickname, List<CharacterInfo> users){
        this.greetingMessage = nickname + " 님이 입장하였습니다.";
        this.users = users;
    }
}
