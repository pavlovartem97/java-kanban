package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    private LocalDateTime endDateTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
    }

    public void addSubTask(int subTaskId) {
        subTaskIds.add(subTaskId);
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
        for (int i = 0; i < subTaskIds.size(); ++i) {
            if (subTaskIds.get(i) == subTaskId) {
                subTaskIds.remove(i);
            }
        }
    }

    public void removeAllSubTasks() {
        subTaskIds.clear();
    }

    public ArrayList<Integer> getAllSubTasksId() {
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
