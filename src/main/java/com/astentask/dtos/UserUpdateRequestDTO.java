package com.astentask.dtos;

import com.astentask.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    @NotBlank(message = "O nome não pode estar vazio")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String name;

    @NotNull(message = "O papel do usuário é obrigatório")
    private Role role;
}
