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
    private int presetNo; // 현재 설정된 장비 프리셋(!날짜 바뀌어도 무조건 현시점임!)

    @JsonProperty("item_equipment")  // JSON의 필드 이름과 매핑
    private List<ItemEquipment> itemEquipment; // 장착중인 장비 리스트

    @JsonProperty("item_equipment_preset_1")
    private List<ItemEquipment> itemEquipmentPreset1; // 장비프리셋1

    @JsonProperty("item_equipment_preset_2")
    private List<ItemEquipment> itemEquipmentPreset2; // 장비프리셋2

    @JsonProperty("item_equipment_preset_3")
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
        private ItemOption itemTotalOption; //전체옵션
        private ItemOption itemBaseOption; //기본옵션
        private String potentialOptionGrade; //윗잠재등급
        private String additionalPotentialOptionGrade; //에디잠재등급
        private String potentialOption1; //윗잠1
        private String potentialOption2; //윗잠2
        private String potentialOption3; //윗잠3
        private String additionalPotentialOption1; //아랫잠1
        private String additionalPotentialOption2; //이랫잠2
        private String additionalPotentialOption3; //아랫잠3
        private int equipmentLevelIncrease; //착용레벨증가
        private ItemExceptionalOption itemExceptionalOption; //익셉셔널 강화
        private ItemAddOption itemAddOption; //추옵
        private int growthExp; //아이템성장경험치
        private int growthLevel; //아이템성장레벨
        private String scrollUpgrade; //업글횟수
        private String cuttableCount; //가횟
        private String goldenHammerFlag; //황망재련1 이외 미적용
        private String scrollResilienceCount; //복구가능횟수
        private String scrollUpgradableCount; //업글가능횟수
        private String soulName; //무기소울명
        private String soulOption; //소울옵션
        private ItemOption itemEtcOption; //기타옵션
        private String starforce; //스타포스레벨
        private String starforceScrollFlag; //놀장강사용여부 1:사용 0미사용
        private ItemOption itemStarforceOption; //스타포스옵션정보

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
    public static class ItemOption { // 추옵(스타포스 장비추옵등등)
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
