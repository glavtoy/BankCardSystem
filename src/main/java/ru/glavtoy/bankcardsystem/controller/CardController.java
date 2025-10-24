package ru.glavtoy.bankcardsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.glavtoy.bankcardsystem.dto.CardDTO;
import ru.glavtoy.bankcardsystem.dto.TransferRequest;
import ru.glavtoy.bankcardsystem.entity.Card;
import ru.glavtoy.bankcardsystem.entity.User;
import ru.glavtoy.bankcardsystem.service.CardService;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "Эндпоинты для управления банковскими картами")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Получить карты", description = "Для админа - все или поиск по owner, для пользователя - только свои. С пагинацией.")
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String owner,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) authentication.getPrincipal();
        if (owner == null) {
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(cardService.getAllCards(pageable));
            } else {
                return ResponseEntity.ok(cardService.getMyCards(user.getUsername(), pageable));
            }
        } else {
            if (user.getRoles().contains("ADMIN") || user.getUsername().equalsIgnoreCase(owner)) {
                return ResponseEntity.ok(cardService.getCardsByOwner(owner, pageable));
            } else {
                throw new org.springframework.security.access.AccessDeniedException("Доступ запрещен");
            }
        }
    }

    @Operation(summary = "Получить карту по ID", description = "Возвращает данные карты по её идентификатору")
    @PreAuthorize("hasRole('ADMIN') or @cardService.isOwner(#id, principal.username)")
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс карты по ID")
    @PreAuthorize("hasRole('ADMIN') or @cardService.isOwner(#id, principal.username)")
    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long id) {
        CardDTO card = cardService.getCard(id);
        return ResponseEntity.ok(Map.of("id", card.getId(), "balance", card.getBalance()));
    }

    @Operation(summary = "Создать карту", description = "Создает новую карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(cardService.createCard(cardDTO));
    }

    @Operation(summary = "Обновить статус карты", description = "Позволяет изменить статус карты (админ - любой, пользователь - только BLOCKED для своих)")
    @PreAuthorize("hasRole('ADMIN') or (@cardService.isOwner(#id, principal.username) and #status.toString() == 'BLOCKED')")
    @PutMapping("/{id}/status")
    public ResponseEntity<CardDTO> updateStatus(@PathVariable Long id, @RequestParam Card.Status status) {
        return ResponseEntity.ok(cardService.updateStatus(id, status));
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту по ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Перевод между картами", description = "Выполняет перевод между картами")
    @PreAuthorize("@cardService.isOwner(#request.fromCardId, principal.username) and @cardService.isOwner(#request.toCardId, principal.username)")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        cardService.transfer(request);
        return ResponseEntity.ok().build();
    }
}