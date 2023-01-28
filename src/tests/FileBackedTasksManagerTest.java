package tests;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;

import java.util.List;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final String fileName = "data.csv";

    @BeforeEach
    public void setUp() {
        taskManager = new FileBackedTasksManager(fileName);
        initTasks();
    }

    @Test
    public void emptyTasksTest() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpicTasks();

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(fileName);
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

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(fileName);
        Assertions.assertEquals(newTaskManager.getTasks(), taskManager.getTasks());
        Assertions.assertTrue(newTaskManager.getSubTasks().isEmpty());
        Assertions.assertEquals(newTaskManager.getEpics(), taskManager.getEpics());
        Assertions.assertTrue(newTaskManager.getEpics().get(0).getAllSubTasksId().isEmpty());
        Assertions.assertEquals(newTaskManager.getHistory(), taskManager.getHistory());
    }

    @Test
    public void historyManagerTest() {
        Assertions.assertTrue(taskManager.getHistory().isEmpty());

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(fileName);
        Assertions.assertTrue(newTaskManager.getHistory().isEmpty());
        Assertions.assertEquals(newTaskManager.getSubTasks(), taskManager.getSubTasks());
    }

    @Test
    public void prioritizedTasksTest() {
        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(fileName);
        Assertions.assertEquals(newTaskManager.getPrioritizedTasks(), taskManager.getPrioritizedTasks());
    }
}
