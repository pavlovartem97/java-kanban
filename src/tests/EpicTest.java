package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskState;

import java.util.ArrayList;
import java.util.List;

class EpicTest {

    private Epic epic;

    private List<SubTask> generateSubtasks(TaskState[] taskStates) {
        List<SubTask> subTasks = new ArrayList<>();
        for (int i = 0; i < taskStates.length; ++i) {
            subTasks.add(new SubTask("Subtask name", "Subtask description", taskStates[i], epic.getTaskId()));
        }
        return subTasks;
    }

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("Task name", "task description");
    }

    @Test
    public void testEmptySubtaskList() {
        Assertions.assertEquals(epic.getAllSubTasksId().size(), 0);
        Assertions.assertEquals(epic.getState(), TaskState.NEW);
    }

    @Test
    public void testAllSubtasksNew() {
        List<SubTask> subTasks = generateSubtasks(new TaskState[]{TaskState.NEW, TaskState.NEW});
        InMemoryTaskManager.updateEpicTaskState(epic, subTasks);
        Assertions.assertEquals(epic.getState(), TaskState.NEW);
    }

    @Test
    public void testAllSubtasksDone() {
        List<SubTask> subTasks = generateSubtasks(new TaskState[]{TaskState.DONE, TaskState.DONE});
        InMemoryTaskManager.updateEpicTaskState(epic, subTasks);
        Assertions.assertEquals(epic.getState(), TaskState.DONE);
    }

    @Test
    public void testAllSubtasksInProgress() {
        List<SubTask> subTasks = generateSubtasks(new TaskState[]{TaskState.IN_PROGRESS, TaskState.IN_PROGRESS});
        InMemoryTaskManager.updateEpicTaskState(epic, subTasks);
        Assertions.assertEquals(epic.getState(), TaskState.IN_PROGRESS);
    }

    @Test
    public void testAllSubtasksNewAndDone() {
        List<SubTask> subTasks = generateSubtasks(new TaskState[]{TaskState.NEW, TaskState.DONE});
        InMemoryTaskManager.updateEpicTaskState(epic, subTasks);
        Assertions.assertEquals(epic.getState(), TaskState.IN_PROGRESS);
    }
}