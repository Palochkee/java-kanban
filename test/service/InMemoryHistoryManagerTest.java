package service;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    private Task createTask(int id) {
        Task task = new Task("test", "desc", Status.NEW);
        task.setId(id);
        return task;
    }


    @Test
    @DisplayName("тест работы метода создание Task")
    void testCreateTaskWhenHistoryManagerThenReturnSize() {
        historyManager.add(createTask(1));
        int actualSize = historyManager.getHistory().size();
        assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("тест удаления первого Task")
    void testRemoveFirstTask() {
        Task task = createTask(1);
        historyManager.add(task);
        Task task1 = createTask(2);
        historyManager.add(task1);
        Task task2 = createTask(3);
        historyManager.add(task2);
        historyManager.remove(1);
        assertEquals(historyManager.getHistory(), List.of(task1, task2));
    }


    @Test
    @DisplayName("тест удаления последнего Task")
    void testRemoveLastTask() {
        Task task = createTask(1);
        historyManager.add(task);
        Task task1 = createTask(2);
        historyManager.add(task1);
        Task task2 = createTask(3);
        historyManager.add(task2);
        historyManager.remove(3);
        assertEquals(historyManager.getHistory(), List.of(task, task1));
    }


    @Test
    @DisplayName("тест удаления среднего Task")
    void testRemoveMiddleTask() {
        Task task = createTask(1);
        historyManager.add(task);
        Task task1 = createTask(2);
        historyManager.add(task1);
        Task task2 = createTask(3);
        historyManager.add(task2);
        historyManager.remove(2);
        assertEquals(historyManager.getHistory(), List.of(task, task2));
    }


    @Test
    @DisplayName("тест невозможности добавить дубль Task")
    void testNotAddDoubleTaskInHistoryManager() {
        Task task = createTask(1);
        historyManager.add(task);
        historyManager.add(task);
        int actualSize = historyManager.getHistory().size();
        assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("тест записи и удаления всего списка HistoryManager")
    void testRemoveWhenRecordExistThenHistoryIsEmpty() {
        Task task = createTask(1);
        historyManager.add(task);
        historyManager.add(createTask(2));
        historyManager.add(createTask(3));
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        List<Task> historyIsEmpty = historyManager.getHistory();
        List<Task> nullHistory = List.of();
        assertEquals(nullHistory, historyIsEmpty);
    }

    @Test
    @DisplayName("тест добавления и записи Task в начало HistoryManager")
    void testAddWhenRecordIsFirst() {
        Task task1 = createTask(1);
        historyManager.add(task1);
        historyManager.add(createTask(2));
        historyManager.add(createTask(3));
        int actualFirstId = historyManager.getHistory().getFirst().getId();
        assertEquals(1, actualFirstId);
    }

    @Test
    @DisplayName("тест добавления и записи Task в конец HistoryManager")
    void testAddWhenRecordIsLast() {
        historyManager.add(createTask(1));
        historyManager.add(createTask(2));
        Task task3 = createTask(3);
        historyManager.add(task3);

        int actualLastId = historyManager.getHistory().getLast().getId();

        assertEquals(3, actualLastId);
    }

    @Test
    @DisplayName("тест добавления и записи Task в середину HistoryManager")
    void testAddWhenRecordInTheMiddle() {
        historyManager.add(createTask(1));
        Task task2 = createTask(2);
        historyManager.add(task2);
        historyManager.add(createTask(3));
        int actualMiddleId = historyManager.getHistory().getFirst().getId(2);
        assertEquals(2, actualMiddleId);
    }


    @Test
    @DisplayName("тест добавления и записи трех Task в HistoryManager")
    void testGetHistoryWhen3RecordsExistThenReturnHistoryList() {
        Task task1 = createTask(1);
        historyManager.add(task1);
        Task task2 = createTask(2);
        historyManager.add(task2);
        Task task3 = createTask(3);
        historyManager.add(task3);
        List<Task> expectedHistory = List.of(task1, task2, task3);
        assertEquals(expectedHistory, historyManager.getHistory());
    }

    @Test
    @DisplayName("тест задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных")
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
