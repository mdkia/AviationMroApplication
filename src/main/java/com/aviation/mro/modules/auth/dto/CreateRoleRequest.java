package com.aviation.mro.modules.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleRequest {
    private String name;
    private String displayName;
    private String description;

}