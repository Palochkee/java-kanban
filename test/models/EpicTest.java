package models;


import enums.Status;
import interfaces.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class EpicTest {
    static final TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("Сравнение двух Epic")
    public void testTwoEpicsWithSameId() {
        Epic epic = new Epic("test", "desc");
        epic.addSubTasksIds(epic.getId());
        Epic epic1 = new Epic("test", "desc");
        epic1.addSubTasksIds(epic1.getId());
        assertEquals(epic, epic1);
    }

    @Test
    @DisplayName("Добавление SubTask в Epic")
    public void testAddSubTaskToEpicWithSameId() {
        Epic epic = new Epic("Test", "desc");
        epic.addSubTasksIds(epic.getId());
        assertEquals(1, epic.getSubTasksIds().size());
    }


    @Test
    @DisplayName("Создание класса эпика")
    void epicCreation() {
        int id = 1;
        String title = "test";
        String description = "desc";
        Status status = Status.NEW;
        Epic epic = new Epic(title, description);
        epic.setId(id);
        assertEquals(id, epic.getId());
        assertEquals(title, epic.getName());
        assertEquals(description, epic.getDescription());
        assertEquals(status, epic.getStatus());
    }

    @Test
    @DisplayName("Сравнение Epic")
    void epicCreationAndEquality() {
        Epic epic = new Epic("test", "desc");
        Epic epic1 = new Epic("test", "desc");
        epic.setId(epic.getId());
        epic.setId(epic.getId());
        assertEquals(epic, epic1);
    }

    @Test
    @DisplayName("Epic заданным id и сгенерированным id не конфликтуют внутри менеджера")
    void epicWithTheSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        Epic epic1 = new Epic("test", "desc", 0);
        Epic epic2 = new Epic("test", "desc");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getEpicList().size());
        assertNotEquals(epic1, epic2);

    }

}

