package models;


import enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static models.EpicTest.taskManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {
    @Test
    @DisplayName("Сравнение двух Task, без генерации ID")
    public void testTwoTasksWithSameId() {
        Task task = new Task("test", "desc", Status.NEW);
        Task task1 = new Task("test", "desc", Status.NEW);
        assertEquals(task, task1);
    }

    @Test
    @DisplayName("Сравннение двух Epic по ID")
    public void testTwoEpicsWithSameId() {
        Epic epic = new Epic("Test", "desc");
        epic.addSubTasksIds(epic.getId());
        Epic epic1 = new Epic("Test", "desc");
        epic1.addSubTasksIds(epic1.getId());
        assertEquals(epic, epic1);
    }

    @Test
    @DisplayName("Сравннение двух SubTask")
    public void testTwoSubTasksWithSameId() {
        SubTask subTask = new SubTask("test", "desc", Status.NEW, 2, 1);
        SubTask subTask2 = new SubTask("test", "desc", Status.NEW, 2, 1);
        assertEquals(subTask, subTask2);
    }

    @Test
    @DisplayName("Задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера")
    void testTasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Task task = new Task("test", "desc", Status.NEW, 0);
        Task task1 = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task1);
        assertEquals(taskManager.getTasksList().size(), 2);
        assertNotEquals(task, task1);
    }

}