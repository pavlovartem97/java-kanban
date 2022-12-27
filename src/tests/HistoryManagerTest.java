package tests;

import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskState;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private List<Task> tasks = new ArrayList<>();

    @BeforeEach
    public void prepareTasks() {
        Task task1 = new Task("", "", TaskState.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("", "", TaskState.NEW);
        task2.setTaskId(2);
        Task task3 = new Task("", "", TaskState.NEW);
        task3.setTaskId(3);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

    @Test
    public void TestEmptyHistoryManager() {
        Assertions.assertEquals(historyManager.getHistory(), new ArrayList<Task>());
    }

    @Test
    public void TestAddTask() {
        for (Task task : tasks) {
            historyManager.addTask(task);
        }
        Assertions.assertEquals(historyManager.getHistory(), tasks);
    }

    @Test
    public void TestRemove() {
        for (Task task : tasks) {
            historyManager.addTask(task);
        }
        historyManager.remove(tasks.get(1).getTaskId());
        tasks.remove(tasks.get(1));
        Assertions.assertEquals(historyManager.getHistory(), tasks);

        historyManager.remove(tasks.get(0).getTaskId());
        tasks.remove(tasks.get(0));
        Assertions.assertEquals(historyManager.getHistory(), tasks);
    }

    @Test
    public void TestHistoryOrder() {
        historyManager.addTask(tasks.get(0));
        historyManager.addTask(tasks.get(2));
        historyManager.addTask(tasks.get(1));

        historyManager.addTask(tasks.get(0));
        historyManager.addTask(tasks.get(1));
        historyManager.addTask(tasks.get(2));

        Assertions.assertEquals(historyManager.getHistory(), tasks);
    }
}
