package bg.chess.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Game {
    public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Id
    private String gameId;
    private Long dateStarted;

    @JsonDeserialize(as=User.class)
    private Player whitePlayer;

    @JsonDeserialize(as=User.class)
    private Player blackPlayer;

    private GameMode mode;

    private List<Move> halfMoves;

    private String boardFen;

    private GameResult result;

    public Side getTurn() {
        return this.getHalfMoves().size() % 2 == 0 ? Side.WHITE : Side.BLACK;
    }
}
