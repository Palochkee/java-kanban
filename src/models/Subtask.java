package models;

import util.Status;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(String name, String description, Epic epic, Status status) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", epic=" + epic +
                '}';
    }
}
