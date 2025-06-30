package com.yandex.app.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.setType(Type.EPIC);
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.setType(Type.EPIC);

    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.setType(Type.EPIC);

    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtask() {
        subtaskList.clear();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}
