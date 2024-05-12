package service;


import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyIndex = new HashMap<>();
    private Node first;
    private Node last;

    private static class Node {
        Node next;
        Node prev;
        Task task;

        public Node(Task task) {
            this.task = task;
        }
    }


    @Override
    public void add(Task task) {
        if (historyIndex.containsKey(task.getId())) {
            remove(task.getId());
            historyIndex.remove(task.getId());
        }
        linkLast(task);
        historyIndex.put(task.getId(), last);
    }

    public void remove(int id) {
        if (!historyIndex.containsKey(id)) {
            return;
        }

        if (first == null) {
            return;
        }

        if (first == last) {
            first = null;
            last = null;
            return;
        }

        Node node = historyIndex.get(id);
        if (node == first) {
            first.next.prev = null;
            first = first.next;
            return;
        }
        if (node == last) {
            last.prev.next = null;
            last = last.prev;
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node node = first;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    private void linkLast(Task task) {
        Node oldLast = last;
        Node newNode = new Node(task);
        newNode.prev = oldLast;
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyIndex=" + historyIndex +
                ", head=" + first +
                ", tail=" + last +
                '}';
    }
}