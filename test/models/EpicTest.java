package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


public class EpicTest {
    @Test
    @DisplayName("Сравнение двух Epic")
    public void testTwoEpicsWithSameId() {
        Epic epic = new Epic("test", "desc");
        epic.addSubTasksIds(epic.getId());
        Epic epic2 = new Epic("test", "desc");
        epic2.addSubTasksIds(epic2.getId());
        Assertions.assertEquals(epic, epic2);
    }

    @Test
    @DisplayName("Добавление SubTask в Epic")
    public void testAddSubTaskToEpicWithSameId() {
        Epic epic = new Epic("Test", "desc");
        epic.addSubTasksIds(epic.getId());
        Assertions.assertEquals(1, epic.getSubTasksIds().size());
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
        Epic epic1 = new Epic("test", "desc");
        Epic epic2 = new Epic("test", "desc");
        epic1.setId(epic1.getId());
        epic2.setId(epic2.getId());
        assertEquals(epic1, epic2);
    }


}

