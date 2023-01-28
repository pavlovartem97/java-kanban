package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH::mm");
    protected String taskName;

    protected String taskDescription;

    protected Integer taskId = null;

    protected TaskState state;

    protected LocalDateTime startTime;

    protected long duration;

    protected String type = "TASK";

    public LocalDateTime getEndTime() {
        return startTime.plus(Duration.ofMinutes(duration));
    }

    public Task(String taskName, String taskDescription, TaskState state) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.state = state;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TaskState getState() {
        return state;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskId() {
        return taskId == null ? -1 : taskId;
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
        return taskId + ";" + taskName + ";" + taskDescription + ";" + state + ";"
                + ((startTime != null) ? startTime.format(dateTimeFormatter) : "") + ";" + duration + ";";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId)
                && taskName.equals(task.taskName)
                && taskDescription.equals(task.taskDescription)
                && state.equals(task.state)
                && (startTime == null && task.startTime == null || startTime.equals(task.startTime))
                && duration == task.duration;
    }

    @Override
    public Task clone() {
        Task task = new Task(taskName, taskDescription, state);
        task.duration = duration;
        task.startTime = startTime;
        task.taskId = taskId;
        return task;
    }
}
