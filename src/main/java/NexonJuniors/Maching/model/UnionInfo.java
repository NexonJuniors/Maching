package NexonJuniors.Maching.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnionInfo {

    @JsonProperty("date")
    private String date;

    @JsonProperty("union_level")
    private String union_level;

    @JsonProperty("union_grade")
    private String union_grade;

    @JsonProperty("union_artifact_level")
    private String union_artifact_level;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUnion_level() {
        return union_level;
    }

    public void setUnion_level(String union_level) {
        this.union_level = union_level;
    }

    public String getUnion_grade() {
        return union_grade;
    }

    public void setUnion_grade(String union_grade) {
        this.union_grade = union_grade;
    }

    public String getUnion_artifact_level() {
        return union_artifact_level;
    }

    public void setUnion_artifact_level(String union_artifact_level) {
        this.union_artifact_level = union_artifact_level;
    }

    // Getters and Setters

}