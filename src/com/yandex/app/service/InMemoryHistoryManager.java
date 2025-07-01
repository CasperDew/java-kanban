package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyList = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistory() {
        final ArrayList<Task> history = new ArrayList<>();
        Node currentNode = tail;

        while (currentNode != null) {
            history.add(currentNode.task);
            currentNode = currentNode.prev;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(historyList.remove(task.getId()));
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyList.remove(id));
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        historyList.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node != null) {
            if (node == head) {
                if (head.next == null) {
                    head = null;
                    tail = null;
                    node = null;
                } else {
                    head = head.next;
                    head.prev = null;
                    node = null;
                }
            } else if (node == tail) {
                tail = tail.prev;
                tail.next = null;
                node = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                node = null;
            }
        }
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }
}
