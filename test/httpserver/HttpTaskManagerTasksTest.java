package httpserver;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import enums.Status;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = HttpTaskServer.httpServer(taskManager);
    Gson gson = HttpTaskServer.getGsonBuilder();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 2",
                Status.NEW, LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Task1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), 5);
        task = taskManager.createTask(task);
        Task newTask = new Task("NewName", "NewDesc",
                Status.NEW, task.getId(), LocalDateTime.now(), 60);
        Task taskTmp = taskManager.getTaskById(task.getId());
        assertEquals("Task1", taskTmp.getName(), "Некорректное имя задачи");
        assertEquals("Testing task 1", taskTmp.getDescription(), "Некорректное описание");
        assertEquals(5, taskTmp.getDuration(), "Некорректная продолжительность");
        // конвертируем её в JSON
        String taskJson = gson.toJson(newTask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        taskTmp = taskManager.getTaskById(task.getId());
        assertEquals("NewName", taskTmp.getName(), "Некорректное имя задачи");
        assertEquals("NewDesc", taskTmp.getDescription(), "Некорректное описание");
        assertEquals(60, taskTmp.getDuration(), "Некорректная продолжительность");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), 5);
        Task task2 = new Task("Task2", "Testing task 2",
                Status.IN_PROGRESS, LocalDateTime.now().plusDays(1), 60);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Task> tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Task2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), 5);
        taskManager.createTask(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Task returnTask = gson.fromJson(jsonElement, Task.class);
        assertNotNull(returnTask, "Задача не возвращается");
        assertEquals(task.getName(), returnTask.getName(), "Некорректное имя задачи");
        assertEquals(task.getStatus(), returnTask.getStatus(), "Некорректный статус");
        assertEquals(1, returnTask.getId(), "Некорректное время");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), 5);
        taskManager.createTask(task);
        assertEquals(1, taskManager.getTasksList().size());
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void testEpicAndSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing task 2");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        JsonElement jsonElement;
        SubTask subTask;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            jsonElement = JsonParser.parseString(response.body());
            List<Epic> tasksFromManager = gson.fromJson(jsonElement, new Epic.EpicListTypeToken().getType());
            epic = tasksFromManager.getFirst();

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Epic", epic.getName(), "Некорректное имя задачи");

            subTask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                    LocalDateTime.now(), 5, epic.getId());
            String subtaskJson = gson.toJson(subTask);
            url = URI.create("http://localhost:8080/subtasks");
            request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        jsonElement = JsonParser.parseString(response.body());
        List<SubTask> subTasksFromManager = gson.fromJson(jsonElement, new SubTask.SubtaskListTypeToken().getType());
        subTask = subTasksFromManager.getFirst();

        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Sub", subTask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpicAndSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        SubTask subtask;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            List<Epic> epicsFromManager = taskManager.getEpicList();

            assertNotNull(epicsFromManager, "Задачи не возвращаются");
            assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
            assertEquals("Epic", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");

            subtask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                    LocalDateTime.now(), 5, epicsFromManager.getFirst().getId());
            String subtaskJson = gson.toJson(subtask);
            url = URI.create("http://localhost:8080/subtasks");
            request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subtasksFromManager = taskManager.getSubtaskList();
        subtask = subtasksFromManager.getFirst();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Sub", subtask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpicAndSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        epic = taskManager.createEpic(epic);
        Epic newEpic = new Epic("NewEpic", "NewDesc", epic.getId());

        Epic epicTmp = taskManager.getEpicById(epic.getId());
        assertEquals("Epic", epicTmp.getName(), "Некорректное имя задачи");
        assertEquals("Testing epic", epicTmp.getDescription(), "Некорректное описание");
        // конвертируем её в JSON
        String taskJson = gson.toJson(newEpic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        epicTmp = taskManager.getEpicById(epic.getId());
        assertEquals("NewEpic", epicTmp.getName(), "Некорректное имя задачи");
        assertEquals("NewDesc", epicTmp.getDescription(), "Некорректное описание");

        SubTask subtask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                LocalDateTime.now(), 5, epic.getId());
        subtask = taskManager.createSubTask(subtask);
        SubTask newSubtask = new SubTask("newSub", "newSubdesc", Status.DONE,
                subtask.getId(), LocalDateTime.now(), 60, epic.getId());

        SubTask subtaskTmp = taskManager.getSubTaskById(subtask.getId());
        assertEquals("Sub", subtaskTmp.getName(), "Некорректное имя задачи");
        assertEquals("subdesc", subtaskTmp.getDescription(), "Некорректное описание");

        String subtaskJson = gson.toJson(newSubtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        subtaskTmp = taskManager.getSubTaskById(subtask.getId());

        assertEquals("Sub", subtaskTmp.getName(), "Некорректное имя задачи");
        assertEquals("subdesc", subtaskTmp.getDescription(), "Некорректное описание");
        assertEquals(Status.IN_PROGRESS, subtaskTmp.getStatus(), "Некорректный статус");
    }

    @Test
    public void testGetEpicsAndSubTasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        Epic epic2 = new Epic("Epic2", "Testing epic2");
        epic = taskManager.createEpic(epic);
        epic2 = taskManager.createEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        JsonElement jsonElement;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            jsonElement = JsonParser.parseString(response.body());
            // проверяем, что создалась одна задача с корректным именем
            List<Epic> tasksFromManager = gson.fromJson(jsonElement, new Epic.EpicListTypeToken().getType());

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
            assertEquals("Epic2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");

            SubTask subTask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                    LocalDateTime.now(), 5, epic.getId());
            SubTask subTask2 = new SubTask("Sub2", "subdesc2", Status.DONE,
                    LocalDateTime.now().plusDays(1), 60, epic2.getId());
            taskManager.createSubTask(subTask);
            taskManager.createSubTask(subTask2);

            url = URI.create("http://localhost:8080/subtasks");
            request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body());
        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subtasksFromManager = gson.fromJson(jsonElement, new SubTask.SubtaskListTypeToken().getType());

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Sub", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Sub2", subtasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicAndSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        taskManager.createEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        JsonElement jsonElement;
        SubTask subTask;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/1");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            jsonElement = JsonParser.parseString(response.body());
            // проверяем, что создалась одна задача с корректным именем
            Epic returnTask = gson.fromJson(jsonElement, Epic.class);

            assertNotNull(returnTask, "Задача не возвращается");
            assertEquals(epic.getName(), returnTask.getName(), "Некорректное имя задачи");
            assertEquals(epic.getStatus(), returnTask.getStatus(), "Некорректный статус");
            assertEquals(1, returnTask.getId(), "Некорректный id");

            subTask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                    LocalDateTime.now(), 5, returnTask.getId());
            subTask = taskManager.createSubTask(subTask);
            url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
            request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body());
        // проверяем, что создалась одна задача с корректным именем
        SubTask returnSubTask = gson.fromJson(jsonElement, SubTask.class);

        assertNotNull(returnSubTask, "Задача не возвращается");
        assertEquals(subTask.getName(), returnSubTask.getName(), "Некорректное имя задачи");
        assertEquals(subTask.getStatus(), returnSubTask.getStatus(), "Некорректный статус");
        assertEquals(subTask.getId(), returnSubTask.getId(), "Некорректный id");
    }

    @Test
    public void testDeleteEpicAndSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        epic = taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                LocalDateTime.now(), 5, epic.getId());
        subTask = taskManager.createSubTask(subTask);

        assertEquals(1, taskManager.getEpicList().size());
        assertEquals(1, taskManager.getSubtaskList().size());

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());
            assertEquals(0, taskManager.getSubtaskList().size());

            url = URI.create("http://localhost:8080/epics/" + epic.getId());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpicList().size());
    }

    @Test
    public void testGetEpicSubTasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic", "Testing epic");
        epic = taskManager.createEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        JsonElement jsonElement;
        List<SubTask> tasksFromManager;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            jsonElement = JsonParser.parseString(response.body());
            // проверяем, что создалась одна задача с корректным именем
            tasksFromManager = gson.fromJson(jsonElement, new SubTask.SubtaskListTypeToken().getType());
            assertEquals(0, tasksFromManager.size());

            SubTask subtask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                    LocalDateTime.now(), 5, epic.getId());
            taskManager.createSubTask(subtask);

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body());
        // проверяем, что создалась одна задача с корректным именем
        tasksFromManager = gson.fromJson(jsonElement, new SubTask.SubtaskListTypeToken().getType());
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    public void testHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Task1", "Testing task 2",
                Status.NEW, LocalDateTime.now(), 5);
        task = taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Testing epic");
        epic = taskManager.createEpic(epic);

        SubTask subtask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                LocalDateTime.now().plusDays(1), 60, epic.getId());
        subtask = taskManager.createSubTask(subtask);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        JsonElement jsonElement;
        List<Task> tasksFromManager;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            jsonElement = JsonParser.parseString(response.body());
            // проверяем, что создалась одна задача с корректным именем
            tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
            assertEquals(0, tasksFromManager.size());

            taskManager.getTaskById(task.getId());
            taskManager.getSubTaskById(subtask.getId());

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            jsonElement = JsonParser.parseString(response.body());
            // проверяем, что создалась одна задача с корректным именем
            tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
            assertEquals(2, tasksFromManager.size());

            taskManager.getEpicById(epic.getId());
            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        jsonElement = JsonParser.parseString(response.body());
        // проверяем, что создалась одна задача с корректным именем
        tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
        assertEquals(3, tasksFromManager.size());
    }

    @Test
    public void testPrioritized() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Task1", "Testing task 1",
                Status.NEW, LocalDateTime.now(), 5);
        task = taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Testing epic");
        epic = taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Sub", "subdesc", Status.IN_PROGRESS,
                LocalDateTime.now().minusDays(1), 60, epic.getId());
        subTask = taskManager.createSubTask(subTask);

        Task task2 = new Task("Task2", "Testing task 2",
                Status.DONE, LocalDateTime.now().plusDays(1), 5);
        task2 = taskManager.createTask(task2);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = gson.fromJson(jsonElement, new Task.TaskListTypeToken().getType());
        assertEquals(3, tasksFromManager.size());
        assertEquals(subTask.getId(), tasksFromManager.getFirst().getId());
        assertEquals(task.getId(), tasksFromManager.get(1).getId());
        assertEquals(task2.getId(), tasksFromManager.getLast().getId());
    }
}