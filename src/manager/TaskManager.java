package manager;

import java.util.ArrayList;
import java.util.List;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpicTasks();

    SubTask getSubTask(int taskId);

    Task getTask(int taskId);

    Epic getEpic(int taskId);

    int addTask(Task task);

    int addSubTask(SubTask task);

    int addEpicTask(Epic task);

    void updateTask(Task task);

    void updateSubTask(SubTask task);

    void updateEpicTask(Epic task);

    void removeTask(int taskId);

    ArrayList<SubTask> getAllSubtasksInEpic(Epic epicTask);

    List<Task> getHistory();
}