package service;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("Тест работы счетчика counter для Task")
    void testCreateTaskShouldAddTaskToTasksList() {
        int count = taskManager.getTasksList().size();
        taskManager.createTask(new Task("test", "desc", Status.NEW));
        int count2 = taskManager.getTasksList().size();
        Assertions.assertEquals(1, (count + count2));
    }


    @Test
    @DisplayName("Тест работы счетчика counter для Epic")
    void testCreateEpicShouldAddEpicToEpicsList() {
        int count = taskManager.getEpicList().size();
        taskManager.createEpic(new Epic("test", "desc", 1));
        int count2 = taskManager.getEpicList().size();
        Assertions.assertEquals(1, (count + count2));
    }

    @Test
    @DisplayName("Тест работы счетчика counter для SubTask")
    void testCreateSubTaskShouldAddSubTaskToSubTasksList() {
        int count = taskManager.getSubtaskList().size();
        Epic epic = new Epic("test", "desc", 1);
        taskManager.createEpic(epic);
        taskManager.createSubTask(new SubTask("test", "desc", Status.NEW, 1));
        int count2 = taskManager.getSubtaskList().size();
        Assertions.assertEquals(1, (count + count2));
    }

    @Test
    @DisplayName("Тест работы счетчика counter по ID для Task")
    void testCreateTaskShouldCreatedCounterId() {
        Task task = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task);
        Assertions.assertEquals(1, task.getId());
    }

    @Test
    @DisplayName("Сравнение ID для Task")
    void testGetTaskByIdShouldReturnTaskById() {
        Task task = new Task("test", "desc", Status.NEW, 1);
        taskManager.createTask(task);
        Task task1 = new Task("test", "desc", Status.NEW, 2);
        taskManager.createTask(task1);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getId()));
    }

    @Test
    @DisplayName("Сравнение ID для Epic")
    void testGetEpicByIdShouldReturnEpicById() {
        Epic epic = new Epic("test", "desc", 1);
        taskManager.createEpic(epic);
        Epic epic1 = new Epic("test", "desc", 2);
        taskManager.createTask(epic1);
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
        Assertions.assertEquals(epic1, taskManager.getTaskById(epic1.getId()));
    }

    @Test
    @DisplayName("Сравнение ID для SubTask")
    void testGetSubTaskByIdShouldReturnSubTaskById() {
        Epic epic = new Epic("test", "desc");
        Epic subTask = new Epic("test", "desc", Status.NEW, 1);
        Epic subTask1 = new Epic("test", "desc", Status.NEW, 1);
        taskManager.createEpic(epic);
        taskManager.createEpic(subTask);
        taskManager.createEpic(subTask1);
        Assertions.assertEquals(subTask, taskManager.getEpicById(subTask.getId()));
        Assertions.assertEquals(subTask1, taskManager.getEpicById(subTask1.getId()));
    }

    @Test
    @DisplayName("Удаление всех Task")
    void testRemoveTasksShouldDeleteAllTasksFromTasksList() {
        Task task = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task);
        taskManager.removeAllTasks();
        Assertions.assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    @DisplayName("Task равен с Task в TaskManager по ID")
    void testCreateTaskTaskWithIdEqualsWithTaskInTaskManager() {
        Task task1 = new Task("test", "desc", Status.NEW);
        Task task2 = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Assertions.assertEquals(1, task1.getId());
        Assertions.assertEquals(2, task2.getId());
    }

    @Test
    @DisplayName("Task должен быть равен с Task в TaskManager при обновлении")
    void testUpdateTaskTaskInTaskManagerShouldBeEqualsWithUpdatedTask() {
        Task task1 = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task1);
        int id = task1.getId();
        Task task2 = new Task("test", "desc1", Status.IN_PROGRESS, id);
        taskManager.updateTask(task2);
        Task taskInManager = taskManager.getTaskById(id);
        Assertions.assertEquals(task2.getName(), taskInManager.getName());
        Assertions.assertEquals(task2.getDescription(), taskInManager.getDescription());
    }

    @Test
    @DisplayName("Удаление Task по ID")
    void testCreateTaskNewTaskShouldBeEqualsWithTaskInManager() {
        Task task = new Task("test", "desc", Status.NEW);
        taskManager.createTask(task);
        Task taskInManager = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task.getName(), taskInManager.getName());
        Assertions.assertEquals(task.getDescription(), taskInManager.getDescription());
    }

    @Test
    @DisplayName("Epic должен быть равен с Epic в TaskManager при создании")
    void testCreateEpicNewEpicShouldBeEqualsWithEpicInManager() {
        Epic epic = new Epic("test", "desc");
        taskManager.createEpic(epic);
        Task EpicInManager = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(epic.getName(), EpicInManager.getName());
        Assertions.assertEquals(epic.getDescription(), EpicInManager.getDescription());
    }

    @Test
    @DisplayName("SubTask должен быть равен с SubTask в TaskManager при создании")
    void testCreateSubTaskNewSubtaskShouldBeEqualsWithSubTaskInManager() {
        Epic epic = new Epic("test", "desc");
        SubTask subtask = new SubTask("test", "desc", Status.NEW, 1);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subtask);
        SubTask SubtaskInManager = taskManager.getSubTaskById(subtask.getId());
        Assertions.assertEquals(subtask.getName(), SubtaskInManager.getName());
        Assertions.assertEquals(subtask.getDescription(), SubtaskInManager.getDescription());
        Assertions.assertEquals(subtask.getEpicId(), SubtaskInManager.getEpicId());
    }

    @Test
    @DisplayName("Неизменность Task (по всем полям) при добавлении задачи в менеджер")
    public void testCreateTaskCheckEveryField() {
        Task task = taskManager.createTask(new Task("test", "desc", Status.NEW, 1));
        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("test", task.getName());
        Assertions.assertEquals("desc", task.getDescription());
        Assertions.assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    @DisplayName("Неизменность Epic (по всем полям) при добавлении задачи в менеджер")
    public void testCreateEpicCheckEveryField() {
        Epic epic = taskManager.createEpic(new Epic("test", "desc", Status.NEW, 1));
        Assertions.assertEquals(1, epic.getId());
        Assertions.assertEquals("test", epic.getName());
        Assertions.assertEquals("desc", epic.getDescription());
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Неизменность SubTask (по всем полям) при добавлении задачи в менеджер")
    public void testCreateSubTaskCheckEveryField() {
        SubTask subTask = taskManager.createSubTask(new SubTask("test", "desc", Status.NEW, 1, 2));
        Assertions.assertEquals(1, subTask.getId());
        Assertions.assertEquals(2, subTask.getEpicId());
        Assertions.assertEquals("test", subTask.getName());
        Assertions.assertEquals("desc", subTask.getDescription());
        Assertions.assertEquals(Status.NEW, subTask.getStatus());
    }


    @Test
    @DisplayName("Проверка на ID")
    void testShouldCreateIdCheckById() {
        Task task = new Task("task", "descTask");
        Epic epic = new Epic("epic", "descEpic");
        Epic epic1 = new Epic("epic", "descEpic");
        SubTask subTask = new SubTask("epic", "descEpic", Status.NEW, 1);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        int IdEpic = epic.getId();
        int id = task.getId();
        int IdEpic1 = epic1.getId();
        taskManager.getEpicById(IdEpic1);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertNotEquals(subTask, epic);
        Assertions.assertNotEquals(IdEpic, id);
        Assertions.assertNotEquals(task, epic.getId(id));
        Assertions.assertNotEquals(epic.getId(IdEpic1), epic1.getId(IdEpic));
    }

    @Test
    @DisplayName("Task нельзя добавить в самого себя в виде подзадачи")
    void testShouldCreateIdAndSaveTaskById() {
        Task task = new Task("epic", "desc");
        final int taskId = taskManager.createTask("epic", "desc");
        final Task savedTask = taskManager.getTaskById(taskId);
        Assertions.assertNotNull(savedTask);
        Assertions.assertNotEquals(task, savedTask);
        final List<Task> tasks = taskManager.getTasksList();
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertNotEquals(task, tasks.getFirst());
    }

    @Test
    @DisplayName("Epic нельзя добавить в самого себя в виде подзадачи")
    void testShouldCreateIdAndSaveEpicById() {
        Epic epic = new Epic("epic", "desc");
        final int epicId = taskManager.createEpic("epic", "desc");
        final Epic savedEic = taskManager.getEpicById(epicId);
        Assertions.assertNotNull(savedEic);
        Assertions.assertNotEquals(epic, savedEic);
        final List<Epic> epics = taskManager.getEpicList();
        Assertions.assertNotNull(epics);
        Assertions.assertEquals(1, epics.size());
        Assertions.assertNotEquals(epic, epics.getFirst());
    }

    @Test
    @DisplayName("SubTask нельзя сделать своим же эпиком")
    void testShouldCreateIdAndSaveSubTaskById() {
        SubTask subTask = new SubTask("subTask", "desc", Status.NEW, 1);
        Epic epic = new Epic("epic", "desc");
        final int subTaskId = taskManager.createSubTask("task", "desc", epic);
        final int epicId = taskManager.createEpic("task", "desc");
        final int savedSubTask = taskManager.createEpic("subTask", "desc");
        Assertions.assertNotEquals(subTaskId, savedSubTask);
        Assertions.assertNotEquals(epicId, savedSubTask);
        final List<SubTask> subTasks = taskManager.getSubtaskList();
        Assertions.assertNotNull(subTasks);
        Assertions.assertEquals(1, subTasks.size());
        Assertions.assertEquals(subTask, subTasks.getFirst());
    }
}