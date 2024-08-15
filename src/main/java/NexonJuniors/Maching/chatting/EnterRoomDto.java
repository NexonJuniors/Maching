package NexonJuniors.Maching.chatting;

import NexonJuniors.Maching.model.PartyInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterRoomDto {
    private String greetingMessage;
    private PartyInfo partyInfo;

    public EnterRoomDto(String nickname, PartyInfo partyInfo){
        this.greetingMessage = nickname + " 님이 입장하였습니다.";
        this.partyInfo = partyInfo;
    }
}
