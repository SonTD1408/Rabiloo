package com.namtg.egovernment.repository.discussion_post;

import com.namtg.egovernment.entity.discussion_post.PostUserBlockedCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostUserBlockedCommentRepository extends JpaRepository<PostUserBlockedCommentEntity, Long> {
}
