package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для регистрации нового пользователя")
public class RegisterRequest {

    @Schema(description = "Имя пользователя", example = "john_doe", required = true)
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;

    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\",\"ROLE_ADMIN\"]", required = true)
    private List<String> roles;
}