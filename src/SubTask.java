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
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskId=" + taskId +
                ", state=" + state +
                '}';
    }
}
