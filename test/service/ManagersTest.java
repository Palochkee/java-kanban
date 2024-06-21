package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    @DisplayName("Возвращает значение для InMemoryTaskManager")
    public void getDefaultInMemoryTaskManager() {
        TaskManager test = Managers.getDefault();
        assertNotNull(test);
    }

    @Test
    @DisplayName("Возвращает историю для InMemoryHistoryManager")
    public void getDefaultHistoryInMemoryHistoryManager() {
        HistoryManager test = Managers.getDefaultHistory();
        assertNotNull(test);
    }

    @Test
    @DisplayName("Возвращает файл для TaskManager")
    public void GetFileBackedTaskManager() {
        TaskManager test = Managers.getFileBackedTaskManager(new File("resources/file.csv"));
        assertNotNull(test);
    }
}