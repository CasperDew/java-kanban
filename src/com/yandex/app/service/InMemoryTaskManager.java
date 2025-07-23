package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.utils.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskMap;
    protected final Map<Integer, Epic> epicMap;
    protected final Map<Integer, Subtask> subtaskMap;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getId)
        );
    }

    private int nextID = 1;

    @Override
    public int getNextID() {
        return nextID++;
    }

    @Override
    public void addTask(Task task) {
        if (isTaskIntersectWithAny(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с существующими задачами");
        }

        task.setId(getNextID());
        taskMap.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNextID());
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTaskIntersectWithAny(subtask)) {
            throw new IllegalArgumentException("Время задачи пересекается с существующими задачами");
        }

        subtask.setId(getNextID());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        Epic epic = epicMap.get(subtask.getEpicID());
        epic.addSubtask(subtask.getId());
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicID());
        updateEpicTime(subtask.getEpicID());
    }

    @Override
    public void updateTask(Task task) {
        Integer taskID = task.getId();
        if (task != null && taskMap.containsKey(taskID) && !isTaskIntersectWithAny(task)) {
            Task oldTask = taskMap.get(task.getId());
            prioritizedTasks.remove(oldTask);

            taskMap.put(task.getId(), task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epic != null && taskMap.containsKey(epicId)) {
            epicMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getEpicID();
        if (subtask != null && taskMap.containsKey(subtaskID) && !isTaskIntersectWithAny(subtask)) {
            int epicID = subtask.getEpicID();
            Subtask oldSubtask = subtaskMap.get(subtaskID);
            prioritizedTasks.remove(oldSubtask);

            subtaskMap.put(subtask.getId(), subtask);

            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

            updateEpicStatus(subtask.getEpicID());
            updateEpicTime(subtask.getEpicID());
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
        Epic epic = epicMap.get(epicID);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Integer> idSubtaskEpic = epic.getSubtaskList();
        List<Subtask> subtaskEpic = new ArrayList<>();
        for (int id : idSubtaskEpic) {
            subtaskEpic.add(subtaskMap.get(id));
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    //очистка задач
    @Override
    public void deleteTasks() {
        for (Integer task : taskMap.keySet()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task);
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epicMap.values()) {
            prioritizedTasks.remove(epic);
            for (Integer idSubtask : epic.getSubtaskList()) {
                Subtask subtask = subtaskMap.get(idSubtask);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
                historyManager.remove(idSubtask);
            }
            historyManager.remove(epic.getId());
        }
        epicMap.clear();
        subtaskMap.clear();

    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtaskMap.values()) {
            prioritizedTasks.remove(subtask);
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
        Task task = taskMap.get(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }

        historyManager.remove(id);
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            prioritizedTasks.remove(epic);

            for (Integer subtaskId : epic.getSubtaskList()) {
                Subtask subtask = subtaskMap.get(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            epicMap.remove(id);
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtaskMap.get(id);
        int epicID = subtask.getEpicID();
        if (subtask != null) {
            prioritizedTasks.remove(subtask);

            epicMap.get(epicID).deleteEpicSubtask(id);
            updateEpicStatus(epicID);

            subtaskMap.remove(id);
            historyManager.remove(id);
        }
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<Integer> iDSubtaskArray = epic.getSubtaskList();
        ArrayList<Subtask> subtasksArray = new ArrayList<>();
        for (Integer id : iDSubtaskArray) {
            subtasksArray.add(subtaskMap.get(id));
        }
        boolean isSubTaskNew = false;
        boolean isSubTaskDone = false;
        if (iDSubtaskArray.isEmpty()) { //если у эпика нет подзадач, то статус должен быть NEW
            epicMap.get(epicId).setStatus(Status.NEW);
        } else {
            for (Subtask subtask : subtasksArray) {
                if (subtask.getStatus().equals(Status.DONE)) isSubTaskDone = true;
                if (subtask.getStatus().equals(Status.NEW)) isSubTaskNew = true;
            }
            if (isSubTaskDone && !isSubTaskNew) epicMap.get(epicId).setStatus(Status.DONE);
            else if (isSubTaskNew && !isSubTaskDone) epicMap.get(epicId).setStatus(Status.NEW);
            else epicMap.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public boolean isTaskIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }
        LocalDateTime task1Start = task1.getStartTime();
        LocalDateTime task1End = task1.getEndTime();
        LocalDateTime task2Start = task2.getStartTime();
        LocalDateTime task2End = task2.getEndTime();

        return !task1End.isBefore(task2Start) &&
                !task2End.isBefore(task1Start) &&
                !task1Start.equals(task2Start) &&
                !task1End.equals(task2Start) &&
                !task2End.equals(task1Start);
    }

    @Override
    public boolean isTaskIntersectWithAny(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }

        for (Task exitingTask : taskMap.values()) {
            if (exitingTask.getId() == task.getId()) {
                continue;
            }
            if (isTaskIntersect(task, exitingTask)) {
                return true;
            }
        }

        for (Subtask existingSubtask : subtaskMap.values()) {
            if (existingSubtask.getId() == task.getId()) {
                continue;
            }
            if (isTaskIntersect(task, existingSubtask)) {
                return true;
            }
        }
        return false;
    }

    protected void updateEpicTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic == null) {
            return;
        }

        List<Subtask> subtasks = getEpicSubtaskByID(epicId);
        if (subtasks.isEmpty()) {
            epic.setEndTime(null);
            return;
        }

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setEndTime(endTime);
    }

}
