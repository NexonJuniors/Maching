package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PartyInfo {
    private String bossName;
    private int maximumPeople;
    private List<CharacterInfo> users = new ArrayList<>();
}
