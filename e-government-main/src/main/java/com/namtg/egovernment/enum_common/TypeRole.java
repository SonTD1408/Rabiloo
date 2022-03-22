package com.namtg.egovernment.enum_common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum TypeRole {
    GOVERNMENT, PROVINCE, DISTRICT, COMMUNE, CITIZEN;

    public static Set<TypeRole> setRoleAdmin = new HashSet<>(Arrays.asList(TypeRole.GOVERNMENT, TypeRole.PROVINCE, TypeRole.DISTRICT, TypeRole.COMMUNE));
}
