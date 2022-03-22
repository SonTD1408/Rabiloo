package com.namtg.egovernment.dto.user;

import com.namtg.egovernment.entity.actionable.ActionableEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailResponse {
    private UserEntity userEntity;
    private ActionableEntity actionableEntity;
}
