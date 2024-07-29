package NexonJuniors.Maching.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CharacterInfo {
    @JsonProperty("date")
    private String date;

    @JsonProperty("character_name")
    private String characterName;

    @JsonProperty("world_name")
    private String worldName;

    @JsonProperty("character_gender")
    private String characterGender;

    @JsonProperty("character_class")
    private String characterClass;

    @JsonProperty("character_class_level")
    private String characterClassLevel;

    @JsonProperty("character_level")
    private int characterLevel;

    @JsonProperty("character_exp")
    private long characterExp;

    @JsonProperty("character_exp_rate")
    private String characterExpRate;

    @JsonProperty("character_guild_name")
    private String characterGuildName;

    @JsonProperty("character_image")
    private String characterImage;

    @JsonProperty("character_date_create")
    private String characterDateCreate;

    @JsonProperty("access_flag")
    private String accessFlag;

    @JsonProperty("liberation_quest_clear_flag")
    private String liberationQuestClearFlag;

    // Getters and Setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getCharacterGender() {
        return characterGender;
    }

    public void setCharacterGender(String characterGender) {
        this.characterGender = characterGender;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public String getCharacterClassLevel() {
        return characterClassLevel;
    }

    public void setCharacterClassLevel(String characterClassLevel) {
        this.characterClassLevel = characterClassLevel;
    }

    public int getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(int characterLevel) {
        this.characterLevel = characterLevel;
    }

    public long getCharacterExp() {
        return characterExp;
    }

    public void setCharacterExp(long characterExp) {
        this.characterExp = characterExp;
    }

    public String getCharacterExpRate() {
        return characterExpRate;
    }

    public void setCharacterExpRate(String characterExpRate) {
        this.characterExpRate = characterExpRate;
    }

    public String getCharacterGuildName() {
        return characterGuildName;
    }

    public void setCharacterGuildName(String characterGuildName) {
        this.characterGuildName = characterGuildName;
    }

    public String getCharacterImage() {
        return characterImage;
    }

    public void setCharacterImage(String characterImage) {
        this.characterImage = characterImage;
    }

    public String getCharacterDateCreate() {
        return characterDateCreate;
    }

    public void setCharacterDateCreate(String characterDateCreate) {
        this.characterDateCreate = characterDateCreate;
    }

    public String getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFlag(String accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getLiberationQuestClearFlag() {
        return liberationQuestClearFlag;
    }

    public void setLiberationQuestClearFlag(String liberationQuestClearFlag) {
        this.liberationQuestClearFlag = liberationQuestClearFlag;
    }
}