package models;


import enums.Status;
import enums.TaskType;

public class SubTask extends Task {
    private final int epicId; //

    public SubTask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }


    public int getEpicId() {
        return epicId;
    }


    public String toString() {
        return "Subtask{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "id=" + id +
                ", epicId=" + epicId +
                '}';
    }

}

