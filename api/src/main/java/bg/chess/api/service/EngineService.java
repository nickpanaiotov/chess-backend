package bg.chess.api.service;

import bg.chess.api.ChessApplication;
import bg.chess.api.model.*;
import bg.chess.engine.StockfishClient;
import bg.chess.engine.enums.Option;
import bg.chess.engine.enums.Query;
import bg.chess.engine.enums.QueryType;
import bg.chess.engine.enums.Variant;
import bg.chess.engine.exceptions.StockfishInitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

@Service
public class EngineService {

    @Value("${engine.folder:#{null}}")
    private Resource resource;

    @Value("${engine.instances:#{10}}")
    private int instances;

    @Value("${engine.number-of-threads:#{4}}")
    private int numberOfThreads;

    @Value("${engine.minimum-thinking-time:#{1000}}")
    private long minimumThinkingTime;

    @Value("${engine.default-skill-level:#{10}}")
    private int defaultSkillLevel;

    private StockfishClient client;

    @PostConstruct
    public void initEngine() throws StockfishInitException, IOException {
        this.client = new StockfishClient.Builder()
                .setPath(resource.getURI().getPath())
                .setInstances(instances)
                .setOption(Option.Threads, numberOfThreads) // Number of threads that Stockfish will use
                .setOption(Option.Minimum_Thinking_Time, minimumThinkingTime) // Minimum thinking time Stockfish will take
                .setOption(Option.Skill_Level, defaultSkillLevel)// Stockfish skill level 0-20
                .setVariant(Variant.BMI2) // Stockfish Variant
                .build();
    }



    public Mono<String> checkers(String boardFen) {
        Query moveQuery = new Query.Builder(QueryType.Checkers)
                .setFen(boardFen)
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }

    public Mono<String> legalMoves(String boardFen) {
        Query moveQuery = new Query.Builder(QueryType.Legal_Moves)
                .setFen(boardFen)
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }

    public Mono<String> bestMove(Game game) {
        var elo = 10;
        if (game.getTurn().equals(Side.WHITE) && game.getWhitePlayer().getType().equals(PlayerType.ENGINE)) {
            if (game.getWhitePlayer().getElo() != 0) {
                elo = game.getWhitePlayer().getElo();
            }
        } else if (game.getTurn().equals(Side.BLACK) && game.getBlackPlayer().getType().equals(PlayerType.ENGINE)) {
            if (game.getWhitePlayer().getElo() != 0) {
                elo = game.getWhitePlayer().getElo();
            }
        }

        Random r = new Random();
        var time = r.nextInt((1000 - 100) + 1) + 100;

        Query moveQuery = new Query.Builder(QueryType.Best_Move)
                .setFen(game.getBoardFen())
                .setDifficulty(elo)
                .setDepth(1)
                .setMovetime(time)
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }

    public Mono<String> bestMove(String boardFen) {
        Query moveQuery = new Query.Builder(QueryType.Best_Move)
                .setFen(boardFen)
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }

    public Mono<String> move(String boardFen, Move move) {
        Query moveQuery = new Query.Builder(QueryType.Make_Move)
                .setFen(boardFen)
                .setMove(move.toString())
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }

    public Mono<String> test() {
        Query moveQuery = new Query.Builder(QueryType.Legal_Moves)
                .setFen("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 0 1")
//                .setMove(move.toString())
                .build();

        return Mono.create(consumer -> {
            this.client.submit(moveQuery, consumer::success);
        });
    }
}
