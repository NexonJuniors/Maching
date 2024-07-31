package NexonJuniors.Maching.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StatInfo {

    @JsonProperty("date")
    private String date;

    @JsonProperty("character_class")
    private String characterClass;

    @JsonProperty("final_stat")
    private List<FinalStat> finalStat;

    @JsonProperty("remain_ap")
    private int remainAp;

    // Getters and Setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public List<FinalStat> getFinalStat() {
        return finalStat;
    }

    public void setFinalStat(List<FinalStat> finalStat) {
        this.finalStat = finalStat;
    }

    public int getRemainAp() {
        return remainAp;
    }

    public void setRemainAp(int remainAp) {
        this.remainAp = remainAp;
    }

    public static class FinalStat {
        @JsonProperty("stat_name")
        private String statName;

        @JsonProperty("stat_value")
        private String statValue;

        // Getters and Setters

        public String getStatName() {
            return statName;
        }

        public void setStatName(String statName) {
            this.statName = statName;
        }

        public String getStatValue() {
            return statValue;
        }

        public void setStatValue(String statValue) {
            this.statValue = statValue;
        }
    }
}
