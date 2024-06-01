package service;

import enums.Status;
import exceptions.ManagerSaveException;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static service.Managers.getFileBackedTaskManager;

public class FileBackedTaskManagerTest {
    Path file = Paths.get("resources/file.csv");
    private TaskManager taskManager;

    @Test
    @DisplayName("Тест создания файла")
    public void testCreateEmptyFileTest() {
        try {
            Files.deleteIfExists(file);
            Assertions.assertFalse(file.toFile().isFile());
            taskManager = getFileBackedTaskManager(file.toFile());
            Assertions.assertNotNull(0, String.valueOf(file.toFile().isFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Тест создание Task")
    public void testCreateTasksToFileTest() throws IOException {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = getFileBackedTaskManager(file.toFile());
        Task task = new Task("Task", "TaskDesc", Status.NEW);
        task = taskManager.createTask(task);
        Epic epic = new Epic("Epic", "EpicDesc");
        epic = taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Subtask", "sub desc", Status.IN_PROGRESS, epic.getId());
        subtask = taskManager.createSubTask(subtask);
        taskManager.getTaskById(task.getId());
        taskManager.getSubTaskById(subtask.getId());
        Reader fileReader = new FileReader(file.toFile());
        BufferedReader br = new BufferedReader(fileReader);
        br.readLine();
        while (br.ready()) {
            String line = br.readLine();
            Assertions.assertTrue((line.contains(String.valueOf(task.getId()))
                    && line.contains(task.getName()) && line.contains(task.getDescription()))
                    || (line.contains(String.valueOf(epic.getId()))
                    && line.contains(epic.getName()) && line.contains(epic.getDescription()))
                    || (line.contains(String.valueOf(subtask.getId()))
                    && line.contains(subtask.getName()) && line.contains(subtask.getDescription()))
                    || (line.contains(task.getId() + "," + subtask.getId())));
        }
        br.close();
    }

    @Test
    @DisplayName("Тест загрузки в файл")
    public void testLoadFileTest() {
        try {
            Files.deleteIfExists(file);
            Files.createFile(file);
            try (Writer writer = new FileWriter(file.toFile())) {
                writer.write("id,type,name,status,description,epic\n");
                writer.write("1,TASK,New Task,NEW,New Task Desc\n");
                writer.write("2,EPIC,New Epic,NEW, New Epic Desc\n");
                writer.write("3,SUBTASK,New Subtask,NEW,New sub desc,2\n");
            }
            taskManager = FileBackedTaskManager.loadFromFile(file.toFile());
            Task task = taskManager.getTaskById(1);
            Assertions.assertEquals(task.getId(), 1);
            Assertions.assertEquals(task.getName(), "New Task");
            Assertions.assertEquals(task.getStatus(), Status.NEW);
            Epic epic = taskManager.getEpicById(2);
            Assertions.assertEquals(epic.getId(), 2);
            Assertions.assertEquals(epic.getName(), "New Epic");
            Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
            SubTask subtask = taskManager.getSubTaskById(3);
            Assertions.assertEquals(subtask.getId(), 3);
            Assertions.assertEquals(subtask.getName(), "New Subtask");
            Assertions.assertEquals(subtask.getStatus(), Status.NEW);
            Assertions.assertEquals(subtask.getEpicId(), 2);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

}