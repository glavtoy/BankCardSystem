package ru.glavtoy.bankcardsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность банковской карты")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Номер карты", example = "1234567812345678")
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cards_owner"))
    @Schema(description = "Владелец карты")
    private User owner;

    @Column(name = "expiry_date", nullable = false)
    @Schema(description = "Срок действия карты", example = "2026-12-31", type = "string", format = "date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Статус карты", example = "ACTIVE")
    private Status status;

    @Column(nullable = false)
    @Schema(description = "Баланс карты", example = "1000.50")
    private BigDecimal balance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,
        BLOCKED,
        EXPIRED
    }
}