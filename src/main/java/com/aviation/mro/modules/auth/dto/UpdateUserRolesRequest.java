package com.aviation.mro.modules.auth.dto;

import java.util.Set;

public record UpdateUserRolesRequest(Set<Long> roleIds) {}