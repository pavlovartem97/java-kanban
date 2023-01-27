package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Epic extends Task {

    private Set<Integer> subTaskIds = new TreeSet<>();

    private LocalDateTime endDateTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
    }

    public void addSubTask(int subTaskId) {
        if (subTaskIds == null) {
            subTaskIds = new TreeSet<>();
        }
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endDateTime;
    }

    public void updateTimes(List<Task> orderedTasks) {
        if (orderedTasks.size() > 0) {
            startTime = orderedTasks.get(0).getStartTime();
            endDateTime = orderedTasks.get(orderedTasks.size() - 1).getStartTime().
                    plusMinutes(orderedTasks.get(orderedTasks.size() - 1).getDuration());
            duration = Duration.between(startTime, endDateTime).toMinutes();
        }
    }

    public void removeSubTask(int subTaskId) {
        if (subTaskIds == null) {
            subTaskIds = new TreeSet<>();
        }
        subTaskIds.remove(subTaskId);
    }

    public void removeAllSubTasks() {
        subTaskIds.clear();
    }

    public Set<Integer> getAllSubTasksId() {
        if (subTaskIds == null) {
            subTaskIds = new TreeSet<>();
        }
        return subTaskIds;
    }

    @Override
    public Epic clone() {
        Epic epic = new Epic(taskName, taskDescription);
        epic.taskId = taskId;
        epic.state = state;
        epic.startTime = startTime;
        epic.duration = duration;
        epic.subTaskIds = subTaskIds;
        epic.endDateTime = endDateTime;
        return epic;
    }
}
