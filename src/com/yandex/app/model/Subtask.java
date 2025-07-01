package com.yandex.app.model;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
        this.setType(Type.SUBTASK);
    }

    public Subtask(String title, String description, Status status, int epicID) {
        super(title, description, status);
        this.epicID = epicID;
        this.setType(Type.SUBTASK);
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
        this.setType(Type.SUBTASK);
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicID=" + epicID +
                ", status=" + getStatus() +
                '}';
    }
}
