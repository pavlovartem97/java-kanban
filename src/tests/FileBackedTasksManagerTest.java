package tests;

import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;

import java.io.File;
import java.util.List;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private final File file = new File("data.csv");

    @BeforeEach
    public void setUp() {
        taskManager = new FileBackedTasksManager(file);
        initTasks();
    }

    @Test
    public void emptyTasksTest() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubTasks();
        taskManager.removeAllEpicTasks();

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertEquals(newTaskManager.getTasks().size(), 0);
        Assertions.assertEquals(newTaskManager.getSubTasks().size(), 0);
        Assertions.assertEquals(newTaskManager.getEpics().size(), 0);
        Assertions.assertEquals(newTaskManager.getHistory().size(), 0);
    }

    @Test
    public void epicWithoutSubtasksTest() {
        List<SubTask> subtasks = taskManager.getSubTasks();
        for (SubTask subTask : subtasks) {
            taskManager.removeTask(subTask.getTaskId());
        }
        taskManager.getEpic(taskManager.getEpics().get(0).getTaskId());
        taskManager.getTask(taskManager.getTasks().get(0).getTaskId());

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertEquals(newTaskManager.getTasks(), taskManager.getTasks());
        Assertions.assertEquals(newTaskManager.getSubTasks().size(), 0);
        Assertions.assertEquals(newTaskManager.getEpics(), taskManager.getEpics());
        Assertions.assertEquals(newTaskManager.getEpics().get(0).getAllSubTasksId().size(), 0);
        Assertions.assertEquals(newTaskManager.getHistory(), taskManager.getHistory());
    }

    @Test
    public void historyManagerTest() {
        Assertions.assertEquals(taskManager.getHistory().size(), 0);

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertEquals(newTaskManager.getHistory().size(), 0);
        Assertions.assertEquals(newTaskManager.getSubTasks(), taskManager.getSubTasks());
    }
}
