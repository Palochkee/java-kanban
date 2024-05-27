package service;

import enums.Status;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected static Map<Integer, Task> tasksMap; // интерфейс Map
    protected static Map<Integer, SubTask> subTasksMap; // интерфейс Map
    protected static Map<Integer, Epic> epicsMap; // интерфейс Map
    protected int counter = 0;
    protected static HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        subTasksMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<SubTask> getSubtaskList() {
        return new ArrayList<>(subTasksMap.values());
    }

    @Override
    public void removeAllTasks() {
        List<Task> removeAllTasks = getTasksList();
        tasksMap.clear();
        for (Task task : removeAllTasks) {
            historyManager.remove(task.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        List<Epic> removeAllEpics = getEpicList();
        List<SubTask> removeSubTasks = getSubtaskList();
        epicsMap.clear();
        subTasksMap.clear();

        for (Epic epic : removeAllEpics) {
            historyManager.remove(epic.getId());
        }

        for (SubTask subTask : removeSubTasks) {
            historyManager.remove(subTask.getId());
        }
    }

    @Override
    public void removeAllSubtasks() {
        List<SubTask> removeAllSubTasks = getSubtaskList();
        subTasksMap.clear();

        for (Epic epic : epicsMap.values()) {
            if (epic != null) {
                epic.removeAllSubtasks();
                updateStatus(epic.getId());
            }
        }

        for (SubTask subTask : removeAllSubTasks) {
            historyManager.remove(subTask.getId());
        }
    }

    @Override
    public void removeTaskId(int taskId) {
        tasksMap.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicId(int epicId) {
        Epic removeEpic = epicsMap.remove(epicId);
        if (removeEpic == null) {
            throw new RuntimeException("Epic пустой");
        }
        List<Integer> subtasksId = removeEpic.getSubTasksIds();
        for (int subtaskId : subtasksId) {
            subTasksMap.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void removeSubTaskId(int subTaskId) {
        SubTask removeSubTask = subTasksMap.remove(subTaskId);
        if (removeSubTask == null) {
            throw new RuntimeException("SubTask пустой");
        }
        Epic epic = epicsMap.get(removeSubTask.getEpicId());
        epic.removeSubTask(subTaskId);
        updateStatus(epic.getId());
        historyManager.remove(subTaskId);
    }

    @Override
    public Task createTask(Task task) {
        int taskId = generateCounter();
        task.setId(taskId);
        tasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public int createTask(String name, String description) {
        int taskId = generateCounter();
        Task task = new Task(name, description, Status.NEW, taskId);
        tasksMap.put(taskId, task);
        return taskId;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int epicId = generateCounter();
        epic.setId(epicId);
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public int createEpic(String name, String description) {
        int epicId = generateCounter();
        Epic epic = new Epic(name, description, Status.NEW, epicId);
        epicsMap.put(epicId, epic);
        return epicId;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            int subTaskId = generateCounter();
            subTask.setId(subTaskId);
            subTasksMap.put(subTaskId, subTask);
            epic.addSubTasksIds(subTaskId);
            if (epic.getStatus() == Status.DONE) {
                epic.setStatus(Status.IN_PROGRESS);
                updateEpic(epic);
            }
        }
        return subTask;
    }

    @Override
    public int createSubTask(String name, String description, Epic epic) {
        int subTaskId = generateCounter();
        SubTask subTask = new SubTask(name, description, Status.NEW, epic.getId());
        subTasksMap.put(subTaskId, subTask);
        return subTaskId;
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        Task savedTask = tasksMap.get(taskId);
        if (savedTask == null) {
            throw new RuntimeException("Task не обновился");
        }
        tasksMap.put(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epicsMap.get(epic.getId());
        if (savedEpic == null) {
            throw new RuntimeException("Epic необновился");
        }
        epicsMap.put(epic.getId(), epic);
        int epicId = epic.getId();
        if (epicsMap.containsKey(epic.getId())) {
            savedEpic = epicsMap.get(epicId);
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            epicsMap.put(epic.getId(), savedEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (subTasksMap.containsKey(subTaskId)) {
            SubTask updateSubTask = subTasksMap.get(subTaskId);
            if (epicsMap.containsKey(subTaskId) && (subTask.getEpicId() == updateSubTask.getEpicId())) {
                Epic epic = epicsMap.get(updateSubTask.getEpicId());
                subTasksMap.put(subTaskId, subTask);
                updateStatus(epic.getId());
            }
            if (updateSubTask == null) {
                throw new RuntimeException("SubTask необновился");
            }
        }
    }

    @Override
    public List<SubTask> getSubtasksByEpic(int epicId) { // исправил название метода
        Epic epic = epicsMap.get(epicId);
        historyManager.add(epicsMap.get(epicId));
        if (epic != null) {
            List<SubTask> subtasks = new ArrayList<>();
            for (int subtaskId : epic.getSubTasksIds()) {
                SubTask subtask = subTasksMap.get(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                }
            }
            return subtasks;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasksMap.get(taskId));
        return tasksMap.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epicsMap.get(epicId));
        return epicsMap.get(epicId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subtask = subTasksMap.get(subTaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateCounter() {
        return ++counter;
    }

    protected void updateStatus(int epicId) {
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            int tasksSize = epicsMap.size();
            int statusNEW = 0;
            int statusDONE = 0;

            for (int subTaskId : epic.getSubTasksIds()) {
                SubTask subTaskInEpic = subTasksMap.get(subTaskId);
                if (subTaskInEpic.getStatus() == Status.NEW) {
                    statusNEW++;
                } else if (subTaskInEpic.getStatus() == Status.DONE) {
                    statusDONE++;
                }
            }
            if (tasksSize == 0 || tasksSize == statusNEW) {
                epic.setStatus(Status.NEW);
            } else if (tasksSize == statusDONE) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
