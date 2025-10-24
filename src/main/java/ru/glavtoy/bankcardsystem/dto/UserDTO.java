package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для пользователя")
public class UserDTO {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Список ролей", example = "[\"USER\",\"ADMIN\"]")
    private List<String> roles;

    @Schema(description = "Дата создания", example = "2025-10-24T10:00:00")
    private LocalDateTime createdAt;
}