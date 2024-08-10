package NexonJuniors.Maching.chatting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMessage {
    private Long roomId;
    private String sender;
    private String message;
    private String time;
}
