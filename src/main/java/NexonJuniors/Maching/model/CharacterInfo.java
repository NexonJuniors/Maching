package NexonJuniors.Maching.model;

public class CharacterInfo {

    private BasicInfo basicInfo;
    private StatInfo statInfo;
    private UnionInfo unionInfo;
    private HexaSkillInfo hexaSkillInfo;
    private String characterClassInfo; // 직업 정보 (ex 비숍)
    private String minutesCharacterClassInfo; // 직업 주기 및 시너지 정보 (ex 2분, 시너지)

    // Getter 및 Setter

    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public StatInfo getStatInfo() {
        return statInfo;
    }

    public void setStatInfo(StatInfo statInfo) {
        this.statInfo = statInfo;
    }

    public String getCharacterClassInfo() {
        return characterClassInfo;
    }

    public void setCharacterClassInfo(String characterClassInfo) {
        this.characterClassInfo = characterClassInfo;
    }

    public String getMinutesCharacterClassInfo() {
        return minutesCharacterClassInfo;
    }

    public void setMinutesCharacterClassInfo(String minutesCharacterClassInfo) {
        this.minutesCharacterClassInfo = minutesCharacterClassInfo;
    }

    public UnionInfo getUnionInfo() {
        return unionInfo;
    }

    public void setUnionInfo(UnionInfo unionInfo) {
        this.unionInfo = unionInfo;
    }

    public HexaSkillInfo getHexaSkillInfo() {
        return hexaSkillInfo;
    }

    public void setHexaSkillInfo(HexaSkillInfo hexaSkillInfo) {
        this.hexaSkillInfo = hexaSkillInfo;
    }

    @Override
    public String toString() {
        return "CharacterInfo{" +
                "basicInfo=" + basicInfo +
                ", statInfo=" + statInfo +
                ", hexaSkillInfo=" + hexaSkillInfo +
                '}';
    }
}

