package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyRequirementInfo {
    private String partyLeader  ; // 파티장 닉네임
    private String worldName ; // 서버이름
    private String classMinutesInfo; // 주기 정보 (ex "2")
    private int power ; // 최소전투력을 정하는건데 파티장 자신보다 높게는 못함
    private int bishop; // 0이면 비숍 불필요 1이면 비숍 필요한거(1명만)
}

