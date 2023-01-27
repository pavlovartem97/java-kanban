package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();

    protected Map<Integer, SubTask> subTasks = new HashMap<>();

    protected Map<Integer, Epic> epics = new HashMap<>();

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    protected TreeSet<Task> prioritizedTasks =
            new TreeSet<>(
                    (Task task1, Task task2) -> {
                        if (task2.getStartTime() == null) {
                            return -1;
                        } else if (task1.getStartTime() == null) {
                            return 1;
                        }
                        return task1.getStartTime().isAfter(task2.getStartTime()) ? 1 : -1;
                    }
            );

    protected int uniqueIdNumber = 0;

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
        System.out.println("Все обычные задачи успешно удалены");
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            epic.setState(TaskState.NEW);
        }
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }
        subTasks.clear();
        System.out.println("Все подзадачи успешно удалены");
    }

    @Override
    public void removeAllEpicTasks() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(epics.get(id));
        }
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики успешно удалены");
    }

    @Override
    public SubTask getSubTask(int taskId) {
        if (subTasks.containsKey(taskId)) {
            historyManager.addTask(subTasks.get(taskId));
            return subTasks.get(taskId).clone();
        }
        System.out.println("Ошибка: получение подзадачи по Id " + taskId);
        return null;
    }

    @Override
    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.addTask(tasks.get(taskId));
            return tasks.get(taskId).clone();
        }
        System.out.println("Ошибка: получение задачи по Id " + taskId);
        return null;
    }

    @Override
    public Epic getEpic(int taskId) {
        if (epics.containsKey(taskId)) {
            historyManager.addTask(epics.get(taskId));
            return epics.get(taskId).clone();
        }
        System.out.println("Ошибка: получение эпика по Id " + taskId);
        return null;
    }

    @Override
    public int addTask(Task task) {
        if (!checkTimeIntervalIsFree(task)) {
            System.out.println("Нельзя добавть новый таск в связи с тем, что это время занято");
            return -1;
        }
        if (task.getTaskId() == -1) {
            task.setTaskId(uniqueIdNumber++);
        }
        tasks.put(task.getTaskId(), task);
        prioritizedTasks.add(task);
        System.out.println("Новый обычный таск с Id " + task.getTaskId() + " успешно добавлен");
        return task.getTaskId();
    }

    @Override
    public int addSubTask(SubTask task) {
        if (!checkTimeIntervalIsFree(task)) {
            System.out.println("Нельзя добавть новый таск в связи с тем, что это время занято");
            return -1;
        }
        if (task.getTaskId() == -1) {
            task.setTaskId(uniqueIdNumber++);
        }
        if (epics.containsKey(task.getEpicId())) {
            subTasks.put(task.getTaskId(), task);
            epics.get(task.getEpicId()).addSubTask(task.getTaskId());
            prioritizedTasks.add(task);
            updateEpicTaskState(task.getEpicId());
            System.out.println("Новая подзадача с Id " + task.getTaskId() + " успешно добавлена");
        } else {
            System.out.println("Ошибка: Добавление подзадачи: не найден эпик для которого добавляется подзадачка");
        }
        return task.getTaskId();
    }

    @Override
    public int addEpicTask(Epic task) {
        if (task.getTaskId() == -1) {
            task.setTaskId(uniqueIdNumber++);
        }
        epics.put(task.getTaskId(), task);
        System.out.println("Новый эпик с Id " + task.getTaskId() + " успешно добавлен");
        return task.getTaskId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            if (!checkTimeIntervalIsFree(task)) {
                System.out.println("Нельзя обновить таск в связи с тем, что это время занято");
                return;
            }
            prioritizedTasks.removeIf((Task task_) -> Objects.equals(task_.getTaskId(), task.getTaskId()));
            prioritizedTasks.add(task);
            tasks.put(task.getTaskId(), task);
            System.out.println("Задача с Id " + task.getTaskId() + " успешно обновлена");
        } else {
            System.out.println("Ошибка: Обновление таски: Нет задачи с таким Id " + task.getTaskId());
        }
    }

    @Override
    public void updateSubTask(SubTask task) {
        if (subTasks.containsKey(task.getTaskId())) {
            if (!checkTimeIntervalIsFree(task)) {
                System.out.println("Нельзя обновить таск в связи с тем, что это время занято");
                return;
            }
            prioritizedTasks.removeIf((Task task_) -> Objects.equals(task_.getTaskId(), task.getTaskId()));
            prioritizedTasks.add(task);
            subTasks.put(task.getTaskId(), task);
            updateEpicTaskState(task.getEpicId());
            System.out.println("Подзадача с Id " + task.getTaskId() + " успешно обновлена");
        } else {
            System.out.println("Ошибка: Обновление сабтаски: Нет задачи с таким Id " + task.getTaskId());
        }
    }

    @Override
    public void updateEpicTask(Epic task) {
        if (epics.containsKey(task.getTaskId())) {
            epics.put(task.getTaskId(), task);
            System.out.println("Эпическая задача с Id " + task.getTaskId() + " успешно обновлена");
        } else {
            System.out.println("Ошибка: Обновление эпика: Нет эпика с таким Id " + task.getTaskId());
        }
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.get(taskId));
            historyManager.remove(taskId);
            tasks.remove(taskId);
            System.out.println("Обычная задача с Id " + taskId + " успешно удалена");
        } else if (epics.containsKey(taskId)) {
            for (int subTaskId : epics.get(taskId).getAllSubTasksId()) {
                prioritizedTasks.remove(subTasks.get(subTaskId));
                historyManager.remove(subTaskId);
                subTasks.remove(subTaskId);
            }
            historyManager.remove(taskId);
            prioritizedTasks.remove(epics.get(taskId));
            epics.remove(taskId);
            System.out.println("Эпическая задача с Id " + taskId + " успешно удалена");
        } else if (subTasks.containsKey(taskId)) {
            SubTask subTask = subTasks.get(taskId);
            Integer epicId = subTask.getEpicId();
            if (!epics.containsKey(epicId)) {
                System.out.println("Ошибка: удаление подзадачи для несуществующего эпика " + taskId);
                return;
            }
            historyManager.remove(taskId);
            prioritizedTasks.remove(subTasks.get(taskId));
            epics.get(epicId).removeSubTask(taskId);
            updateEpicTaskState(epicId);
            subTasks.remove(taskId);
            System.out.println("Подзадача с Id " + taskId + " успешно удалена");
        } else {
            System.out.println("Ошибка: удаление таска по Id: не найдено таска с Id " + taskId);
        }
    }

    @Override
    public List<SubTask> getAllSubtasksInEpic(int epicTaskId) {
        List<SubTask> subTasksList = new ArrayList<>();
        if (epics.containsKey(epicTaskId)) {
            Epic epic = epics.get(epicTaskId);
            for (int subTaskId : epic.getAllSubTasksId()) {
                subTasksList.add(subTasks.get(subTaskId));
            }
        }
        return subTasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateEpicTaskState(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка: Обновление состояния эпика: эпик с Id " + epicId + " не найден");
            return;
        }
        updateEpicTime(epicId);

        Epic epic = epics.get(epicId);
        ArrayList<SubTask> subTasksInEpic = new ArrayList<>();

        for (Integer subTaskId : epics.get(epicId).getAllSubTasksId()) {
            if (subTasks.containsKey(subTaskId)) {
                subTasksInEpic.add(subTasks.get(subTaskId));
            }
        }

        updateEpicTaskState(epic, subTasksInEpic);
    }

    public boolean checkTimeIntervalIsFree(Task newTask) {
        if (newTask.getStartTime() == null) {
            return true;
        }
        boolean flag = true;

        for (Task task : prioritizedTasks) {
            if (task.getStartTime() == null) {
                continue;
            }
            if (task.getStartTime().isBefore(newTask.getStartTime())
                    && task.getEndTime().isAfter(newTask.getStartTime())) {
                flag = false;
            }
            if (newTask.getStartTime().isBefore(task.getStartTime())
                    && newTask.getEndTime().isAfter(task.getStartTime())) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public static void updateEpicTaskState(Epic epic, List<SubTask> subTasksInEpic) {
        if (subTasksInEpic.isEmpty()) {
            epic.setState(TaskState.NEW);
            return;
        }

        int newTasksCount = 0;
        int doneTasksCount = 0;

        for (Task task : subTasksInEpic) {
            switch (task.getState()) {
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
        } else if (subTasksInEpic.size() == doneTasksCount) {
            epic.setState(TaskState.DONE);
        } else {
            epic.setState(TaskState.IN_PROGRESS);
        }
    }

    public void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        Set<Integer> subtaskIds = epic.getAllSubTasksId();
        ArrayList<Task> orderedSubTask = new ArrayList<>();
        for (Task task : prioritizedTasks) {
            if (task.getStartTime() != null && subtaskIds.contains(task.getTaskId())) {
                orderedSubTask.add(task);
            }
        }
        epic.updateTimes(orderedSubTask);
    }
}
