package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyRequirementInfo {
    private String partyLeader  ; // 파티장 닉네임
    private String partyWorldName ; // 파티장 서버이름
    private String partyNeedClassMinutesInfo; // 주기 정보 (ex free, 2, 3)
    private int partyNeedPower ; // 최소전투력을 정하는건데 파티장 자신보다 높게는 못함
    private int partyNeedBishop; // 0이면 비숍 불필요 1이면 비숍 필요한거(1명만)
}

