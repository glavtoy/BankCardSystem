package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для аутентификации пользователя")
public class AuthRequest {

    @Schema(description = "Имя пользователя", example = "john_doe", required = true)
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;
}