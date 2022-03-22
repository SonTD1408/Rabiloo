package com.namtg.egovernment.service.user;

import com.namtg.egovernment.entity.user.RoleEntity;
import com.namtg.egovernment.repository.user.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> getList() {
        return roleRepository.findAll();
    }

    public Map<Long, RoleEntity> getMapRoleById(List<Long> listRoleId) {
        List<RoleEntity> listRole = getList();
        return listRole
                .stream()
                .collect(Collectors.toMap(RoleEntity::getId, Function.identity()));
    }

    public RoleEntity getById(Long roleId) {
        RoleEntity roleEntity = roleRepository.getById(roleId);
        return roleEntity != null ? roleEntity : null;
    }

    public Set<Long> getSetAllRoleId() {
        return roleRepository.getSetAllRoleId();
    }
}
