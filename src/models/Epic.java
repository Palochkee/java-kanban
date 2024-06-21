package models;


import enums.Status;
import enums.TaskType;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {

    private final List<Integer> subTasksIds;

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.subTasksIds = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksIds = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
        this.subTasksIds = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public void addSubTasksIds(int subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void removeSubtaskIds(Integer subtask) {
        subTasksIds.remove(subtask);
    }

    public void updateStatus() {
        status = Status.NEW;
    }

    public String toString() {
        return "Subtask{" +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                "id=" + super.getId() +
                '}';
    }

    public void removeSubTask(int id) {
        for (int i = 0; i < subTasksIds.size(); i++)
            if (id == subTasksIds.get(i)) {
                subTasksIds.remove(i);
                break;
            }
    }

    public void removeAllSubtasks() {
        subTasksIds.clear();
    }
}



