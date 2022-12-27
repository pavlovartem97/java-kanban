package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String TASK_KEY = "TASK";

    private final String SUBTASK_KEY = "SUBTASK";

    private final String EPIC_KEY = "EPIC";

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        fileBackedTasksManager.load();
        return fileBackedTasksManager;
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int addSubTask(SubTask task) {
        super.addSubTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int addEpicTask(Epic task) {
        super.addEpicTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public SubTask getSubTask(int taskId) {
        SubTask task = super.getSubTask(taskId);
        save();
        return task;
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int taskId) {
        Epic epic = super.getEpic(taskId);
        save();
        return epic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    private void save() {
        try {
            try (BufferedWriter buffer = new BufferedWriter(new FileWriter(file));) {
                for (Task task : super.getTasks()) {
                    buffer.write(TASK_KEY + ";" + task.toString() + "-1;" + "\n");
                }
                for (Task task : super.getEpics()) {
                    buffer.write(EPIC_KEY + ";" + task.toString() + "-1;" + "\n");
                }
                for (Task task : super.getSubTasks()) {
                    buffer.write(SUBTASK_KEY + ";" + task.toString() + "\n");
                }

                buffer.write("\n");
                buffer.write(historyToString() + "\n");
            } catch (IOException error) {
                throw new ManagerSaveException("неправильная работа с файлом");
            }
        } catch (ManagerSaveException exception) {
            System.out.println("Ошибка записи: " + exception.getMessage());
        }
    }

    private void load() {
        try {
            try (BufferedReader buffer = new BufferedReader(new FileReader(file));) {
                int maxId = -1;
                while (buffer.ready()) {
                    String line = buffer.readLine();
                    if (!line.isEmpty()) {
                        int taskId = addTaskFromString(line);
                        maxId = Integer.max(maxId, taskId);
                    } else {
                        line = buffer.readLine();
                        parseHistoryFromString(line);
                    }
                }
                super.uniqueIdNumber = maxId + 1;
            } catch (IOException error) {
                throw new ManagerSaveException("неправильная работа с файлом");
            }
        } catch (ManagerSaveException exception) {
            System.out.println("Ошибка чтения: " + exception.getMessage());
        }
    }

    private TaskState getTaskStateFormString(String state) throws ManagerSaveException {
        try {
            return TaskState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ManagerSaveException("Неопознанное состояние задачи: " + state);
        }
    }

    private int addTaskFromString(String taskStr) throws ManagerSaveException {
        String[] items = taskStr.split(";");
        if (items.length != 8) {
            throw new ManagerSaveException("Некорректные данные в строке: " + taskStr);
        }

        Integer taskId = -1;
        try {
            taskId = Integer.parseInt(items[1]);
        } catch (Throwable exception) {
            throw new ManagerSaveException("Ошибка парсинга id задачи: " + items[1]);
        }

        String taskKey = items[0];
        String taskName = items[2];
        String taskDescription = items[3];
        TaskState taskState = getTaskStateFormString(items[4]);
        LocalDateTime startDate = null;
        Integer duration = null;

        if (!items[5].isEmpty()) {
            startDate = LocalDateTime.parse(items[5], Task.dateTimeFormatter);
        }
        if (!items[6].isEmpty()) {
            duration = Integer.parseInt(items[6]);
        }

        switch (taskKey) {
            case TASK_KEY:
                Task task = new Task(taskName, taskDescription, taskState);
                task.setTaskId(taskId);
                if (startDate != null) {
                    task.setStartTime(startDate);
                }
                if (duration != null) {
                    task.setDuration(duration);
                }
                super.addTask(task);
                break;
            case EPIC_KEY:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setTaskId(taskId);
                super.addEpicTask(epic);
                break;
            case SUBTASK_KEY:
                int epicId = Integer.parseInt(items[7]);
                SubTask subTask = new SubTask(taskName, taskDescription, taskState, epicId);
                subTask.setTaskId(taskId);
                if (startDate != null) {
                    subTask.setStartTime(startDate);
                }
                if (duration != null) {
                    subTask.setDuration(duration);
                }
                super.addSubTask(subTask);
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + taskKey);
        }

        return taskId;
    }

    private String historyToString() {
        List<Task> historyTasks = super.getHistory();
        String[] taskIds = new String[historyTasks.size()];
        for (int i = 0; i < historyTasks.size(); ++i) {
            taskIds[i] = Integer.toString(historyTasks.get(i).getTaskId());
        }
        return String.join(";", taskIds);
    }

    private void parseHistoryFromString(String value) throws ManagerSaveException {
        String[] items = value.split(";");
        for (int i = 0; i < items.length; ++i) {
            int curId = -1;
            try {
                curId = Integer.parseInt(items[i]);
            } catch (Throwable exception) {
                throw new ManagerSaveException("Ошибка парсинга id задачи в истории: " + items[i]);
            }

            if (tasks.containsKey(curId)) {
                historyManager.addTask(tasks.get(curId));
            } else if (epics.containsKey(curId)) {
                historyManager.addTask(epics.get(curId));
            } else if (subTasks.containsKey(curId)) {
                historyManager.addTask(subTasks.get(curId));
            } else {
                throw new ManagerSaveException("Неизвестный id в истории: " + curId);
            }
        }
    }
}
