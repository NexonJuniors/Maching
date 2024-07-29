package NexonJuniors.Maching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class CharacterSearchController {

    @Autowired
    private Dotenv dotenv;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/character")
    public String getCharacter(@RequestParam String characterName, Model model) {
        try {
            String API_KEY = dotenv.get("API_KEY");
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String ocidUrl = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + encodedName;
            String ocid = getOcid(ocidUrl, API_KEY); // ocid를 제일 먼저 가져오기

            if (ocid == null || ocid.isEmpty()) {
                model.addAttribute("error", "Character not found");
                return "home";
            }

            String characterUrl = "https://open.api.nexon.com/maplestory/v1/character/basic?ocid=" + ocid;
            String characterInfo = getCharacterInfo(characterUrl, API_KEY); // 캐릭터 basic api호출, date default

            model.addAttribute("characterInfo", characterInfo);
            return "character"; // character.html에서 노출
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            return "home";
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