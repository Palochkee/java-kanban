package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    @DisplayName("Сравнение Task")
    public void testTwoTasksWithSameId() {
        Task task = new Task("test", "desc", Status.NEW);
        Task task2 = new Task("test", "desc", Status.NEW);
        Assertions.assertEquals(task, task2);
    }

    @Test
    @DisplayName("Сравннение Epic")
    public void testTwoEpicsWithSameId() {
        Epic epic = new Epic("Test", "desc");
        epic.addSubTasksIds(epic.getId());
        Epic epic2 = new Epic("Test", "desc");
        epic2.addSubTasksIds(epic2.getId());
        Assertions.assertEquals(epic, epic2);
    }

    @Test
    @DisplayName("Сравннение SubTask")
    public void testTwoSubTasksWithSameId() {
        SubTask subTask = new SubTask("test", "desc", Status.NEW, 2, 1);
        SubTask subTask2 = new SubTask("test", "desc", Status.NEW, 2, 1);
        Assertions.assertEquals(subTask, subTask2);
    }
}