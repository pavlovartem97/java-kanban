package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> historyLinkedList = new CustomLinkedList<>();

    @Override
    public void remove(int id) {
        historyLinkedList.remove(id);
    }

    @Override
    public void addTask(Task task) {
        historyLinkedList.addLast(task, task.getTaskId());
    }

    @Override
    public List<Task> getHistory() {
        return historyLinkedList.getTasks();
    }
}

class Node<T> {

    T value;

    Node<T> prev;

    Node<T> next;

    public Node(T value, Node<T> prev, Node<T> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }
}

class CustomLinkedList<T> {

    private Node<T> first;

    private Node<T> last;

    private final Map<Integer, Node<T>> historyTaskMap = new HashMap<>();

    private Node<T> addLast(T task) {
        if (last == null) {
            last = new Node(task, null, null);
            first = last;
        } else {
            Node<T> newNode = new Node<>(task, last, null);
            last.next = newNode;
            last = newNode;
        }
        return last;
    }

    private void remove(Node<T> node) {
        if (first == last) {
            first = null;
            last = null;
        } else if (node == first) {
            first = first.next;
            first.prev = null;
        } else if (node == last) {
            last = last.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public void remove(int id) {
        if (historyTaskMap.containsKey(id)) {
            this.remove(historyTaskMap.remove(id));
        }
    }

    public void addLast(T task, int id) {
        if (historyTaskMap.containsKey(id)) {
            this.remove(historyTaskMap.get(id));
        }
        historyTaskMap.put(id, this.addLast(task));
    }

    public List<T> getTasks() {
        List<T> tasks = new ArrayList<>();

        Node<T> currentNode = first;
        while (currentNode != null) {
            tasks.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return tasks;
    }
}