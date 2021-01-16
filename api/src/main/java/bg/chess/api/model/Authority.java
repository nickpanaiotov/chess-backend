package bg.chess.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode
@Document(collection = "authority")
public class Authority implements Serializable, GrantedAuthority {
    @Id
    private String id;

    @NotNull
    @Size(max = 50)
    @Indexed(unique = true)
    private String name;

    public Authority() {
    }

    public Authority(@NotNull @Size(max = 50) String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
