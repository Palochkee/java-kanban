package models;


import enums.Status;
import enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class Epic extends Task {

    private final List<Integer> subTasksIds;
    private LocalDateTime endTime;

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
        super(name, description, Status.NEW, id);
        this.subTasksIds = new ArrayList<>();
        this.setType(TaskType.EPIC);
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksIds=" + subTasksIds +
                ", endTime=" + endTime +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return super.equals(o) && subTasksIds.equals(epic.subTasksIds);
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

    public void addSubTaskId(int subtask) {
        if (subtask != super.getId()) {
            subTasksIds.add(subtask);
        }
    }
}



