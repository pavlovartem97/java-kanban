package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

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

    List<SubTask> getAllSubtasksInEpic(Epic epicTask);

    List<Task> getHistory();
}
