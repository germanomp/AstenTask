package com.astentask.controller;

import com.astentask.service.ExternalUserImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
public class ExternalUserController {

    private final ExternalUserImportService importService;

    @PostMapping("/import-users")
    public ResponseEntity<String> importUsers() {
        importService.importUsersFromJsonPlaceholder();
        return ResponseEntity.ok("Importação concluída com sucesso!");
    }

}
