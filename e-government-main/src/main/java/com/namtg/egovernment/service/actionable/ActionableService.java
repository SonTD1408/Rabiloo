package com.namtg.egovernment.service.actionable;

import com.namtg.egovernment.entity.actionable.ActionableEntity;
import com.namtg.egovernment.repository.actionable.ActionableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActionableService {
    @Autowired
    private ActionableRepository actionableRepository;

    public ActionableEntity findByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return actionableRepository.findByUserId(userId);
    }

    public void save(ActionableEntity actionableEntity) {
        actionableRepository.save(actionableEntity);
    }

    public Optional<ActionableEntity> getOptionalByUserId(Long userId) {
        return actionableRepository.getOptionalByUserId(userId);
    }

    private List<ActionableEntity> findByListUserId(List<Long> listUserId) {
        if (listUserId.isEmpty()) {
            return Collections.emptyList();
        }
        return actionableRepository.findByListUserId(listUserId);
    }

    public Map<Long, ActionableEntity> getMapActionableByUserId(List<Long> listUserId) {
        List<ActionableEntity> listActionable = findByListUserId(listUserId);
        return listActionable
                .stream()
                .collect(Collectors.toMap(ActionableEntity::getUserId, Function.identity()));
    }
}
