package service;

import models.Epic;
import models.Subtask;
import models.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idCounter = 0;

    private HashMap<Integer, Task> allTasks;
    private HashMap<Integer, Epic> allEpics;
    private HashMap<Integer, Subtask> allSubtasks;

    public TaskManager() {
        this.allTasks = new HashMap<>();
        this.allEpics = new HashMap<>();
        this.allSubtasks = new HashMap<>();
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(allTasks.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(allEpics.values());
    }

    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(allSubtasks.values());
    }

    public void deleteAllTasks() {
        allTasks.clear();
    }

    public void deleteAllEpics() {
        allEpics.clear();
        allSubtasks.clear();
    }

    public void deleteAllSubtask() {
        for (Subtask subtask : allSubtasks.values()) {
            Epic epic = subtask.getEpic();
            List<Subtask> EpicSubtasks = epic.getSubtasks();
            EpicSubtasks.clear();
            checkEpicStatus(epic);
        }
        allSubtasks.clear();
    }

    public Task getTaskById(int id) {
        return allTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return allEpics.get(id);
    }

    public Subtask getSubtasksById(int id) {
        return allSubtasks.get(id);
    }

    public Task saveTask(Task task) {
        task.setId(generateId());
        allTasks.put(task.getId(), task);
        return task;

    }

    public Epic saveEpic(Epic epic) {
        epic.setId(generateId());
        allEpics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask saveSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtask.getEpic().getSubtasks().add(subtask);
        allSubtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
        return subtask;
    }

    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic == null) {
            return;
        }
        Epic saved = allEpics.get(updatedEpic.getId());
        saved.setName(updatedEpic.getName());
        saved.setDescription(updatedEpic.getDescription());
    }

    public void updateSubtask(Subtask updatedSubtask) {
        Subtask oldSubtask = allSubtasks.put(updatedSubtask.getId(), updatedSubtask);
        Epic epic = updatedSubtask.getEpic();
        epic.getSubtasks().remove(oldSubtask);
        epic.getSubtasks().add(updatedSubtask);
        checkEpicStatus(epic);
    }

    public void deleteTaskById(int id) {
        allTasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic deletedEpic = allEpics.remove(id);
        List<Subtask> deletedEpicSubtasks = deletedEpic.getSubtasks();
        deletedEpicSubtasks.forEach(subtask -> allSubtasks.remove(subtask.getId()));
        deletedEpicSubtasks.clear();
    }

    public List<Subtask> getSubtaskByEpicId(int id) {
        return allEpics.get(id).getSubtasks();
    }

    private void checkEpicStatus(Epic epic) {
        List<Subtask> epicSubtasks = epic.getSubtasks();
        int epicSubtasksSize = epicSubtasks.size();
        int SubtaskWithStatusNEW = 0;
        int SubtaskWithStatusDONE = 0;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() == Status.NEW) {
                SubtaskWithStatusNEW++;
            } else if (subtask.getStatus() == Status.DONE) {
                SubtaskWithStatusDONE++;
            }
        }
        if (epicSubtasksSize == 0 || epicSubtasksSize == SubtaskWithStatusNEW) {
            epic.setStatus(Status.NEW);
        } else if (epicSubtasksSize == SubtaskWithStatusDONE) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private int generateId() {
        return ++idCounter;
    }
}

