package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingUser {
    private String uuId;
    private String bossName;
    private CharacterInfo characterInfo;
    private int maximumPeople;
    private int power;
}
