package cz.maku.rest_storage.service;

import cz.maku.rest_storage.model.tokens.Token;
import cz.maku.rest_storage.model.tokens.TokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthTokensValidationService {

    @Autowired
    private TokensRepository tokensRepository;

    public boolean validateToken(String authenticationToken) {
        Optional<Token> optionalToken = tokensRepository.findByToken(authenticationToken);
        return optionalToken.isPresent() && optionalToken.get().isActive();
    }

}
