package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskMap;
    protected final Map<Integer, Epic> epicMap;
    protected final Map<Integer, Subtask> subtaskMap;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    private int nextID = 1;

    @Override
    public int getNextID() {
        return nextID++;
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNextID());
        taskMap.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNextID());
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(getNextID());
        Epic epic = epicMap.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void updateTask(Task task) {
        Integer taskID = task.getId();
        if (task != null && taskMap.containsKey(taskID)) {
            taskMap.replace(taskID, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epic != null && taskMap.containsKey(epicId)) {
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
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getEpicID();
        if (subtask != null && taskMap.containsKey(subtaskID)) {
            int epicID = subtask.getEpicID();
            Subtask oldSubtask = subtaskMap.get(subtaskID);
            subtaskMap.replace(subtaskID, subtask);

            Epic epic = epicMap.get(epicID);
            ArrayList<Subtask> subtasksList = epic.getSubtaskList();
            subtasksList.remove(oldSubtask);
            subtasksList.add(subtask);
            epic.setSubtaskList(subtasksList);

            updateEpicStatus(epic);
        }
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
        for (Integer task : taskMap.keySet()) {
            historyManager.remove(task);
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epicMap.values()) {
            for (Subtask idSubtask : epic.getSubtaskList()) {
                historyManager.remove(idSubtask.getId());
            }
            historyManager.remove(epic.getId());
        }
        epicMap.clear();
        subtaskMap.clear();

    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
        }

        subtaskMap.clear();
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
        Epic epic = epicMap.remove(id);
        if (epic != null) {
            historyManager.remove(id);
            ArrayList<Subtask> epicSubtask = epic.getSubtaskList();

            if (!epicSubtask.isEmpty()) {
                for (Subtask subtask : epicSubtask) {
                    subtaskMap.remove(subtask.getId());
                    historyManager.remove(subtask.getId());
                }
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
