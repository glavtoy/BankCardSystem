package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для банковской карты")
public class CardDTO {

    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;

    @NotBlank
    @Size(min = 12, max = 19)
    @Schema(description = "Номер карты", example = "1234567812345678")
    private String number;

    @NotBlank
    @Schema(description = "Имя владельца карты", example = "john_doe")
    private String owner;

    @NotNull
    @Schema(description = "Срок действия карты", example = "2026-12-31", type = "string", format = "date")
    private LocalDate expiryDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private String status;

    @NotNull
    @Schema(description = "Баланс карты", example = "1000.50")
    private BigDecimal balance;
}