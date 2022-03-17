package com.namtg.egovernment.repository.token;

import com.namtg.egovernment.entity.token.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    @Query(value = "select t from TokenEntity t " +
            "where t.token = ?1 and t.tokenType = ?2 and t.isDeleted = false")
    TokenEntity findByTokenAndTokenType(String token, Integer tokenType);
}
