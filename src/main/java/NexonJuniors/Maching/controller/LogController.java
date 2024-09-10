package NexonJuniors.Maching.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    // 이건 위험한거같음.
    @GetMapping("/{date}")
    public ResponseEntity<List<String>> getLogsByDate(@PathVariable("date") String date) {
        try {
            // 오늘의 로그를 요청할 경우 today.log를 읽습니다.(전체 로그를 읽음)
            String logFileName = "today".equals(date) ? "logs/today.log" : "logs/app." + date + ".log";
            List<String> logs = Files.readAllLines(Paths.get(logFileName));
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 이건 지난 로그들을 리스트로 불러올거임
    @GetMapping("/listLogs")
    public ResponseEntity<List<String>> getLogList() {
        try {
            List<String> logs = Files.list(Paths.get("logs"))
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith("app.") && name.endsWith(".log"))
                    .map(name -> name.substring(4, 14)) // 로그 날짜 형식 추출 (yyyy-MM-dd)
                    .sorted(Comparator.reverseOrder()) // 최근 로그가 먼저 나오도록 정렬
                    .collect(Collectors.toList());

            logs.add(0, "today"); // 목록의 첫 번째 항목으로 "today" 추가
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 채팅 로그를 제외한 다른 매칭 정보를 가져오는 API
    @GetMapping("/filtered/{date}")
    public ResponseEntity<List<String>> getFilteredLogsByDate(@PathVariable("date") String date) {
        try {
            String logFileName = "today".equals(date) ? "logs/today.log" : "logs/app." + date + ".log";
            List<String> logs = Files.readAllLines(Paths.get(logFileName));

            // "캐릭터", "매칭", "채팅방"이 포함된 로그만 필터링
            List<String> filteredLogs = logs.stream()
                    .filter(log -> log.contains("매칭") || log.contains("채팅방"))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filteredLogs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

/*    // 채팅 로그만 가져오는 API 채팅은 유저의 개인정보이므로 동의를 얻어야한다
    @GetMapping("/chattingLog/{date}")
    public ResponseEntity<List<String>> getChattingLogsByDate(@PathVariable("date") String date) {
        try {
            String logFileName = "today".equals(date) ? "logs/today.log" : "logs/app." + date + ".log";
            List<String> logs = Files.readAllLines(Paths.get(logFileName));

            // "채팅로그" 가 포함된 로그만 필터링
            List<String> filteredLogs = logs.stream()
                    .filter(log -> log.contains("채팅 로그"))
                    .collect(Collectors.toList());
            System.out.println(filteredLogs); // 필터링된 로그를 출력
            return ResponseEntity.ok(filteredLogs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }*/

    // 검색 로그만 가져오는 API
    @GetMapping("/searchingLog/{date}")
    public ResponseEntity<List<String>> getSearchingLogsByDate(@PathVariable("date") String date) {
        try {
            String logFileName = "today".equals(date) ? "logs/today.log" : "logs/app." + date + ".log";
            List<String> logs = Files.readAllLines(Paths.get(logFileName));

            List<String> filteredLogs = logs.stream()
                    .filter(log -> log.contains("캐릭터"))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filteredLogs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 채팅로그,검색로그,매칭,채팅방 제외한 시스템 로그
    @GetMapping("/systemNotChatLog/{date}")
    public ResponseEntity<List<String>> getSystemNotChatLog(@PathVariable("date") String date) {
        try {
            String logFileName = "today".equals(date) ? "logs/today.log" : "logs/app." + date + ".log";
            List<String> logs = Files.readAllLines(Paths.get(logFileName));

            List<String> filteredLogs = logs.stream()
                    .filter(log -> !log.contains("매칭") && !log.contains("채팅") && !log.contains("캐릭터"))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filteredLogs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}