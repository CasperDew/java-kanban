package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;

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

    public void addSubtask(int subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtask() {
        subtaskList.clear();
    }

    public void deleteEpicSubtask(Integer idSubtask) {
        subtaskList.remove(idSubtask);
    }

    public ArrayList<Integer> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    public void setSubtaskList(ArrayList<Integer> subtaskList) {
        this.subtaskList = subtaskList;
    }

    /*
        Расчет времени происходит на основе подзадач
     */
    @Override
    public LocalDateTime getStartTime() {
        return null;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {

    }

    @Override
    public Duration getDuration() {
        return null;
    }

    @Override
    public void setDuration(Duration duration) {

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
        return "com.yandex.app.model.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}
