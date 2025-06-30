package com.yandex.app.service;

import com.yandex.app.Exceptions.ManagerSaveException;
import com.yandex.app.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists()) {
            return manager;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) {
                return manager;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty() || line.isBlank()) {
                    continue;
                }
                Task task = fromString(line);
                if (task != null) {
                    switch (task.getType()) {
                        case TASK:
                            manager.taskMap.put(task.getId(), task);
                            break;
                        case EPIC:
                            Epic epic = (Epic) task;
                            manager.epicMap.put(epic.getId(), epic);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;
                            manager.subtaskMap.put(subtask.getId(), subtask);

                            Epic parentEpic = manager.epicMap.get(subtask.getEpicID());
                            if (parentEpic != null) {
                                parentEpic.addSubtask(subtask);
                            }
                            break;
                    }
                }
            }

            for (Epic epic : manager.epicMap.values()) {
                manager.updateEpicStatus(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла", e);
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0]);
            Type type = Type.valueOf(parts[1]);
            String name = parts[2];
            Status status = Status.valueOf(parts[3]);
            String description = parts[4];

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description, status);
                    epic.setId(id);
                    return epic;
                case SUBTASK:
                    if (parts.length < 6) {
                        return null;
                    }
                    int epicId = Integer.parseInt(parts[5]);
                    Subtask subtask = new Subtask(name, description, status, epicId);
                    subtask.setId(id);
                    return subtask;
                default:
                    return null;
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла", e);
        }
    }

    private String toString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",");
        builder.append(task.getType()).append(",");
        builder.append(task.getName()).append(",");
        builder.append(task.getStatus()).append(",");
        builder.append(task.getDescription());

        if (task.getType() == Type.SUBTASK) {
            builder.append(",").append(((Subtask) task).getEpicID());
        }

        return builder.toString();
    }
}
