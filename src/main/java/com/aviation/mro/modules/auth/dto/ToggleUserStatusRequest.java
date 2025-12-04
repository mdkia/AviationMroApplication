package com.aviation.mro.modules.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToggleUserStatusRequest {
    private boolean enabled;

}
