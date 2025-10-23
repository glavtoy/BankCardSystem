package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на перевод средств между картами")
public class TransferRequest {

    @NotNull
    @Schema(description = "ID карты отправителя", example = "1")
    private Long fromCardId;

    @NotNull
    @Schema(description = "ID карты получателя", example = "2")
    private Long toCardId;

    @NotNull
    @DecimalMin(value = "0.01")
    @Schema(description = "Сумма перевода", example = "500.00")
    private BigDecimal amount;
}