package service;

import enums.Status;
import enums.TaskType;
import exceptions.DurationException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import models.Task;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasksMap; // интерфейс Map
    protected final Map<Integer, SubTask> subTasksMap; // интерфейс Map
    protected final Map<Integer, Epic> epicsMap; // интерфейс Map
    protected int counter = 0;
    protected final HistoryManager historyManager;
    protected Set<Task> sortedTasks;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        subTasksMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        this.sortedTasks = new TreeSet<>();
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
        sortedTasks.removeIf(task -> task.getTaskType().equals(TaskType.TASK));
        tasksMap.clear();
        for (Task task : removeAllTasks) {
            historyManager.remove(task.getId());
        }

    }

    @Override
    public void removeAllEpics() {
        List<Epic> removeAllEpics = getEpicList();
        List<SubTask> removeSubTasks = getSubtaskList();
        sortedTasks.removeIf(task -> task.getTaskType().equals(TaskType.SUBTASK));
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
        sortedTasks.removeIf(task -> task.getTaskType().equals(TaskType.SUBTASK));
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
        sortedTasks.remove(tasksMap.get(taskId));
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
            sortedTasks.remove(subTasksMap.get(subtaskId));
            subTasksMap.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void removeSubTaskId(int subTaskId) { // исправил логику перерасчета статуса соответствующего эпика
        SubTask removeSubTask = subTasksMap.remove(subTaskId);
        if (removeSubTask == null) {
            throw new RuntimeException("SubTask пустой");
        }
        Epic epic = epicsMap.get(removeSubTask.getEpicId());
        if (epic == null) {
            throw new RuntimeException("Epic пустой");
        }
        epic.removeSubTask(subTaskId);
        sortedTasks.remove(removeSubTask);
        subTasksMap.remove(subTaskId);
        historyManager.remove(subTaskId);
        updateEpicFields(epic);
    }


    @Override
    public Task createTask(Task task) { // унифицировал все методы создания
        if (checkTimeTask(task)) {
            throw new DurationException("Есть пересечение по времени выполения задач Task!");
        }
        int taskId = generateCounter();
        task.setId(taskId);
        tasksMap.put(task.getId(), task);
        addTaskToSortedTasks(task);
        return task;
    }

    @Override
    public int createTask(String name, String description) {
        int taskId = generateCounter();
        Task task = new Task(name, description, Status.NEW, taskId);
        tasksMap.put(taskId, task);
        addTaskToSortedTasks(task);
        return taskId;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (checkTimeTask(epic)) {
            throw new DurationException("Есть пересечение по времени выполения задач Epic!");
        }
        int epicId = generateCounter();
        epic.setId(epicId);
        epicsMap.put(epic.getId(), epic);
        updateEpicFields(epic);
        addTaskToSortedTasks(epic);
        return epic;
    }

    @Override
    public int createEpic(String name, String description) {
        int epicId = generateCounter();
        Epic epic = new Epic(name, description, Status.NEW, epicId);
        epicsMap.put(epicId, epic);
        updateEpicFields(epic);
        addTaskToSortedTasks(epic);
        return epicId;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (checkTimeTask(subTask)) {
            throw new DurationException("Есть пересечение по времени выполения задач SubTask!");
        }
        int epicId = subTask.getEpicId();
        Epic epic = epicsMap.get(epicId);
        if (epic == null) {
            System.out.println("Нет такого tasks.Epic");
            return null;
        }
        int subtaskId = generateCounter();
        subTask.setId(subtaskId);
        subTasksMap.put(subtaskId, subTask);
        epic.addSubTaskId(subtaskId);
        updateEpicFields(epic);
        addTaskToSortedTasks(subTask);
        return subTask;
    }


    @Override
    public int createSubTask(String name, String description, Epic epic) {
        int subTaskId = generateCounter();
        SubTask subTask = new SubTask(name, description, Status.NEW, epic.getId());
        subTasksMap.put(subTaskId, subTask);
        updateEpicFields(epic);
        addTaskToSortedTasks(subTask);
        return subTaskId;
    }

    @Override
    public void updateTask(Task task) {
        if (checkTimeTask(task)) {
            throw new DurationException("Есть пересечение по времени выполения задач " +
                    "при обновлении Task!");
        }
        int taskId = task.getId();
        Task savedTask = tasksMap.get(taskId);
        if (savedTask == null) {
            throw new RuntimeException("Task не обновился");
        }
        tasksMap.put(taskId, task);
        updateTaskToPrioritizedTasks(task);
    }


    @Override
    public void updateEpic(Epic epic) {
        if (checkTimeTask(epic)) {
            throw new DurationException("Есть пересечение по времени выполения задач " +
                    "при обновлении Epic!");
        }
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
            updateTaskToPrioritizedTasks(epic);
        }

    }


    @Override
    public void updateSubTask(SubTask subTask) {
        if (checkTimeTask(subTask)) {
            throw new DurationException("Есть пересечение по времени выполения задач!");
        }
        int subTaskId = subTask.getId();
        if (subTasksMap.containsKey(subTaskId)) {
            SubTask updateSubTask = subTasksMap.get(subTaskId);
            if (epicsMap.containsKey(subTaskId) && (subTask.getEpicId() == updateSubTask.getEpicId())) {
                Epic epic = epicsMap.get(updateSubTask.getEpicId());
                subTasksMap.put(subTaskId, subTask);
                updateTaskToPrioritizedTasks(subTask);
                updateEpicFields(epic);
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
    public Task getTaskById(int taskId) { // исправил название метода
        Task task = tasksMap.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epicsMap.get(epicId);
        historyManager.add(epic);
        return epic;
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

    @Override
    public Set<Task> getSortedTasks() {
        return sortedTasks;
    }

    private int generateCounter() {
        return ++counter;
    }

    protected void addTaskToSortedTasks(Task task) {
        if (task.getStartTime() != null) sortedTasks.add(task);
    }

    private boolean checkTimeTask(Task task) {
        if (task.getStartTime() != null) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();
            Set<Task> prioritizedTasks = getSortedTasks();
            return !prioritizedTasks.stream().filter(priorityTask -> task.getId() != priorityTask.getId())
                    .allMatch(priorityTask -> (taskEndTime.isBefore(priorityTask.getStartTime())
                            || (taskStartTime.isAfter(priorityTask.getEndTime()))));
        } else return false;
    }

    protected void updateEpicFields(Epic epic) {
        List<Integer> subTaskIds = epic.getSubTasksIds();
        Status status = Status.NEW;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        int subDuration = 0;
        if (!subTaskIds.isEmpty()) {
            Status tmpStatus = null;
            for (Integer subTaskId : subTaskIds) {
                SubTask subtask = subTasksMap.get(subTaskId);
                Status subStatus = subtask.getStatus();
                if (tmpStatus == null) {
                    tmpStatus = subStatus;
                } else if (!tmpStatus.equals(subStatus)) {
                    tmpStatus = Status.IN_PROGRESS;
                }
                LocalDateTime subStartTime = subtask.getStartTime();
                if (subStartTime != null) {
                    LocalDateTime subEndTime = subtask.getEndTime();
                    if (startTime == null || subStartTime.isBefore(startTime)) startTime = subStartTime;
                    if (endTime == null || subEndTime.isAfter(endTime)) endTime = subEndTime;
                    subDuration = subDuration + subtask.getDuration();
                }
            }
            status = tmpStatus;
        }
        if (!status.equals(epic.getStatus())) epic.setStatus(status);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(subDuration);
    }

    protected void updateTaskToPrioritizedTasks(Task task) {
        sortedTasks.removeIf(prioritizedTask -> prioritizedTask.getId() == task.getId());
        if (task.getStartTime() != null) sortedTasks.add(task);
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
