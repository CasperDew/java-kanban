package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_LIMIT = 10;
    private final LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void add(Task task) {

        if (historyList.size() == HISTORY_LIMIT) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}
