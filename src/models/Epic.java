package models;


import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {

    private final List<Integer> subTasksIds;

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
        this.subTasksIds = new ArrayList<>();
    }

    public void addSubTasksIds(int subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public String toString() {
        return "Subtask{" +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                "id=" + super.getId() +
                '}';
    }
}



