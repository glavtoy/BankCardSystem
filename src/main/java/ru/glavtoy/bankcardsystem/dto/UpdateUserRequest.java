package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на обновление пользователя")
public class UpdateUserRequest {

    @Size(min = 6, max = 100)
    @Schema(description = "Новый пароль (опционально)", example = "newpassword123")
    private String password;

    @Schema(description = "Новый список ролей (опционально)", example = "[\"USER\",\"ADMIN\"]")
    private List<String> roles;
}