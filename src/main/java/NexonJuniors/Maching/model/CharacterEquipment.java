package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CharacterEquipment {

    private String date; // 데이터 날짜
    private String characterGender; // 성별
    private String characterClass; // 직업
    private int presetNo; // 현재 설정된 장비 프리셋
    private List<ItemEquipment> itemEquipment; // 장착중인 장비 리스트
    private List<ItemEquipment> itemEquipmentPreset1; // 장비프리셋1
    private List<ItemEquipment> itemEquipmentPreset2; // 장비프리셋2
    private List<ItemEquipment> itemEquipmentPreset3; // 장비프리셋3
    private Title title; // 캐릭터 타이틀( 별거 안나옴)
    private List<ItemEquipment> dragonEquipment; //에반 전용 장비창
    private List<ItemEquipment> mechanicEquipment; //메카닉 전용 장비창

}

@Getter
@Setter
class ItemEquipment {

    private String itemEquipmentPart;
    private String itemEquipmentSlot;
    private String itemName;
    private String itemIcon;
    private String itemDescription;
    private String itemShapeName;
    private String itemShapeIcon;
    private String itemGender;
    private ItemOption itemTotalOption;
    private ItemOption itemBaseOption;
    private String potentialOptionGrade;
    private String additionalPotentialOptionGrade;
    private String potentialOption1;
    private String potentialOption2;
    private String potentialOption3;
    private String additionalPotentialOption1;
    private String additionalPotentialOption2;
    private String additionalPotentialOption3;
    private int equipmentLevelIncrease;
    private ItemExceptionalOption itemExceptionalOption;
    private ItemAddOption itemAddOption;
    private int growthExp;
    private int growthLevel;
    private String scrollUpgrade;
    private String cuttableCount;
    private String goldenHammerFlag;
    private String scrollResilienceCount;
    private String scrollUpgradableCount;
    private String soulName;
    private String soulOption;
    private ItemOption itemEtcOption;
    private String starforce;
    private String starforceScrollFlag;
    private ItemOption itemStarforceOption;
    private int specialRingLevel;
    private String dateExpire;

}

@Getter
@Setter
class ItemOption {
    private String str;
    private String dex;
    private String int_;
    private String luk;
    private String maxHp;
    private String maxMp;
    private String attackPower;
    private String magicPower;
    private String armor;
    private String speed;
    private String jump;
    private String bossDamage;
    private String ignoreMonsterArmor;
    private String allStat;
    private String damage;
    private int equipmentLevelDecrease;
    private String maxHpRate;
    private String maxMpRate;

}

@Getter
@Setter
class ItemExceptionalOption {
    private String str;
    private String dex;
    private String int_;
    private String luk;
    private String maxHp;
    private String maxMp;
    private String attackPower;
    private String magicPower;
    private int exceptionalUpgrade;

}

@Getter
@Setter
class ItemAddOption {
    private String str;
    private String dex;
    private String int_;
    private String luk;
    private String maxHp;
    private String maxMp;
    private String attackPower;
    private String magicPower;
    private String armor;
    private String speed;
    private String jump;
    private String bossDamage;
    private String damage;
    private String allStat;
    private int equipmentLevelDecrease;

}

@Getter
@Setter
class Title {
    private String titleName;
    private String titleIcon;
    private String titleDescription;
    private String dateExpire;
    private String dateOptionExpire;

}
