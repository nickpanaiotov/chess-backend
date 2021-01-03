package bg.chess.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class Player {
    @Id
    protected String id;

    protected int elo;

    @Size(min = 1, max = 100)
    protected String name;

    @Size(min = 1, max = 500)
    protected String description;

    protected PlayerType type;

    @JsonIgnore
    @DBRef(db = "game")
    private Set<Game> games = new HashSet<>();
}
