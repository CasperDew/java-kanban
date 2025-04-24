package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();

    private int nextID = 1;

    public int getNextID() {
        return nextID++;
    }

    public Task addTask(Task task) {
        task.setId(getNextID());
        taskMap.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(getNextID());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(getNextID());
        Epic epic = epicMap.get(subtask.getEpicID());
        epic.addSubtask(subtask);
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if(taskID == null || !taskMap.containsKey(taskID)) {
            return null;
        }

        taskMap.replace(taskID, task);
        return task;
    }

    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if(epicId == null || !epicMap.containsKey(epicId)) {
            return null;
        }

        Epic oldEpic = epicMap.get(epicId);
        ArrayList<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if(!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtaskMap.remove(subtask.getId());
            }
        }
        epicMap.replace(epicId, epic);

        ArrayList<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if(!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList){
                subtaskMap.put(subtask.getId(), subtask);
            }
        }

        updateEpicStatus(epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getEpicID();
        if(subtaskID == null || !subtaskMap.containsKey(subtaskID)) {
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
    public Task getTaskByID(int id) {
        return taskMap.get(id);
    }

    public Epic getEpicByID(int id) {
        return epicMap.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtaskMap.get(id);
    }

    //получение подзадачи Эпик метода по id эпика
    public List<Subtask> getEpicSubtaskByID(int epicID) {

        List<Subtask> subtaskEpic = new ArrayList<>();
        for(Subtask subtask : subtaskMap.values()) {
            if(subtask.getEpicID() == epicID) {
                subtaskEpic.add(subtask);
            }
        }
        return subtaskEpic;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    //очистка задач
    public void deleteTasks() {
        taskMap.clear();
    }

    public void deleteEpics() {
        epicMap.clear();
        List<Subtask> subtasks = getSubtasks();
        if(subtasks != null) {
            deleteSubtasks();
        }
    }

    public void deleteSubtasks() {
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubtask();
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteTaskByID(int id) {
        Task task = taskMap.get(id);
        if(task != null) {
            taskMap.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        ArrayList<Subtask> epicSubtask = epicMap.get(id).getSubtaskList();
        if(epicSubtask != null){
            epicMap.remove(id);

            for (Subtask subtask : epicSubtask) {
                subtaskMap.remove(subtask.getId());
            }
        }
    }

    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtaskMap.get(id);
        int epicID = subtask.getEpicID();
        if(subtask != null) {
            subtaskMap.remove(id);

            Epic epic = epicMap.get(epicID);
            ArrayList<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            epic.setSubtaskList(subtaskList);
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        int allDoneCount = 0;
        int allNewCount = 0;
        ArrayList<Subtask> list = epic.getSubtaskList();

        for(Subtask subtask : list) {
            if(subtask.getStatus() == Status.DONE) {
                allDoneCount++;
            }
            if(subtask.getStatus() == Status.NEW) {
                allNewCount++;
            }
        }
        if(allDoneCount == list.size()) {
            epic.setStatus(Status.DONE);
        } if(allNewCount == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
