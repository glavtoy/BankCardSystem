package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для регистрации нового пользователя")
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}