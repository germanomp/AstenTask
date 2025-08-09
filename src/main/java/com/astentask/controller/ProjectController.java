package com.astentask.controller;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.service.ProjectService;
import com.astentask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public List<ProjectResponseDTO> getProjectsByUser(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.listProjectByUser(user);
    }

    @GetMapping("/{id}")
    public ProjectResponseDTO getProjectById(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.getProjectByIdAndUser(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO createProject(@RequestBody ProjectRequestDTO dto, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.createProject(dto, user);
    }

    @PutMapping("/{id}")
    public ProjectResponseDTO updateProject(@PathVariable Long id,
                                            @RequestBody ProjectRequestDTO dto,
                                            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.updateProject(id, dto, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        projectService.deleteProject(id, user);
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}
