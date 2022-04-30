package cz.maku.rest_storage.model.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

}
