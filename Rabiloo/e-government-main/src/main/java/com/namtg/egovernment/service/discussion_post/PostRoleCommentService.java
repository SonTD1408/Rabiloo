package com.namtg.egovernment.service.discussion_post;

import com.google.common.collect.Lists;
import com.namtg.egovernment.entity.discussion_post.PostRoleCommentEntity;
import com.namtg.egovernment.entity.user.RoleEntity;
import com.namtg.egovernment.repository.discussion_post.PostRoleCommentRepository;
import com.namtg.egovernment.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostRoleCommentService {
    @Autowired
    private PostRoleCommentRepository repository;

    @Autowired
    private RoleService roleService;

    @Transactional
    public void save(Set<Long> listRoleIdAfter, Long discussionPostId) {
        List<PostRoleCommentEntity> listPostRoleComment = repository.findByPostId(discussionPostId);
        Set<Long> listRoleIdBefore = listPostRoleComment
                .stream()
                .map(PostRoleCommentEntity::getRoleId)
                .collect(Collectors.toSet());

        /* add new */
        addNew(listRoleIdBefore, listRoleIdAfter, discussionPostId);

        /* delete */
        deleteNew(listRoleIdBefore, listRoleIdAfter, discussionPostId);

    }

    @Transactional
    public void saveWithAllRole(Set<Long> listRoleIdAfter, Long discussionPostId) {
        List<PostRoleCommentEntity> listEntity = Lists.newArrayListWithCapacity(listRoleIdAfter.size());
        listRoleIdAfter.forEach(roleIdAfter -> {
            PostRoleCommentEntity entity = new PostRoleCommentEntity(discussionPostId, roleIdAfter);
            listEntity.add(entity);
        });
        repository.saveAll(listEntity);
    }

    private void deleteNew(Set<Long> listRoleIdBefore, Set<Long> listRoleIdAfter, Long discussionPostId) {
        if (listRoleIdBefore.isEmpty()) {
            return;
        }

        List<Long> listRoleIdDeleted = listRoleIdBefore
                .stream()
                .filter(idBefore -> !listRoleIdAfter.contains(idBefore))
                .collect(Collectors.toList());

        if (listRoleIdDeleted.isEmpty()) {
            return;
        }
        repository.deleteByRoleIdInAndPostId(listRoleIdDeleted, discussionPostId);
    }

    private void addNew(Set<Long> listRoleIdBefore, Set<Long> listRoleIdAfter, Long discussionPostId) {
        List<Long> listRoleIdAdded = listRoleIdAfter
                .stream()
                .filter(idAfter -> !listRoleIdBefore.contains(idAfter))
                .collect(Collectors.toList());

        if (listRoleIdAdded.isEmpty()) {
            return;
        }
        List<PostRoleCommentEntity> listDiscussionPostRoleComment = new ArrayList<>();
        listRoleIdAdded.forEach(roleIdAdded -> {
            PostRoleCommentEntity entity = new PostRoleCommentEntity(discussionPostId, roleIdAdded);
            listDiscussionPostRoleComment.add(entity);
        });

        repository.saveAll(listDiscussionPostRoleComment);
    }

    public Map<Long, List<RoleEntity>> getMapListRoleByPostId(List<Long> listPostId) {
        if (listPostId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PostRoleCommentEntity> listPostRole = repository.findByPostIdIn(listPostId);
        Map<Long, List<Long>> mapListRoleIdByPostId = listPostRole
                .stream()
                .collect(Collectors.groupingBy(PostRoleCommentEntity::getDiscussionPostId,
                        Collectors.mapping(PostRoleCommentEntity::getRoleId, Collectors.toList())));

        List<Long> listRoleId = listPostRole
                .stream()
                .map(PostRoleCommentEntity::getRoleId)
                .collect(Collectors.toList());
        Map<Long, RoleEntity> mapRoleByRoleId = roleService.getMapRoleById(listRoleId);

        Map<Long, List<RoleEntity>> mapResult = new HashMap<>(listPostId.size());

        listPostId.forEach(postId ->
                mapResult.put(postId, convertListRoleIdToListRole(mapListRoleIdByPostId.get(postId), mapRoleByRoleId))
        );

        return mapResult;
    }

    private List<RoleEntity> convertListRoleIdToListRole(List<Long> listRoleId, Map<Long, RoleEntity> mapRoleByRoleId) {
        List<RoleEntity> listRole = Lists.newArrayListWithCapacity(listRoleId.size());
        listRoleId.forEach(roleId -> listRole.add(mapRoleByRoleId.get(roleId)));
        return listRole;
    }

    public List<RoleEntity> getListRole(Long postId) {
        List<PostRoleCommentEntity> listPostRole = repository.findByPostId(postId);
        List<Long> listRoleId = listPostRole
                .stream()
                .map(PostRoleCommentEntity::getRoleId)
                .collect(Collectors.toList());

        Map<Long, RoleEntity> mapRoleByRoleId = roleService.getMapRoleById(listRoleId);
        List<RoleEntity> result = Lists.newArrayListWithCapacity(listRoleId.size());

        listRoleId.forEach(roleId -> result.add(mapRoleByRoleId.get(roleId)));
        return result;
    }

    @Transactional
    public void deleteByPostId(Long postId) {
        repository.deleteByPostId(postId);
    }
}
