package NexonJuniors.Maching.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialRingInfo {
    private boolean isNowUserHasSpecialRing = false; // 현재 적용된 프리셋에 유저는 특수스킬 반지가 있을까, 기본은 false
    private boolean isUserHasNotSpecialRing = false; // 설마 없겠냐만 뉴비는 없을수도있으니까 처리해줌, 기본은 false
    private String specialRingName = ""; // 시드링 이름
    private int specialRingLevel = 0; // 시드링 레벨
}
