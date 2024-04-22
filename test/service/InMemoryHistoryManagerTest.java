package service;

import models.Status;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Проверка добаления Task в HistoryManager")
    void testAddShouldAddNotNullTaskToHistory() {
        Task task = new Task("test", "desc", Status.NEW);
        historyManager.add(task);
        Assertions.assertNotNull(historyManager.getHistory().getFirst());
    }

    @Test
    @DisplayName("Проверка добвления не больше 10 задач в HistoryManager")
    void testAddHistoryShouldNotBeLongerThan10() {
        int i = 0;
        while (i < 11) {
            historyManager.add(new Task("test", "desc", Status.NEW));
            i++;
        }
        Assertions.assertEquals(10, historyManager.getHistory().size());
    }
}