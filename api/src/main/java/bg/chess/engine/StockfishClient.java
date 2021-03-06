package bg.chess.engine;

import bg.chess.engine.enums.Option;
import bg.chess.engine.enums.Query;
import bg.chess.engine.enums.Variant;
import bg.chess.engine.exceptions.StockfishInitException;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class StockfishClient {
    private final ExecutorService executor;
    private final ExecutorService callback;
    private final Queue<Stockfish> engines;

    public StockfishClient(String path, int instances, Variant variant, Set<Option> options) throws StockfishInitException {
        executor = Executors.newFixedThreadPool(instances);
        callback = Executors.newSingleThreadExecutor();
        engines = new ArrayBlockingQueue<>(instances);

        for (int i = 0; i < instances; i++)
            engines.add(new Stockfish(path, variant, options.toArray(new Option[options.size()])));
    }

    public void submit(Query query) {
        submit(query, null);
    }

    public void submit(Query query, Consumer<String> result) {
        executor.submit(() -> {
            Stockfish engine = engines.remove();
            String output;

            switch (query.getType()) {
                case Best_Move:
                    output = engine.getBestMove(query);
                    break;
                case Make_Move:
                    output = engine.makeMove(query);
                    break;
                case Legal_Moves:
                    output = engine.getLegalMoves(query);
                    break;
                case Checkers:
                    output = engine.getCheckers(query);
                    break;
                default:
                    output = null;
                    break;
            }

            callback.submit(() -> result.accept(output));
            engines.add(engine);
        });
    }

    public static class Builder {
        private final Set<Option> options = new HashSet<>();
        private Variant variant = Variant.DEFAULT;
        private String path = null;
        private int instances = 1;

        public final Builder setInstances(int num) {
            instances = num;
            return this;
        }

        public final Builder setVariant(Variant v) {
            variant = v;
            return this;
        }

        public final Builder setOption(Option o, long value) {
            options.add(o.setValue(value));
            return this;
        }

        public final Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public final StockfishClient build() throws StockfishInitException {
            return new StockfishClient(path, instances, variant, options);
        }
    }
}
