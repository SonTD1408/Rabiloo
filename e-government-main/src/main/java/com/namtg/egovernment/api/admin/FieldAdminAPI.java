package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.dto.field.FieldDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.field.FieldEntity;
import com.namtg.egovernment.service.field.FieldService;
import com.namtg.egovernment.util.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/field")
public class FieldAdminAPI {

    @Autowired
    private FieldService fieldService;

    @GetMapping("getPage")
    public Page<FieldEntity> getPage(@RequestParam int size, @RequestParam int page,
                                     @RequestParam String sortDir, @RequestParam String sortField,
                                     @RequestParam String keyword) {
        Pageable pageable = PageableUtils.from(page, size, sortDir, sortField);

        return fieldService.getPage(keyword, pageable);
    }

    @GetMapping("/getList")
    public ResponseEntity<ServerResponseDto> getList() {
        return ResponseEntity.ok(new ServerResponseDto(ResponseCase.SUCCESS, fieldService.getList()));
    }

    @PostMapping("/save")
    public ResponseEntity<ServerResponseDto> save(@ModelAttribute FieldDto fieldDto){
        return ResponseEntity.ok(fieldService.save(fieldDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.detail(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<ServerResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.delete(id));
    }

}
