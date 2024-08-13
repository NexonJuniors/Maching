package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PartyInfo {
    // TODO 추후에 보스이름이랑 방 최대인원  partyRequirementInfo 에 포함 시키는게 좋을 수도
    private String bossName;
    private int maximumPeople;
    private List<CharacterInfo> users = new ArrayList<>();
    private PartyRequirementInfo partyRequirementInfo; // 파티 필요조건 클래스
    private String bossImg;
}
