package service;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, SubTask> subTasksMap;
    private final Map<Integer, Epic> epicsMap;
    private int counter = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasksMap = new HashMap<>();
        this.epicsMap = new HashMap<>();
        this.subTasksMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
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
        tasksMap.clear();
    }


    @Override
    public void removeAllEpics() {
        epicsMap.clear();
        subTasksMap.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subTasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            updateStatus(epic.getId());
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
        if (removeEpic != null) {
            for (int subtaskId : removeEpic.getSubTasksIds()) {
                subTasksMap.remove(subtaskId);
            }
        }
    }

    @Override
    public void removeSubTaskId(int subTaskId) { // исправил логику перерасчета статуса соответствующего эпика
        SubTask removeSubTask = subTasksMap.remove(subTaskId);
        if (removeSubTask != null) {
            Epic epic = getEpicById(removeSubTask.getEpicId());
            if (epic != null) {
                epic.getSubTasksIds().remove(Integer.valueOf(subTaskId));
                updateStatus(epic.getId());
            }
        }
    }

    @Override
    public void createTask(Task task) {
        int taskId = generateCounter();
        task.setId(taskId);
        tasksMap.put(task.getId(), task);
    }


    @Override
    public void createEpic(Epic epic) {
        int epicId = generateCounter();
        epic.setId(epicId);
        epicsMap.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
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
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (tasksMap.containsKey(taskId)) {
            tasksMap.put(taskId, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epicsMap.containsKey(epic.getId())) {
            Epic savedEpic = epicsMap.get(epicId);
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            epicsMap.put(epic.getId(), savedEpic);
        }

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (subTasksMap.containsKey(subTaskId)) {
            SubTask savedSubTask = subTasksMap.get(subTaskId);
            if (epicsMap.containsKey(subTaskId) && (subTask.getEpicId() == savedSubTask.getEpicId())) {
                Epic epic = epicsMap.get(savedSubTask.getEpicId());
                subTasksMap.put(subTaskId, subTask);
                updateStatus(epic.getId());
            }
        }
    }


    @Override
    public List<SubTask> getSubTasksEpicsIds(int epicId) {
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
    public Task get(int taskId) {
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

    private void updateStatus(int epicId) {
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            int tasksSize = epicsMap.size();
            int StatusNEW = 0;
            int StatusDONE = 0;

            for (int subTaskId : epic.getSubTasksIds()) {
                SubTask subTaskInEpic = subTasksMap.get(subTaskId);
                if (subTaskInEpic.getStatus() == Status.NEW) {
                    StatusNEW++;
                } else if (subTaskInEpic.getStatus() == Status.DONE) {
                    StatusDONE++;
                }
            }
            if (tasksSize == 0 || tasksSize == StatusNEW) {
                epic.setStatus(Status.NEW);
            } else if (tasksSize == StatusDONE) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
