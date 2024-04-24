package service;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    List<Task> history;
    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        history = historyManager.getHistory();
    }

    @Test
    @DisplayName("Проверка добаления Task в HistoryManager")
    void testAddShouldAddNotNullTaskToHistory() {
        Task task = new Task("test", "desc", Status.NEW);
        Task task1 = new Task("test", "desc", Status.NEW);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task);
        Assertions.assertEquals(historyManager.getHistory().size(), 3);
        Assertions.assertEquals(history.get(0), history.get(2));
        Assertions.assertNotNull(historyManager.getHistory().size());
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
    @Test
    @DisplayName("Задачи в HistoryManager, сохраняют предыдущую версию задачи и её данных")
    void testTaskAddedRetainThePreviousVersionInMemoryHistoryManager() {
        Task task = new Task("test", "desc", Status.NEW, 1);
        Epic epic = new Epic("test", "desc", Status.NEW, 2);
        SubTask subTask = new SubTask("test", "desc", Status.NEW, 2, 2);
        historyManager.add(task);
        historyManager.add(epic);
        assertEquals(1, task.getId(), 1);
        assertEquals("test", task.getName());
        assertEquals("desc", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(2, epic.getId());
        assertEquals("test", epic.getName());
        assertEquals("desc", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(2, subTask.getEpicId());
        assertEquals(2, subTask.getId());
        assertEquals("test", subTask.getName());
        assertEquals("desc", subTask.getDescription());
        assertEquals(Status.NEW, subTask.getStatus());
    }
}