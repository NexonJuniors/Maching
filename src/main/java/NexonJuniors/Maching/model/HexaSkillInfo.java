package NexonJuniors.Maching.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class HexaSkillInfo {
    @JsonProperty("date")
    private String date;

    @JsonProperty("character_hexa_core_equipment")
    private List<CharacterHexaCoreEquipment> characterHexaCoreEquipment;

    // Getters and Setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CharacterHexaCoreEquipment> getCharacterHexaCoreEquipment() {
        return characterHexaCoreEquipment;
    }

    public void setCharacterHexaCoreEquipment(List<CharacterHexaCoreEquipment> characterHexaCoreEquipment) {
        this.characterHexaCoreEquipment = characterHexaCoreEquipment;
    }

    public static class CharacterHexaCoreEquipment {
        @JsonProperty("hexa_core_name")
        private String hexaCoreName; //스피릿 칼리버

        @JsonProperty("hexa_core_level")
        private String hexaCoreLevel; //3

        @JsonProperty("hexa_core_type")
        private String hexaCoreType; //스킬 코어

        // Getters and Setters

        public String getHexaCoreName() {
            return hexaCoreName;
        }

        public void setHexaCoreName(String hexaCoreName) {
            this.hexaCoreName = hexaCoreName;
        }

        public String getHexaCoreLevel() {
            return hexaCoreLevel;
        }

        public void setHexaCoreLevel(String hexaCoreLevel) {
            this.hexaCoreLevel = hexaCoreLevel;
        }

        public String getHexaCoreType() {
            return hexaCoreType;
        }

        public void setHexaCoreType(String hexaCoreType) {
            this.hexaCoreType = hexaCoreType;
        }
    }
}