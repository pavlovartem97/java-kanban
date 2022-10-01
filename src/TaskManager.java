import java.sql.SQLOutput;
import java.util.*;

public class TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();

    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final Map<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> taskList = new ArrayList<>();
        for (SubTask task : subTasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> taskList = new ArrayList<>();
        for (Epic task : epics.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все обычные задачи успешно удалены");
    }

    public void removeAllSubTasks() {
        for (Epic epic: epics.values()) {
            epic.removeAllSubTasks();
            epic.setState(TaskState.NEW);
        }
        subTasks.clear();
        System.out.println("Все подзадачи успешно удалены");
    }

    public void removeAllEpicTasks() {
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики успешно удалены");
    }

    public SubTask getSubTask(int taskId) {
        if (subTasks.containsKey(taskId)) {
            return subTasks.get(taskId);
        }
        System.out.println("Ошибка: получение подзадачи по Id " + taskId);
        return null;
    }

    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }
        System.out.println("Ошибка: получение задачи по Id " + taskId);
        return null;
    }

    public Epic getEpic(int taskId) {
        if (epics.containsKey(taskId)) {
            return epics.get(taskId);
        }
        System.out.println("Ошибка: получение эпика по Id " + taskId);
        return null;
    }

    public int addTask(Task task) {
        tasks.put(task.getTaskId(), task);
        System.out.println("Новый обычный таск с Id " + task.getTaskId() + " успешно добавлен");
        return task.getTaskId();
    }

    public int addSubTask(SubTask task) {
        if (epics.containsKey(task.getEpicId())){
            subTasks.put(task.getTaskId(), task);
            epics.get(task.getEpicId()).addSubTask(task.getTaskId());
            updateEpicTaskState(task.getEpicId());
            System.out.println("Новая подзадача с Id " + task.getTaskId() + " успешно добавлена");
        }
        else {
            System.out.println("Ошибка: Добавление подзадачи: не найден эпик для которого добавляется подзадачка");
        }
        return task.getTaskId();
    }

    public int addEpicTask(Epic task) {
        epics.put(task.getTaskId(), task);
        System.out.println("Новый эпик с Id " + task.getTaskId() + " успешно добавлен");
        return task.getTaskId();
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
            System.out.println("Задача с Id " + task.getTaskId() + " успешно обновлена");
        }
        else {
            System.out.println("Ошибка: Обновление таски: Нет задачи с таким Id " + task.getTaskId());
        }
    }

    public void updateSubTask(SubTask task) {
        if (subTasks.containsKey(task.getTaskId())) {
            subTasks.put(task.getTaskId(), task);
            updateEpicTaskState(task.getEpicId());
            System.out.println("Подзадача с Id " + task.getTaskId() + " успешно обновлена");
        }
        else {
            System.out.println("Ошибка: Обновление сабтаски: Нет задачи с таким Id " + task.getTaskId());
        }
    }

    public void updateEpicTask(Epic task) {
        if (epics.containsKey(task.getTaskId())) {
            epics.put(task.getTaskId(), task);
            System.out.println("Эпическая задача с Id " + task.getTaskId() + " успешно обновлена");
        }
        else{
            System.out.println("Ошибка: Обновление эпика: Нет эпика с таким Id " + task.getTaskId());
        }
    }

    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Обычная задача с Id " + taskId + " успешно удалена");
        }
        else if (epics.containsKey(taskId)) {
            for (int subTaskId : epics.get(taskId).getAllSubTasksId()){
                subTasks.remove(subTaskId);
            }
            epics.remove(taskId);
            System.out.println("Эпическая задача с Id " + taskId + " успешно удалена");
        }
        else if (subTasks.containsKey(taskId)) {
            SubTask subTask = subTasks.get(taskId);
            Integer epicId = subTask.getEpicId();
            if (!epics.containsKey(epicId)) {
                System.out.println("Ошибка: удаление подзадачи для несуществующего эпика " + taskId);
                return;
            }
            epics.get(epicId).removeSubTask(taskId);
            updateEpicTaskState(epicId);
            subTasks.remove(taskId);
            System.out.println("Подзадача с Id " + taskId + " успешно удалена");
        }
        else {
            System.out.println("Ошибка: удаление таска по Id: не найдено таска с Id " + taskId);
        }
    }

    public ArrayList<SubTask> getAllSubtasksInEpic(Epic epicTask) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (int subTaskId: epicTask.getAllSubTasksId()){
            subTasksList.add(subTasks.get(subTaskId));
        }
        return subTasksList;
    }

    public void updateEpicTaskState(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка: Обновление состояния эпика: эпик с Id " + epicId + " не найден");
            return;
        }

        Epic epic = epics.get(epicId);
        ArrayList<SubTask> subTasksInEpic = new  ArrayList<SubTask>();

        for (Integer subTaskId: epics.get(epicId).getAllSubTasksId()) {
            if (subTasks.containsKey(subTaskId)) {
                subTasksInEpic.add(subTasks.get(subTaskId));
            }
        }

        if (subTasksInEpic.isEmpty()) {
            epic.state = TaskState.NEW;
            return;
        }

        int newTasksCount = 0;
        int doneTasksCount = 0;

        for (Task task: subTasks.values()) {
            switch (task.getState()){
                case NEW:
                    newTasksCount++;
                    break;
                case DONE:
                    doneTasksCount++;
                    break;
            }
        }

        if (subTasksInEpic.size() == newTasksCount) {
            epic.setState(TaskState.NEW);
        } else if(subTasksInEpic.size() == doneTasksCount) {
            epic.setState(TaskState.DONE);
        } else {
            epic.setState(TaskState.IN_PROGRESS);
        }
    }
}
