package com.sontd.bookingpj.repository.user;

import com.sontd.bookingpj.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
//import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("select u from UserEntity u where u.userName= ?1 and u.password= ?2")
    UserEntity getUserByUserNameAndPassword(String userName, String password);

    @Query("select u from UserEntity u where u.role= 2")
    List<UserEntity> getAgency();

    @Modifying
    @Query("update UserEntity u set u.status=1 where u.id=?1")
    void acceptAgency(long id);
}
