package NexonJuniors.Maching.model;

public class CharacterInfo {

    private BasicInfo basicInfo;
    private StatInfo statInfo;
    private UnionInfo unionInfo;
    private HexaSkillInfo hexaSkillInfo;
    private CharacterEquipmentInfo characterEquipmentInfo;
    private String characterClassInfo; // 직업 정보 (ex 비숍)
    private String classMinutesInfo; // 주기 정보 (ex "2")
    private String classMainStatInfo; // 주스탯 정보 (ex "str")

    // Getter 및 Setter

    public CharacterEquipmentInfo getCharacterEquipmentInfo() {
        return characterEquipmentInfo;
    }

    public void setCharacterEquipmentInfo(CharacterEquipmentInfo characterEquipmentInfo) {
        this.characterEquipmentInfo = characterEquipmentInfo;
    }

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

    public String getClassMinutesInfo() {
        return classMinutesInfo;
    }

    public void setClassMinutesInfo(String classMinutesInfo) {
        this.classMinutesInfo = classMinutesInfo;
    }

    public String getClassMainStatInfo() {
        return classMainStatInfo;
    }

    public void setClassMainStatInfo(String classMainStatInfo) {
        this.classMainStatInfo = classMainStatInfo;
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
        return "검색된 캐릭터 : " + basicInfo ;
    }
}