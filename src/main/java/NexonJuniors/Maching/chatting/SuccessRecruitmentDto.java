package NexonJuniors.Maching.chatting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessRecruitmentDto {
    private String leader;
    private String recruitmentMessage;

    public SuccessRecruitmentDto(String leader){
        this.leader = leader;
        this.recruitmentMessage = String.format("방장 %s 님이 파티원 모집을 완료하셨습니다.", leader);
    }
}
