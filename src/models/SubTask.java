package models;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public  SubTask(String name, String description, Status status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void updateStatus() {
        status = Status.NEW;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                "id=" + super.getId() +
                ", epicId=" + epicId +
                '}';
    }
}
