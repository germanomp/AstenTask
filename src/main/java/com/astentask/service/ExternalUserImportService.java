package com.astentask.service;

import com.astentask.dtos.ExternalUserDTO;
import com.astentask.model.Role;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ExternalUserImportService {

    private final UserRepository userRepository;

    public void importUsersFromJsonPlaceholder() {
        String url = "https://jsonplaceholder.typicode.com/users";
        RestTemplate restTemplate = new RestTemplate();
        ExternalUserDTO[] externalUsers = restTemplate.getForObject(url, ExternalUserDTO[].class);

        if (externalUsers != null) {
            Arrays.stream(externalUsers).forEach(eu -> {
                if (!userRepository.existsByEmail(eu.getEmail())) {
                    User user = User.builder()
                            .name(eu.getName())
                            .email(eu.getEmail())
                            .password("senha123")
                            .role(Role.DEVELOPER)
                            .build();
                    userRepository.save(user);
                }
            });
        }
    }

}
