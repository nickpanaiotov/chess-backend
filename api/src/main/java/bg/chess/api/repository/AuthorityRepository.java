package bg.chess.api.repository;

import bg.chess.api.model.Authority;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
}
