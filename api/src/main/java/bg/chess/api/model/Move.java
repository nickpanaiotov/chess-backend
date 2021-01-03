package bg.chess.api.model;

import com.github.bhlangonijr.chesslib.Constants;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import lombok.Data;

@Data
public class Move {
    private Square from;
    private Square to;
    private Piece promotion;

    public String toString() {
        String promo = "";
        if (this.promotion != null && !Piece.NONE.equals(this.promotion)) {
            promo = Constants.getPieceNotation(this.promotion);
        }

        return this.from.toString().toLowerCase() + this.to.toString().toLowerCase() + promo.toLowerCase();
    }

    public static Move of(String an) {
        Move move = new Move();
        move.setFrom(Square.fromValue(an.toUpperCase().substring(0,2)));
        move.setTo(Square.fromValue(an.toUpperCase().substring(2,4)));

        return move;
    }
}
