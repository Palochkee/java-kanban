package models;

import enums.Status;
import interfaces.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubTaskTest {
    static final TaskManager taskManager = Managers.getDefault();

    @Test
    @DisplayName("SubTask с заданным id и сгенерированным id не конфликтуют внутри менеджера")
    void testSubTasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflict() {
        SubTask subTask = new SubTask("test", "desc", Status.NEW, 0, 1);
        SubTask subTask1 = new SubTask("test", "desc", Status.NEW, 1);
        taskManager.createEpic(new Epic("test", "desc"));
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask1);
        assertEquals(2, taskManager.getSubtaskList().size());
        assertNotEquals(subTask, subTask1);
    }

}
