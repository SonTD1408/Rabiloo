package com.namtg.egovernment.service.token;

import com.namtg.egovernment.entity.token.TokenEntity;
import com.namtg.egovernment.repository.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public String generateToken() {
        String token = UUID.randomUUID().toString();
        String generateToken = generateToken(token);
        String timeNow = String.valueOf(System.currentTimeMillis());
        return generateToken + timeNow;
    }

    private String generateToken(String token) {
        final TokenEntity myToken = new TokenEntity(token);
        return token + myToken.hashCode();
    }

    public void save(TokenEntity token) {
        tokenRepository.save(token);
    }

    public TokenEntity validateToken(String token, Integer tokenType) {
        return tokenRepository.findByTokenAndTokenType(token, tokenType);
    }

    public void deleteToken(TokenEntity tokenEntity) {
        tokenEntity.setDeleted(true);
        tokenRepository.save(tokenEntity);
    }
}
