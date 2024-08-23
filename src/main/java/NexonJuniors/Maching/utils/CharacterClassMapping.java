package NexonJuniors.Maching.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterClassMapping {

    private static final Map<String, List<String>> characterClassMapping = new HashMap<>();

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 파일을 InputStream으로 읽어 Map으로 변환
            ClassPathResource resource = new ClassPathResource("characterClassMapping.json");
            InputStream inputStream = resource.getInputStream();

            // JSON 파일을 Map으로 변환하여 characterClassMapping에 저장
            characterClassMapping.putAll(objectMapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, List<String>>>() {}
            ));

        } catch (IOException e) {
            // 파일 읽기 실패 시 로그를 남기고 빈 맵을 사용
            e.printStackTrace();
            System.err.println("Failed to load 직업클래스 mapping from JSON file.");
        }
    }

    public static Map<String, List<String>> getCharacterClassMapping() {
        return characterClassMapping;
    }
}