package NexonJuniors.Maching.user;

import NexonJuniors.Maching.user.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(SignUpDto signUpDto){
        String userId = signUpDto.getUserId();
        String userPw = signUpDto.getUserPw();

        UserEntity userEntity = new UserEntity(userId, userPw);
        userRepository.save(userEntity);
    }
}
