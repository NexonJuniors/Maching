package NexonJuniors.Maching.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDto {
    private String userId;
    private String userPw;
}
