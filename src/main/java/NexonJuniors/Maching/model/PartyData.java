package NexonJuniors.Maching.model;

public class PartyData {
    private String characterImageSrc;
    private String characterName;
    private String characterClass;
    private String minutes;
    private String power;
    private String bossTitle;

    @Override
    public String toString() {
        return "PartyData{" +
                "bossTitle=" + bossTitle +
                ", characterName=" + characterName +
                ", minutes=" + minutes +
                '}';
    }

    public String getCharacterImageSrc() {
        return characterImageSrc;
    }

    public void setCharacterImageSrc(String characterImageSrc) {
        this.characterImageSrc = characterImageSrc;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getBossTitle() {
        return bossTitle;
    }

    public void setBossTitle(String bossTitle) {
        this.bossTitle = bossTitle;
    }
// Getters and Setters
    // (생략된 부분은 lombok 등을 사용할 수도 있습니다)
}