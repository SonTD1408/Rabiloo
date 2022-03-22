package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.field.SubFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sub_field")
public class SubFieldAdminAPI {
    @Autowired
    private SubFieldService subFieldService;

    @GetMapping("/getList/{fieldId}")
    public ResponseEntity<ServerResponseDto> getList(@PathVariable Long fieldId) {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, subFieldService.findByFieldId(fieldId)));
    }
}
