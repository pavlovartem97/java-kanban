package tests;

import manager.HttpTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.SubTask;

import java.io.IOException;
import java.util.List;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private final String url = Managers.getDefaultUrl();
    private KVServer kvServer;


    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(url);
        initTasks();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
    }

    @Test
    public void emptyTasksTest() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpicTasks();

        TaskManager newTaskManager = Managers.loadTaskManagerFromServer(url);
        Assertions.assertTrue(newTaskManager.getTasks().isEmpty());
        Assertions.assertTrue(newTaskManager.getSubTasks().isEmpty());
        Assertions.assertTrue(newTaskManager.getEpics().isEmpty());
        Assertions.assertTrue(newTaskManager.getHistory().isEmpty());
    }

    @Test
    public void epicWithoutSubtasksTest() {
        List<SubTask> subtasks = taskManager.getSubTasks();
        for (SubTask subTask : subtasks) {
            taskManager.removeTask(subTask.getTaskId());
        }
        taskManager.getEpic(taskManager.getEpics().get(0).getTaskId());
        taskManager.getTask(taskManager.getTasks().get(0).getTaskId());

        TaskManager newTaskManager = Managers.loadTaskManagerFromServer(url);
        Assertions.assertEquals(newTaskManager.getTasks(), taskManager.getTasks());
        Assertions.assertTrue(newTaskManager.getSubTasks().isEmpty());
        Assertions.assertEquals(newTaskManager.getEpics(), taskManager.getEpics());
        Assertions.assertTrue(newTaskManager.getEpics().get(0).getAllSubTasksId().isEmpty());
        Assertions.assertEquals(newTaskManager.getHistory(), taskManager.getHistory());
    }

    @Test
    public void historyManagerTest() {
        Assertions.assertTrue(taskManager.getHistory().isEmpty());

        TaskManager newTaskManager = Managers.loadTaskManagerFromServer(url);
        Assertions.assertTrue(newTaskManager.getHistory().isEmpty());
        Assertions.assertEquals(newTaskManager.getSubTasks(), taskManager.getSubTasks());
    }

    @Test
    public void prioritizedTasksTest() {
        TaskManager newTaskManager = Managers.loadTaskManagerFromServer(url);
        System.out.println(newTaskManager.getPrioritizedTasks());
        System.out.println(taskManager.getPrioritizedTasks());
        Assertions.assertEquals(newTaskManager.getPrioritizedTasks(), taskManager.getPrioritizedTasks());
    }
}
