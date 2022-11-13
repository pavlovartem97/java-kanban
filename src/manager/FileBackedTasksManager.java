package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.*;
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

    static FileBackedTasksManager loadFromFile(File file) {
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

    private void save() {
        try {
            try (FileWriter fileWriter = new FileWriter(file);) {
                BufferedWriter buffer = new BufferedWriter(fileWriter);
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
                buffer.close();
            } catch (IOException error) {
                throw new ManagerSaveException("неправильная работа с файлом");
            }
        } catch (ManagerSaveException exception) {
            System.out.println("Ошибка записи: " + exception.getMessage());
        }
    }

    private void load() {
        try {
            try (FileReader fileReader = new FileReader(file);) {
                BufferedReader buffer = new BufferedReader(fileReader);
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
        if (items.length != 6) {
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

        switch (taskKey) {
            case TASK_KEY:
                Task task = new Task(taskName, taskDescription, taskState);
                task.setTaskId(taskId);
                super.addTask(task);
                break;
            case EPIC_KEY:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setTaskId(taskId);
                super.addEpicTask(epic);
                break;
            case SUBTASK_KEY:
                int epicId = Integer.parseInt(items[5]);
                SubTask subTask = new SubTask(taskName, taskDescription, taskState, epicId);
                subTask.setTaskId(taskId);
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