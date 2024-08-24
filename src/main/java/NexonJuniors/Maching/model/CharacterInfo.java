package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterInfo {

    private BasicInfo basicInfo;
    private StatInfo statInfo;
    private UnionInfo unionInfo;
    private HexaSkillInfo hexaSkillInfo;
    private CharacterEquipmentInfo characterEquipmentInfo;
    private String characterClassInfo; // 직업 정보 (ex 비숍)
    private String classMinutesInfo; // 주기 정보 (ex "2")
    private String classMainStatInfo; // 주스탯 정보 (ex "str")
    private SpecialRingInfo specialRingInfo;

    @Override
    public String toString() {
        return "검색된 캐릭터 : " + basicInfo ;
    }
}