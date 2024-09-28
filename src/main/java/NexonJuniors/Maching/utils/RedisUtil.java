package NexonJuniors.Maching.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    // 이메일 인증 코드 저장
    // [authCode-이메일 : 인증코드] 형태로 저장
    // 데이터 만료시간은 5분 ( 300 초 )
    public void setEmailAuthCode(String email, Integer authCode){
        ValueOperations<String, String> data = redisTemplate.opsForValue();
        String key = String.format("authCode-%s",email);
        data.set(key, String.valueOf(authCode), Duration.ofSeconds(300));
    }

    // 이메일 인증이 완료되면 REDIS 에 저장된 인증코드를 삭제하는 메소드
    public void deleteEmailAuthCode(String email){
        String key = String.format("authCode-%s", email);
        redisTemplate.delete(key);
    }

    // 해당하는 이메일로 보내진 인증코드를 반환하는 메소드
    public Object getEmailAuthCode(String email){
        String key = String.format("authCode-%s",email);
        return redisTemplate.opsForValue().get(key);
    }
}
