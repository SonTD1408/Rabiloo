package com.namtg.egovernment.repository.news;

import com.namtg.egovernment.entity.news.NewsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    @Query(value = "select n from NewsEntity n " +
            "where n.title like concat('%', ?1, '%') and n.isDeleted = false")
    Page<NewsEntity> getPage(String keyword, Pageable pageable);

    NewsEntity findByIdAndIsDeletedFalse(Long newsId);

    @Query(value = "select n from NewsEntity n " +
            "where n.isDeleted = false and n.createdTime = (select max(n.createdTime) from NewsEntity n where n.isDeleted = false) ")
    NewsEntity getNewsLatest();

    @Query(value = "select n from NewsEntity n " +
            "where n.isDeleted = false")
    List<NewsEntity> getListNews();

    @Modifying
    @Query(value = "delete from NewsEntity n " +
            "where n.subFieldId in ?1")
    void deleteByListSubFieldId(List<Long> listSubFieldIdBeDeleted);

    @Query(value = "select n.* from news as n " +
            "where n.is_deleted = false order by n.created_time desc limit 5", nativeQuery = true)
    List<NewsEntity> getListTop5NewsLatest();

    @Query(value = "select count(n) from NewsEntity n " +
            "where n.title = ?1 and n.isDeleted = false")
    int countNewsExist(String title);

    @Query(value = "select count(n) from NewsEntity n " +
            "where n.title = ?1 and n.id <> ?2 and n.isDeleted = false")
    int countNewsExist(String title, Long newsId);

    NewsEntity findBySeoAndIsDeletedFalse(String seo);
}
