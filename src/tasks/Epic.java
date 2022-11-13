package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskState.NEW);
    }

    public void addSubTask(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    @Override
    public String toString() {
        return "" + taskId + ";" + taskName + ";" + taskDescription + ";" + state + ";";
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
}
