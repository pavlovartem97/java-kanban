package tasks;

import java.util.Objects;

public class Task {

    protected String taskName;

    protected String taskDescription;

    protected int taskId = -1;
    protected TaskState state;

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.state = state;
    }

    public TaskState getState() {
        return state;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    ;

    public int getTaskId() {
        return taskId;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public String toString() {
        return "" + taskId + ";" + taskName + ";" + taskDescription + ";" + state + ";";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(taskDescription, task.taskDescription)
                && state == task.state;
    }
}
