package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextID = 1;

    @Override
    public int getNextID() {
        return nextID++;
    }

    @Override
    public Task addTask(Task task) {
        task.setId(getNextID());
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getNextID());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(getNextID());
        Epic epic = epicMap.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!taskMap.containsKey(taskID)) {
            return null;
        }

        taskMap.replace(taskID, task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (!epicMap.containsKey(epicId)) {
            return null;
        }

        Epic oldEpic = epicMap.get(epicId);
        ArrayList<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if (!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtaskMap.remove(subtask.getId());
            }
        }
        epicMap.replace(epicId, epic);

        ArrayList<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if (!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList) {
                subtaskMap.put(subtask.getId(), subtask);
            }
        }

        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getEpicID();
        if (!subtaskMap.containsKey(subtaskID)) {
            return null;
        }

        int epicID = subtask.getEpicID();
        Subtask oldSubtask = subtaskMap.get(subtaskID);
        subtaskMap.replace(subtaskID, subtask);

        Epic epic = epicMap.get(epicID);
        ArrayList<Subtask> subtasksList = epic.getSubtaskList();
        subtasksList.remove(oldSubtask);
        subtasksList.add(subtask);
        epic.setSubtaskList(subtasksList);

        updateEpicStatus(epic);
        return subtask;
    }

    // методы получения по id
    @Override
    public Task getTaskByID(int id) {
        Task task = taskMap.get(id);
        if (task == null) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
        } else {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epicMap.get(id);
        if (epic == null) {
            System.out.println("Эпика с идентификатором " + id + " нет!");
        } else {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        return subtaskMap.get(id);
    }

    //получение подзадачи Эпик метода по id эпика
    @Override
    public List<Subtask> getEpicSubtaskByID(int epicID) {

        List<Subtask> subtaskEpic = new ArrayList<>();
        for (Subtask subtask : subtaskMap.values()) {
            if (subtask.getEpicID() == epicID) {
                subtaskEpic.add(subtask);
            }
        }
        return subtaskEpic;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    //очистка задач
    @Override
    public void deleteTasks() {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        epicMap.clear();
        List<Subtask> subtasks = getSubtasks();

        if (subtasks != null) {
            deleteSubtasks();
        }

        for (Epic epic : epicMap.values()) {
            historyManager.remove(epic.getId());
            for (Subtask idSubtask : epic.getSubtaskList()) {
                historyManager.remove(idSubtask.getId());
            }
        }
    }

    @Override
    public void deleteSubtasks() {
        subtaskMap.clear();
        for (Subtask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
        }

        for (Epic epic : epicMap.values()) {
            epic.clearSubtask();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtask = epicMap.get(id).getSubtaskList();
        if (epicSubtask != null) {
            epicMap.remove(id);
            historyManager.remove(id);

            for (Subtask subtask : epicSubtask) {
                subtaskMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtaskMap.get(id);
        int epicID = subtask.getEpicID();
        if (subtask != null) {
            subtaskMap.remove(id);
            historyManager.remove(id);

            Epic epic = epicMap.get(epicID);
            ArrayList<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            epic.setSubtaskList(subtaskList);
            updateEpicStatus(epic);
        }
    }

    public void updateEpicStatus(Epic epic) {
        int allDoneCount = 0;
        int allNewCount = 0;
        ArrayList<Subtask> list = epic.getSubtaskList();

        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allDoneCount++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allNewCount++;
            }
        }
        if (allDoneCount == list.size()) {
            epic.setStatus(Status.DONE);
        }
        if (allNewCount == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}
