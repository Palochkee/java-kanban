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

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    List<SubTask> getSubTasksEpicsIds(int epicId);

    Task get(int id);

    Epic getEpicById(int id);

    //SubTask getSubTaskById(int subTaskId);

    SubTask getSubTaskById(int subTaskId);

    List<Task> getHistory();
}
