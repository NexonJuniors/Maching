package NexonJuniors.Maching.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CharacterEquipmentInfo {
    private LocalDate searchDate; // LocalDate로 변경
    private boolean isRealTime; // boolean으로 변경

    @JsonProperty("date")
    private String date; // 데이터 날짜

    @JsonProperty("character_gender")
    private String characterGender; // 성별

    @JsonProperty("character_class")
    private String characterClass; // 직업

    @JsonProperty("preset_no")
    private int presetNo; // 현재 설정된 장비 프리셋

    @JsonProperty("item_equipment")  // JSON의 필드 이름과 매핑
    private List<ItemEquipment> itemEquipment; // 장착중인 장비 리스트

    @JsonProperty("item_equipment_preset1")
    private List<ItemEquipment> itemEquipmentPreset1; // 장비프리셋1

    @JsonProperty("item_equipment_preset2")
    private List<ItemEquipment> itemEquipmentPreset2; // 장비프리셋2

    @JsonProperty("item_equipment_preset3")
    private List<ItemEquipment> itemEquipmentPreset3; // 장비프리셋3

    @JsonProperty("title")
    private Title title; // 캐릭터 타이틀( 별거 안나옴)

    @JsonProperty("dragon_equipment")
    private List<ItemEquipment> dragonEquipment; //에반 전용 장비창

    @JsonProperty("mechanic_equipment")
    private List<ItemEquipment> mechanicEquipment; //메카닉 전용 장비창

    public void setIsRealTime(boolean realTime) {
        isRealTime = realTime;
    }

    @Getter
    @Setter
    public static class ItemEquipment {

        private String itemEquipmentPart;
        private String itemEquipmentSlot;
        private String itemIcon;

        @JsonProperty("item_name")
        private String itemName; //진짜 아이템 이름

        private String itemDescription;
        private String itemShapeName; //모루
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

        @JsonProperty("special_ring_level")
        private int specialRingLevel; //이게 1이상이면 시드링을 끼고있는거임

        private String dateExpire;

        @Override
        public String toString() {
            return "장착중인 장비 : " + itemName ;
        }
    }

    @Getter
    @Setter
    public static class ItemOption { // 아이템 옵션
        private String str;
        private String dex;
        private String int_; // int로 하면 정수형 int랑 충돌나서 _ 붙임
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
    public static class ItemExceptionalOption { // 익셉셔널 강화
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
    public static class ItemAddOption { // 잠재능력
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
    public static class Title {
        private String titleName;
        private String titleIcon;
        private String titleDescription;
        private String dateExpire;
        private String dateOptionExpire;
    }
}
