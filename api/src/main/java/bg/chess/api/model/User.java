package bg.chess.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user")
@AllArgsConstructor
public class User extends Player implements Serializable {

    @Indexed(unique = true)
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(min = 60, max = 60)
    private String password;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    @JsonIgnore
    @DBRef(db = "authority")
    private Set<Authority> authorities = new HashSet<>();

    public User() {
        this.type = PlayerType.HUMAN;
    }
}
