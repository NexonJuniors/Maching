package NexonJuniors.Maching.model;

public class CharacterInfo {

    private BasicInfo basicInfo;
    private StatInfo statInfo;


    @Override
    public String toString() {
        return "CharacterInfo{" +
                "basicInfo=" + basicInfo +
                ", statInfo=" + statInfo +
                '}';
    }


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
}