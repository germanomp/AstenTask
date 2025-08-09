package com.astentask.mapper;

import com.astentask.dtos.UserResponseDTO;
import com.astentask.model.User;

public class UserMapper {

    public static UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
}
