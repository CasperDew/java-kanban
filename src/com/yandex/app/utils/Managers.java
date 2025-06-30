package com.yandex.app.utils;

import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.InMemoryHistoryManager;
import com.yandex.app.service.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
