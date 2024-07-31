package NexonJuniors.Maching.utils;


import NexonJuniors.Maching.model.BasicInfo;
import NexonJuniors.Maching.model.CharacterInfo;
import NexonJuniors.Maching.model.StatInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class ApiUtil {
    @Autowired
    private Dotenv dotenv;
    @Autowired
    private ObjectMapper objectMapper;

    public CharacterInfo getCharacter(String characterName) {
        try{
            String API_KEY = dotenv.get("API_KEY");
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String ocidUrl = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + encodedName;
            String ocid = getOcid(ocidUrl, API_KEY); // ocid를 제일 먼저 가져오기

            // 기본 정보
            String basicUrl = "https://open.api.nexon.com/maplestory/v1/character/basic?ocid=" + ocid;
            String basicInfoJson = getCharacterInfo(basicUrl, API_KEY); // 캐릭터 basic api호출, date default

            // 종합 능력치
            String statUrl = "https://open.api.nexon.com/maplestory/v1/character/stat?ocid=" + ocid;
            String statInfoJson = getCharacterInfo(statUrl, API_KEY);

            // JSON을 객체로 변환
            BasicInfo basicInfo = objectMapper.readValue(basicInfoJson, BasicInfo.class);
            StatInfo statInfo = objectMapper.readValue(statInfoJson, StatInfo.class);

            // CharacterInfo 객체 생성 및 데이터 설정
            CharacterInfo characterInfo = new CharacterInfo();
            characterInfo.setBasicInfo(basicInfo);
            characterInfo.setStatInfo(statInfo);

            return characterInfo;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getOcid(String urlString, String apiKey) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-nxopen-api-key", apiKey);

        int responseCode = connection.getResponseCode();
        BufferedReader in;

        if (responseCode == 200) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonNode jsonNode = objectMapper.readTree(response.toString());
        return jsonNode.get("ocid").asText();
    }

    private String getCharacterInfo(String urlString, String apiKey) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-nxopen-api-key", apiKey);

        int responseCode = connection.getResponseCode();
        BufferedReader in;

        if (responseCode == 200) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
