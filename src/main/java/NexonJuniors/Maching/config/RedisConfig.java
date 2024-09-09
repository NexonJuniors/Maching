package NexonJuniors.Maching.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@DependsOn("dotenv")
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    // REDIS 연결 정보 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        // REDIS 설정 객체 생성
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        // REDIS Host 설정
        redisConfiguration.setHostName(host);
        // REDIS Port 설정
        redisConfiguration.setPort(port);

        redisConfiguration.setUsername(username);
        redisConfiguration.setPassword(password);

        // REDIS 연결 객체 생성 후 설정 삽입
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Primary
    @Bean
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // Redis 연결 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 데이터 직렬화 역직렬화 기능 추가
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
