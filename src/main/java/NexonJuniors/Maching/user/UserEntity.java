package NexonJuniors.Maching.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
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
