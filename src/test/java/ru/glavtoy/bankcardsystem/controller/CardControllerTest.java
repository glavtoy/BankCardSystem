package ru.glavtoy.bankcardsystem.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.glavtoy.bankcardsystem.service.CardService;

import java.math.BigDecimal;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @Test
    void getBalance_ShouldReturnBalance() throws Exception {
        Long cardId = 1L;
        BigDecimal balance = new BigDecimal("1500.75");

        when(cardService.getCard(cardId).getBalance()).thenReturn(balance);

        mockMvc.perform(get("/api/cards/{id}/balance", cardId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1500.75));
    }

    @Test
    void getBalance_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
        Long cardId = 999L;

        when(cardService.getCard(cardId).getBalance())
                .thenThrow(new RuntimeException("Карта не найдена!"));

        mockMvc.perform(get("/api/cards/{id}/balance", cardId))
                .andExpect(status().isNotFound());
    }
}