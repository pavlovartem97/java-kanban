package tests;

import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected int epicId;

    protected int subTaskId1;

    protected int subTaskId2;

    protected int taskId;

    protected LocalDateTime startDate = LocalDateTime.of(
            LocalDate.of(2023, 1, 1), LocalTime.of(10, 00));

    protected void initTasks() {
        epicId = taskManager.addEpicTask(new Epic("Epic task", "Epic task description"));
        subTaskId1 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );
        subTaskId2 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );

        Task task = new Task("Common Task 1", "", TaskState.IN_PROGRESS);
        task.setStartTime(startDate);
        task.setDuration(90);
        taskId = taskManager.addTask(task);
    }

    @Test
    public void getTasksTest() {
        Assertions.assertEquals(taskManager.getTasks().size(), 1);

        List<Task> tasks = new ArrayList<>();
        Task task = taskManager.getTask(taskId);
        tasks.add(task);
        Assertions.assertEquals(taskManager.getTasks(), tasks);
    }

    @Test
    public void getSubTasksTest() {
        Assertions.assertEquals(taskManager.getSubTasks().size(), 2);

        List<SubTask> tasks = new ArrayList<>();
        SubTask task1 = taskManager.getSubTask(subTaskId1);
        SubTask task2 = taskManager.getSubTask(subTaskId2);

        tasks.add(task1);
        tasks.add(task2);

        Assertions.assertEquals(taskManager.getSubTasks(), tasks);
    }

    @Test
    public void getEpicTasksTest() {
        Assertions.assertEquals(taskManager.getEpics().size(), 1);

        List<Epic> tasks = new ArrayList<>();
        Epic task = taskManager.getEpic(epicId);
        tasks.add(task);
        Assertions.assertEquals(taskManager.getEpics(), tasks);
    }

    @Test
    public void removeAllTasksTest() {
        taskManager.removeAllTasks();
        Assertions.assertEquals(taskManager.getTasks(), new ArrayList<Task>());
    }

    @Test
    public void removeAllSubtasksTest() {
        taskManager.removeAllSubTasks();
        Assertions.assertEquals(taskManager.getSubTasks(), new ArrayList<SubTask>());
        Assertions.assertEquals(taskManager.getEpic(epicId).getAllSubTasksId().size(), 0);
    }

    @Test
    public void removeAllEpicsTest() {
        taskManager.removeAllEpicTasks();
        Assertions.assertEquals(taskManager.getEpics(), new ArrayList<Epic>());
        Assertions.assertEquals(taskManager.getSubTasks(), new ArrayList<SubTask>());
    }

    @Test
    public void getTaskTest() {
        Task task = taskManager.getTask(taskId);
        Assertions.assertEquals(task.getTaskId(), taskId);
        Assertions.assertNull(taskManager.getTask(epicId));
    }

    @Test
    public void getSubTaskTest() {
        SubTask subTask = taskManager.getSubTask(subTaskId1);
        Assertions.assertEquals(subTask.getTaskId(), subTaskId1);
        Assertions.assertNull(taskManager.getSubTask(taskId));
    }

    @Test
    public void getEpicTest() {
        Epic epic = taskManager.getEpic(epicId);
        Assertions.assertEquals(epic.getTaskId(), epicId);
        Assertions.assertNull(taskManager.getEpic(taskId));
    }

    @Test
    public void addTaskTest() {
        Task task = new Task("", "", TaskState.IN_PROGRESS);
        taskManager.addTask(task);
        Assertions.assertEquals(taskManager.getTasks().size(), 2);
        Assertions.assertEquals(taskManager.getTask(task.getTaskId()), task);

        Task newTask = new Task("", "", TaskState.NEW);
        newTask.setStartTime(startDate.plusHours(1));
        taskManager.addTask(newTask);
        Assertions.assertEquals(taskManager.getTasks().size(), 2);
        Assertions.assertNull(taskManager.getTask(newTask.getTaskId()));
    }

    @Test
    public void addSubTaskTest() {
        SubTask subTask = new SubTask("", "", TaskState.IN_PROGRESS, epicId);
        taskManager.addSubTask(subTask);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 3);
        Assertions.assertEquals(taskManager.getSubTask(subTask.getTaskId()), subTask);
        Assertions.assertEquals(taskManager.getEpic(epicId).getAllSubTasksId().size(), 3);
    }

    @Test
    public void addEpicTest() {
        Epic epic = new Epic("", "");
        int newId = taskManager.addEpicTask(epic);
        Assertions.assertEquals(taskManager.getEpics().size(), 2);
        Assertions.assertEquals(taskManager.getEpic(epic.getTaskId()), epic);
    }

    @Test
    public void updateTaskTest() {
        Task task = taskManager.getTask(taskId);
        task.setState(TaskState.DONE);
        taskManager.updateTask(task);
        Assertions.assertEquals(taskManager.getTask(taskId), task);
    }

    @Test
    public void updateSubTaskTest() {
        SubTask subTask = taskManager.getSubTask(subTaskId1);
        subTask.setState(TaskState.DONE);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(taskManager.getSubTask(subTaskId1), subTask);
    }

    @Test
    public void updateEpicTaskTest() {
        Epic epic = taskManager.getEpic(epicId);
        epic.setTaskName("New Epic Task");
        taskManager.updateEpicTask(epic);
        Assertions.assertEquals(taskManager.getEpic(epicId), epic);
    }

    @Test
    public void removeTaskTest() {
        taskManager.removeTask(taskId);
        Assertions.assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    public void getAllSubtasksInEpicTest() {
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(taskManager.getSubTask(subTaskId1));
        subTasks.add(taskManager.getSubTask(subTaskId2));
        Assertions.assertEquals(taskManager.getAllSubtasksInEpic(taskManager.getEpic(epicId)), subTasks);
    }

    @Test
    public void updateEpicStateTest() {
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.NEW);

        SubTask subTask1 = taskManager.getSubTask(subTaskId1);
        SubTask subTask2 = taskManager.getSubTask(subTaskId2);

        subTask1.setState(TaskState.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.IN_PROGRESS);

        taskManager.removeTask(subTaskId1);
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.NEW);

        subTask2.setState(TaskState.DONE);
        taskManager.updateSubTask(subTask2);
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.DONE);

        SubTask subTask3 = new SubTask("", "", TaskState.NEW, epicId);
        taskManager.addSubTask(subTask3);
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.IN_PROGRESS);

        taskManager.removeAllSubTasks();
        Assertions.assertEquals(taskManager.getEpic(epicId).getState(), TaskState.NEW);
    }

    @Test
    public void historyManagerTest() {
        Assertions.assertEquals(taskManager.getHistory(), new ArrayList<Task>());

        List<Task> history = new ArrayList<>();
        history.add(taskManager.getTask(taskId));
        history.add(taskManager.getSubTask(subTaskId1));
        history.add(taskManager.getEpic(epicId));
        Assertions.assertEquals(taskManager.getHistory(), history);

        history.remove(1);
        taskManager.removeTask(subTaskId1);
        Assertions.assertEquals(taskManager.getHistory(), history);
    }

    @Test
    void getPrioritizedStepTask() {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        Task task = taskManager.getTask(taskId);
        Assertions.assertEquals(prioritizedList.get(0), task);

        SubTask subTask = taskManager.getSubTask(subTaskId1);
        subTask.setStartTime(startDate.minusHours(2));
        subTask.setDuration(60);
        taskManager.updateSubTask(subTask);
        prioritizedList = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(prioritizedList.get(0), subTask);
        Assertions.assertEquals(prioritizedList.get(1), task);
    }

    @Test
    void updateEpicTimeTest() {
        Assertions.assertNull(taskManager.getEpic(epicId).getStartTime());
        Assertions.assertNull(taskManager.getEpic(epicId).getEndTime());

        SubTask subTask1 = taskManager.getSubTask(subTaskId1);
        subTask1.setStartTime(startDate.minusHours(2));
        subTask1.setDuration(60);
        taskManager.updateSubTask(subTask1);

        Assertions.assertEquals(taskManager.getEpic(epicId).getStartTime(), startDate.minusHours(2));
        Assertions.assertEquals(taskManager.getEpic(epicId).getEndTime(), startDate.minusHours(2).plusMinutes(60));

        SubTask subTask2 = taskManager.getSubTask(subTaskId2);
        subTask2.setStartTime(startDate.plusHours(5));
        subTask2.setDuration(90);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(taskManager.getEpic(epicId).getStartTime(), startDate.minusHours(2));
        Assertions.assertEquals(taskManager.getEpic(epicId).getEndTime(), startDate.plusHours(5).plusMinutes(90));
    }
}
