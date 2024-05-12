package service;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<Epic> getEpicList();

    List<SubTask> getSubtaskList();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeTaskId(int id);

    void removeEpicId(int id);

    void removeSubTaskId(int id);

    Task createTask(Task task);

    int createTask(String name, String description);

    Epic createEpic(Epic epic);

    int createEpic(String name, String description);

    SubTask createSubTask(SubTask subTask);

    int createSubTask(String name, String description, Epic epic);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    List<SubTask> getSubtasksByEpic(int epicId);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int subTaskId);

    List<Task> getHistory();
}