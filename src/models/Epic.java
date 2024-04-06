package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksIds;

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.subTasksIds = new ArrayList<>();
    }
    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }
    public void addSubTasksIds(int subTaskId) {
        subTasksIds.add(subTaskId);
    }
    public void updateStatus() {
        status = Status.NEW;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                "id=" + super.getId() +
                '}';
    }
}
