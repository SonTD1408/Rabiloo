package com.namtg.egovernment.api.user;

import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.service.field.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/field")
public class FieldAPI {
    @Autowired
    private FieldService fieldService;

    @GetMapping("/getList")
    public ResponseEntity<ServerResponseDto> getList() {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, fieldService.getList()));
    }
}
