package NexonJuniors.Maching.chatting;

import NexonJuniors.Maching.model.PartyInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExitRoomDto {
    // 1 이면 일반 유저 퇴장, 2 이면 방장 퇴장
    private int flag;
    private String exitMessage;
    private PartyInfo partyInfo;

    public ExitRoomDto(int flag, String exitMessage){
        this.flag = flag;
        this.exitMessage = String.format("%s 님이 퇴장하셨습니다", exitMessage);
    }
}
