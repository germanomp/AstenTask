package com.astentask.dtos;

import com.astentask.model.Role;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String name;
    private Role role;
}
