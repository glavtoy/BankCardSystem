package ru.glavtoy.bankcardsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на перевод средств между картами")
public class TransferRequest {

    @Schema(description = "ID карты отправителя", example = "1", required = true)
    private Long fromCardId;

    @Schema(description = "ID карты получателя", example = "2", required = true)
    private Long toCardId;

    @Schema(description = "Сумма перевода", example = "500.00", required = true)
    private BigDecimal amount;
}