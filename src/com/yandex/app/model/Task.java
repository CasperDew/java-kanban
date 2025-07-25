package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private Type type;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
        this.startTime = (startTime != null) ? startTime.withSecond(0).withNano(0) : null;
        this.duration = duration;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
        this.startTime = (startTime != null) ? startTime.withSecond(0).withNano(0) : null;
        this.duration = duration;
    }

    public Task(String title, String description, Status status) {
        this.name = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
        this.startTime = (startTime != null) ? startTime.withSecond(0).withNano(0) : null;
        this.duration = duration;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.name = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
        this.startTime = (startTime != null) ? startTime.withSecond(0).withNano(0) : null;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String startTimeStr = startTime != null ? startTime.format(formatter) : "null";
        String durationStr = duration != null ? duration.toMinutes() + " минут" : "null";
        String endTimeStr = getEndTime() != null ? getEndTime().format(formatter) : "null";

        return "com.yandex.app.model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", startTime=" + startTimeStr +
                ", duration=" + durationStr +
                ", endTime=" + endTimeStr +
                '}';
    }
}
