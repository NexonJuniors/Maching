package NexonJuniors.Maching.user;

import NexonJuniors.Maching.excption.user.UserException;
import NexonJuniors.Maching.excption.user.UserExceptionCode;
import NexonJuniors.Maching.user.dto.EmailAuthDto;
import NexonJuniors.Maching.user.dto.SignInDto;
import NexonJuniors.Maching.user.dto.SignUpDto;
import NexonJuniors.Maching.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메소드
    public void registerUser(SignUpDto signUpDto){
        String userId = signUpDto.getUserId();
        String userPw = signUpDto.getUserPw();
        String authCode = signUpDto.getAuthCode();

        if(isExistUser(userId)) throw new UserException(UserExceptionCode.EXIST_USER);

        if(!isCheckEmailAuthCode(userId, authCode)) throw new UserException(UserExceptionCode.IS_NOT_VALID_CODE);

        UserEntity userEntity = new UserEntity(userId, passwordEncoder.encode(userPw));
        userRepository.save(userEntity);

        redisUtil.deleteEmailAuthCode(userId);
    }

    // 인증코드 생성 및 인증 메일 발송하는 메소드
    public void IssueEmailAuthCode(EmailAuthDto dto){
        Integer authCode = generateEmailAuthCode();
        String email = dto.getUserId();

        // REDIS 에 이메일 인증 정보 저장
        redisUtil.setEmailAuthCode(email, authCode);
        // 이메일 인증 메일 전송
        sendMail(email, authCode);
    }

    // 로그인 메소드
    public void signIn(SignInDto dto){
        String userId = dto.getUserId();
        String userPw = dto.getUserPw();

        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);

        if(optionalUser.isEmpty()) throw new UserException(UserExceptionCode.NOT_EXIST_USER);

        UserEntity user = optionalUser.get();

        // 패스워드 디코딩 후 입력한 패스워드와 같은 지 비교
        if(!passwordEncoder.matches(userPw, user.getUserPw())) throw new UserException(UserExceptionCode.IS_NOT_VALID_PASSWORD);

        // 토근 발급
    }

    // 이메일 인증 메일을 전송하는 메소드
    private void sendMail(String email, Integer authCode){
        // 이메일 전송 시 내용 구성 설정 객체 생성
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 어디로 보낼 것인지 설정
        simpleMailMessage.setTo(email);
        // 제목 설정
        simpleMailMessage.setSubject("[메칭] 이메일 인증 요청 메일입니다.");
        // 내용 설정
        simpleMailMessage.setText(String.format("아래의 인증 코드를 입력해주세요\n%d", authCode));
        // 메일 전송
        mailSender.send(simpleMailMessage);
    }

    // 인증코드로 사용할 랜덤한 6자리 난수 리턴하는 메소드
    private Integer generateEmailAuthCode(){
        return (int)(Math.random() * 1000000);
    }

    // 이미 존재하는 유저인지 확인하는 메소드
    private boolean isExistUser(String userId){
        return userRepository.existsByUserId(userId);
    }

    // 인증코드를 올바르게 입력했는지 확인하는 메소드
    private boolean isCheckEmailAuthCode(String userId, String authCode){
        Object code = redisUtil.getEmailAuthCode(userId);
        return code != null && authCode.equals(code.toString());
    }
}
