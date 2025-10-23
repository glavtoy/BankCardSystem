package ru.glavtoy.bankcardsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glavtoy.bankcardsystem.dto.CardDTO;
import ru.glavtoy.bankcardsystem.dto.TransferRequest;
import ru.glavtoy.bankcardsystem.entity.Card;
import ru.glavtoy.bankcardsystem.service.CardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "Эндпоинты для управления банковскими картами")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Получить все карты", description = "Возвращает список всех карт или по владельцу")
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(@RequestParam(required = false) String owner) {
        return ResponseEntity.ok(owner == null ? cardService.getAllCards() : cardService.getCardsByOwner(owner));
    }

    @Operation(summary = "Получить карту по ID", description = "Возвращает данные карты по её идентификатору")
    @PreAuthorize("hasRole('ADMIN') or @cardService.isOwner(#id, principal)")
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс карты по ID")
    @PreAuthorize("hasRole('ADMIN') or @cardService.isOwner(#id, principal)")
    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long id) {
        CardDTO card = cardService.getCard(id);
        return ResponseEntity.ok(Map.of("id", card.getId(), "balance", card.getBalance()));
    }

    @Operation(summary = "Создать карту", description = "Создает новую карту")
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(cardService.createCard(cardDTO));
    }

    @Operation(summary = "Обновить статус карты", description = "Позволяет изменить статус карты")
    @PutMapping("/{id}/status")
    public ResponseEntity<CardDTO> updateStatus(@PathVariable Long id, @RequestParam Card.Status status) {
        return ResponseEntity.ok(cardService.updateStatus(id, status));
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Перевод между картами", description = "Выполняет перевод между картами")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        cardService.transfer(request);
        return ResponseEntity.ok().build();
    }
}