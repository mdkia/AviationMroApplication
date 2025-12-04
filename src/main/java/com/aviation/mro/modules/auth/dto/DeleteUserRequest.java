package com.aviation.mro.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUserRequest {

    @NotBlank(message = "Deleted by username cannot be blank")
    private String deletedBy;

}
