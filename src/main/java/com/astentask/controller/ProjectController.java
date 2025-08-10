package com.astentask.controller;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.dtos.ProjectStatsDTO;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.service.ProjectService;
import com.astentask.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    @GetMapping
    public Page<ProjectResponseDTO> getProjectsByUser(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        User user = getUserFromAuth(authentication);
        return projectService.listProjectByUser(user, name, startDate, endDate, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public ProjectResponseDTO getProjectById(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.getProjectByIdAndUser(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO createProject(@RequestBody @Valid ProjectRequestDTO dto, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.createProject(dto, user);
    }

    @PutMapping("/{id}")
    public ProjectResponseDTO updateProject(@PathVariable Long id,
                                            @RequestBody @Valid ProjectRequestDTO dto,
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

    @GetMapping("/{id}/stats")
    public ProjectStatsDTO getStats(@PathVariable Long id) {
        return projectService.getProjectStats(id);
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}
