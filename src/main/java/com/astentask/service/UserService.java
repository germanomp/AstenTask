package com.astentask.service;

import com.astentask.dtos.UserResponseDTO;
import com.astentask.dtos.UserUpdateRequestDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.mapper.UserMapper;
import com.astentask.model.Role;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Buscando usuário com ID {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return UserMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String name, String email, Role role,
                                             LocalDateTime startDate, LocalDateTime endDate,
                                             Pageable pageable) {

        log.info("Listando usuários com filtros name={}, email={}, role={}, startDate={}, endDate={}",
                name, email, role, startDate, endDate);

        Specification<User> spec = Specification.allOf(
                UserSpecification.hasName(name),
                UserSpecification.hasEmail(email),
                UserSpecification.hasRole(role),
                UserSpecification.createdBetween(startDate, endDate)
        );

        return userRepository.findAll(spec, pageable)
                .map(UserMapper::toDto);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto) {
        log.info("Atualizando usuário {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        user.setName(dto.getName());
        user.setRole(dto.getRole());

        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        log.warn("Deletando usuário {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com esse email: " + email));
    }
}
