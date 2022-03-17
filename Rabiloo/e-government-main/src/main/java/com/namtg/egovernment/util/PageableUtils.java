package com.namtg.egovernment.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static Pageable from(int page, int size, String sortDir, String sortField) {
        Pageable pageable;
        if ("asc".equals(sortDir)) {
            pageable = PageRequest.of(page - 1, size, Sort.Direction.ASC, sortField);
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, sortField);
        }
        return pageable;
    }

    public static Pageable from(int page, int size) {
        return new PageRequest(page - 1, size);
    }

}
