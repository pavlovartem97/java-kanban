package tasks;

public class SubTask extends Task {

    private int epicTaskId;

    public SubTask(String taskName, String taskDescription, TaskState state, Integer epicTaskId) {
        super(taskName, taskDescription, state);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicId() {
        return epicTaskId;
    }

    @Override
    public String toString() {
        return super.toString() + epicTaskId + ";";
    }

    @Override
    public SubTask clone() {
        SubTask subTask = new SubTask(taskName, taskDescription, state, epicTaskId);
        subTask.duration = duration;
        subTask.startTime = startTime;
        subTask.taskId = taskId;
        subTask.epicTaskId = epicTaskId;
        return subTask;
    }
}
