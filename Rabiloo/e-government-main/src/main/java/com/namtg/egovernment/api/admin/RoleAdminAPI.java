package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/role")
public class RoleAdminAPI {
    @Autowired
    private RoleService roleService;

    @GetMapping("/getList")
    public ResponseEntity<ServerResponseDto> getList() {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, roleService.getList()));
    }
}
