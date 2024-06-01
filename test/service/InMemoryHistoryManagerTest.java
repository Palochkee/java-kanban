package service;

import enums.Status;
import interfaces.HistoryManager;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;


    @BeforeEach
    void createHistory() {
        historyManager = new InMemoryHistoryManager();
    }

    private Task createTask(int id) {
        Task task = new Task("test", "desc", Status.NEW);
        task.setId(id);
        return task;
    }

    private HistoryManager createHistory(int id) {
        Task task = new Task("test", "desc", Status.NEW);
        task.setId(id);
        historyManager.add(task);
        return historyManager;
    }

    @Test
    @DisplayName("Тест работы метода создание Task")
    void testCreateTaskWhenHistoryManagerThenReturnSize() {
        historyManager.add(createTask(1));
        int actualSize = historyManager.getHistory().size();
        assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("Тест удаления первого Task")
    void testRemoveFirstTask() {
        createHistory(1);
        createHistory(2);
        createHistory(3);
        historyManager.remove(1);
        int actualSize = historyManager.getHistory().size();
        List<Task> allTasks = List.copyOf(createHistory(2).getHistory());
        assertEquals(2, actualSize);
        assertEquals(historyManager.getHistory(), allTasks);
    }


    @Test
    @DisplayName("Тест удаления последнего Task")
    void testRemoveLastTask() {
        createHistory(1);
        createHistory(2);
        createHistory(3);
        historyManager.remove(3);
        int actualSize = historyManager.getHistory().size();
        List<Task> allTasks = List.copyOf(createHistory(1).getHistory());
        assertEquals(2, actualSize);
        assertEquals(historyManager.getHistory(), allTasks);
    }


    @Test
    @DisplayName("Тест удаления среднего Task")
    void testRemoveMiddleTask() {
        createHistory(1);
        createHistory(2);
        createHistory(3);
        historyManager.remove(2);
        int actualSize = historyManager.getHistory().size();
        List<Task> allTasks = List.copyOf(createHistory(1).getHistory());
        assertEquals(2, actualSize);
        assertEquals(historyManager.getHistory(), allTasks);
    }


    @Test
    @DisplayName("Тест невозможности добавить дубль Task")
    void testNotAddDoubleTaskInHistoryManager() {
        Task task = createTask(1);
        historyManager.add(task);
        historyManager.add(task);
        int actualSize = historyManager.getHistory().size();
        assertEquals(1, actualSize);
    }

    @Test
    @DisplayName("Тест записи и удаления всего списка HistoryManager")
    void testRemoveWhenRecordExistThenHistoryIsEmpty() {
        createHistory(1);
        createHistory(2);
        createHistory(3);
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        List<Task> historyIsEmpty = historyManager.getHistory();
        List<Task> nullHistory = new ArrayList<>();
        assertEquals(nullHistory, historyIsEmpty);
    }

    @Test
    @DisplayName("Тест добавления и записи Task в начало HistoryManager")
    void testAddWhenRecordIsFirst() {
        Task task1 = createTask(1);
        historyManager.add(task1);
        historyManager.add(createTask(2));
        historyManager.add(createTask(3));
        int actualFirstId = historyManager.getHistory().getFirst().getId();
        assertEquals(1, actualFirstId);
    }

    @Test
    @DisplayName("Тест добавления и записи Task в конец HistoryManager")
    void testAddWhenRecordIsLast() {
        historyManager.add(createTask(1));
        historyManager.add(createTask(2));
        Task task3 = createTask(3);
        historyManager.add(task3);
        int actualLastId = historyManager.getHistory().getLast().getId();
        assertEquals(3, actualLastId);
    }

    @Test
    @DisplayName("Тест добавления и записи Task в середину HistoryManager")
    void testAddWhenRecordInTheMiddle() {
        historyManager.add(createTask(1));
        Task task2 = createTask(2);
        historyManager.add(task2);
        historyManager.add(createTask(3));
        int actualMiddleId = historyManager.getHistory().getFirst().getId(2);
        assertEquals(2, actualMiddleId);
    }


    @Test
    @DisplayName("Тест добавления и записи трех Task в HistoryManager")
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
    @DisplayName("Тест задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных")
    void testTaskAddedRetainThePreviousVersionInMemoryHistoryManager() {
        Task task = new Task("test", "desc", Status.NEW, 1);
        Epic epic = new Epic("test1", "desc1", Status.IN_PROGRESS, 2);
        SubTask subTask = new SubTask("test2", "desc3", Status.DONE, 3, 3);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        assertEquals(1, task.getId(), "ID Task равны");
        assertEquals("test", task.getName(), "Задачи Task равны");
        assertEquals("desc", task.getDescription(), "Подзадачи Task равны");
        assertEquals(Status.NEW, task.getStatus(), "Статусы Task равны");
        assertEquals(2, epic.getId(), "ID Epic равны");
        assertEquals("test1", epic.getName(), "Задачи Epic равны");
        assertEquals("desc1", epic.getDescription(), "Подзадачи Epic равны");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статусы Epic равны");
        assertEquals(3, subTask.getEpicId(), "ID Epic save в SubTask");
        assertEquals(3, subTask.getId(), "ID SubTask равны");
        assertEquals("test2", subTask.getName(), "Задачи Epic save SubTask");
        assertEquals("desc3", subTask.getDescription(), "Подзадачи SubTask равны");
        assertEquals(Status.DONE, subTask.getStatus(), "Статусы SubTask равны");
        assertEquals(historyManager.getHistory(), List.of(task, epic, subTask), "Задачи добавляемые в HistoryManager равны задачам в списке");
    }
}