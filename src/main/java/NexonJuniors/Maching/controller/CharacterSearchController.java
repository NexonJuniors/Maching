package NexonJuniors.Maching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.cdimascio.dotenv.Dotenv;

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

    @GetMapping("/character")
    public String getCharacter(@RequestParam String characterName, Model model) {
        try {
            String API_KEY = dotenv.get("API_KEY");
            String encodedName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);
            String urlString = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + encodedName;
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-nxopen-api-key", API_KEY);

            int responseCode = connection.getResponseCode();

            BufferedReader in;
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            model.addAttribute("apiResponse", response.toString());
            return "home";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            return "home";
        }
    }
}