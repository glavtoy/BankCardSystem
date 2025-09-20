package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Номер карты", example = "1234-5678-9876-5432")
    private String number;

    @Schema(description = "Имя владельца карты", example = "john_doe")
    private String owner;

    @Schema(description = "Срок действия карты", example = "2026-12-31")
    private LocalDate expiryDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private String status;

    @Schema(description = "Баланс карты", example = "1000.50")
    private BigDecimal balance;
}