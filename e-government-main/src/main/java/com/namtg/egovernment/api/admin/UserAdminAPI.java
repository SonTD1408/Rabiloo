package com.namtg.egovernment.api.admin;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.dto.user.UserDto;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class UserAdminAPI {
    @Autowired
    private UserService userService;

    @GetMapping("/getPage")
    public Page<UserEntity> getPage(@RequestParam int size, @RequestParam int page,
                                    @RequestParam String sortDir, @RequestParam String sortField,
                                    @RequestParam String keyword,
                                    @RequestParam String typeActionableFilter) {
        Pageable pageable = PageableUtils.from(page, size, sortDir, sortField);

        return userService.getPage(keyword, typeActionableFilter, pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<ServerResponseDto> createOrUpdate(@ModelAttribute UserDto userDto) {
        return ResponseEntity.ok(userService.createOrUpdate(userDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ServerResponseDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(userService.detail(id));
    }

    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<ServerResponseDto> changeStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.changeStatus(id));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ServerResponseDto> changePassword(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                                                            @RequestParam String currentPassword,
                                                            @RequestParam String newPassword) {
        if (customUserDetail == null) {
            return ResponseEntity.ok(new ServerResponseDto(ResponseCase.ERROR));
        }

        return ResponseEntity.ok(userService.changePassword(customUserDetail.getId(), currentPassword, newPassword));
    }

    @GetMapping("/my_profile/{currentUserId}")
    public ResponseEntity<ServerResponseDto> getMyProfile(@PathVariable Long currentUserId) {
        return ResponseEntity.ok(userService.getMyProfile(currentUserId));
    }

    @PostMapping("/update_profile")
    public ResponseEntity<ServerResponseDto> updateMyProfile(@ModelAttribute UserDto userDto) {
        return ResponseEntity.ok(userService.updateMyProfile(userDto));
    }
}
