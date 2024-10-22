package NexonJuniors.Maching.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String userPw;
//    private List<String> characterList;
//    private Integer authority;

    public UserEntity(String userId, String userPw){
        this.userId = userId;
        this.userPw = userPw;
    }
}
