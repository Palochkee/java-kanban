package service;


import enums.Status;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static service.Managers.getFileBackedTaskManager;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final Path file = Paths.get("resources/file.csv");

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(file.toFile());
    }

    @Test
    @DisplayName("Тест создания файла")
    public void testCreateEmptyFileTest() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertFalse(file.toFile().isFile());
        taskManager = (FileBackedTaskManager) getFileBackedTaskManager(file.toFile());
        Assertions.assertNotNull(String.valueOf(file.toFile().isFile()));
    }


    @Test
    @DisplayName("Тест работы создание Task")
    public void testCreateTasksToFileTest() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = (FileBackedTaskManager) getFileBackedTaskManager(file.toFile());
        Task task = new Task("Task", "TaskDesc", Status.NEW);
        task = taskManager.createTask(task);
        Epic epic = new Epic("Epic", "EpicDesc");
        epic = taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Subtask", "SubDesc", Status.IN_PROGRESS, epic.getId());
        subtask = taskManager.createSubTask(subtask);
        taskManager.getTaskById(task.getId());
        taskManager.getSubTaskById(subtask.getId());
        Reader fileReader = null;
        try {
            fileReader = new FileReader(file.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader br = new BufferedReader(fileReader);
        try {
            br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                if (!br.ready()) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Assertions.assertTrue((line.contains(String.valueOf(task.getId())) && line.contains(task.getName()) && line.contains(task.getDescription())) || (line.contains(String.valueOf(epic.getId())) && line.contains(epic.getName()) && line.contains(epic.getDescription())) || (line.contains(String.valueOf(subtask.getId())) && line.contains(subtask.getName()) && line.contains(subtask.getDescription())) || (line.contains(task.getId() + "," + subtask.getId())));
        }
        try {
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Тест загрузки в файл")
    public void testLoadFileTest() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.createFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new FileWriter(file.toFile())) {
            writer.write("id,type,name,status,description,startTime,endTime,duration,epic\n");
            writer.write("1,TASK,New Task,DONE,New Task Desc,null,null,0\n");
            writer.write("2,EPIC,New Epic,NEW, New Epic Desc,null,null,0\n");
            writer.write("3,SUBTASK,New Subtask,NEW,New sub desc,null,null,0,2\n");
            writer.write("3,1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = FileBackedTaskManager.loadFromFile(file.toFile());
        Task task = taskManager.getTaskById(1);
        Assertions.assertEquals(task.getId(), 1);
        Assertions.assertEquals(task.getName(), "New Task");
        Assertions.assertEquals(task.getStatus(), Status.DONE);
        Epic epic = taskManager.getEpicById(2);
        Assertions.assertEquals(epic.getId(), 2);
        Assertions.assertEquals(epic.getName(), "New Epic");
        Assertions.assertEquals(epic.getStatus(), Status.NEW);
        SubTask subtask = taskManager.getSubTaskById(3);
        Assertions.assertEquals(subtask.getId(), 3);
        Assertions.assertEquals(subtask.getName(), "New Subtask");
        Assertions.assertEquals(subtask.getStatus(), Status.NEW);
        Assertions.assertEquals(subtask.getEpicId(), 2);
    }
}

