package com.namtg.egovernment.repository.user;

import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.dto.user.UserIdAndNameRoleInterface;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.enum_common.TypeRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "select u from UserEntity u " +
            "where u.email = ?1 and u.status = 1 and u.isDeleted = false")
    UserEntity findByEmailToCheckLogin(String email);

    List<UserEntity> findByIdInAndIsDeletedFalse(Collection<Long> listUserId);

    @Query(value = "select u from UserEntity u " +
            "where u.name like concat ('%', ?1, '%') and u.isDeleted = false")
    Page<UserEntity> getPage(String keyword, Pageable pageable);

    UserEntity findByIdAndIsDeletedFalse(Long userId);

    @Query(value = "select u from UserEntity u " +
            "where u.id = ?1 and u.status = 1 and u.isDeleted = false")
    UserEntity getById(Long userId);

    @Query(value = "select u.name from UserEntity u " +
            "where u.id = ?1 and u.isDeleted = false")
    String getNameUserByUserId(Long userId);

    @Query(value = "select u.* from user as u " +
            "inner join user_role as ur on u.id = ur.user_id " +
            "inner join role as r on r.id = ur.role_id " +
            "where r.name in ?1", nativeQuery = true)
    List<UserEntity> getListAdmin(Set<String> setRoleAdmin);

    @Query(value = "select u.* from user as u " +
            "inner join user_role as ur on u.id = ur.user_id " +
            "inner join role as r on r.id = ur.role_id " +
            "inner join actionable on actionable.user_id = u.id " +
            "where r.name in ?1 " +
            "and actionable.is_manage_comment_post = true", nativeQuery = true)
    List<UserEntity> getListAdminCanReceiveNotificationCommentPost(Set<String> setRoleAdmin);

    @Query(value = "select u.* from user as u " +
            "inner join user_role as ur on u.id = ur.user_id " +
            "inner join role as r on r.id = ur.role_id " +
            "inner join actionable on actionable.user_id = u.id " +
            "where r.name in ?1 " +
            "and actionable.is_manage_conclusion_post = true", nativeQuery = true)
    List<UserEntity> getListAdminCanReceiveNotificationConclusion(Set<String> setRoleAdmin);

    @Query(value = "select u.id as userId, r.name as nameRole from role as r " +
            "inner join user_role as ur on r.id = ur.role_id " +
            "inner join user as u on u.id = ur.user_id " +
            "where u.id in ?1 " +
            "and u.is_deleted = false", nativeQuery = true)
    List<UserIdAndNameRoleInterface> getListUserIdAndNameRole(List<Long> listUserId);

    @Query(value = "select u.id from user as u " +
            "inner join user_role as ur on u.id = ur.user_id " +
            "inner join role as r on r.id = ur.role_id " +
            "inner join actionable on actionable.user_id = u.id " +
            "where r.name in ?1 " +
            "and actionable.is_manage_post = true", nativeQuery = true)
    List<Long> getListAdminIdCanReceiveNotificationApprovePost(Set<String> setRoleAdmin);

    @Query(value = "select count(u.id) from user as u " +
            "where u.email = ?1 and u.is_deleted = false", nativeQuery = true)
    int countEmail(String email);

    @Query(value = "select u from UserEntity u " +
            "where u.email = ?1 and u.isDeleted = false")
    UserEntity findByEmailAndIsDeletedFalse(String email);
}
