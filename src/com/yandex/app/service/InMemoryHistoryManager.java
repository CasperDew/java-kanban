package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_LIMIT = 10;
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {

        if (historyList.size() == HISTORY_LIMIT) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}
