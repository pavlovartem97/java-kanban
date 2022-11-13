import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.File;

public class Main {

    // Почему-то обычный assert не срабатывает, поэтому написан для тестов в таком виде
    static void myAssert(boolean statement) {
        if (!statement) {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) {

        File file = new File("C:/Users/Artem/java-kanban/data.txt");
        TaskManager taskManager = new FileBackedTasksManager(file);

        int epicId = taskManager.addEpicTask(new Epic("Покупки", "Сходить в магазин за едой"));
        int subTaskId1 = taskManager.addSubTask(new SubTask("Список покупок", "Составить список покупок", TaskState.DONE, epicId));
        int subTaskId2 = taskManager.addSubTask(new SubTask("Купить продукты", "Дойти до магазина и купить продукты", TaskState.NEW, epicId));
        int taskId = taskManager.addTask(new Task("Помыть посуду", "", TaskState.IN_PROGRESS));

        taskManager.getSubTask(subTaskId2);
        taskManager.getTask(taskId);
        taskManager.getSubTask(subTaskId1);
        taskManager.removeTask(subTaskId2);

        System.out.println("Исходный список задач");
        System.out.println("История тасков: " + taskManager.getHistory());
        System.out.println("Эпики:" + taskManager.getEpics());
        System.out.println("Сабтаски:" + taskManager.getSubTasks());
        System.out.println("Таски:" + taskManager.getTasks());

        TaskManager newTaskManager = Managers.getFileBackendManager(file);
        System.out.println("Список задача загруженный из newTaskManager");
        System.out.println("История тасков: " + newTaskManager.getHistory());
        System.out.println("Эпики:" + newTaskManager.getEpics());
        System.out.println("Сабтаски:" + newTaskManager.getSubTasks());
        System.out.println("Таски:" + newTaskManager.getTasks());

        myAssert(taskManager.getHistory().toString().equals(newTaskManager.getHistory().toString()));
        myAssert(taskManager.getEpics().toString().equals(newTaskManager.getEpics().toString()));
        myAssert(taskManager.getSubTasks().toString().equals(newTaskManager.getSubTasks().toString()));
        myAssert(taskManager.getTasks().toString().equals(newTaskManager.getTasks().toString()));

        int newEpicid = newTaskManager.addEpicTask(new Epic("Английский", "Сделать домашнее задание"));
        int newsubTaskId1 = newTaskManager.addSubTask(new SubTask("Прослушать аудио", "", TaskState.DONE, newEpicid));
        int newsubTaskId2 = newTaskManager.addSubTask(new SubTask("Сделать упражнение", "-", TaskState.NEW, newEpicid));
        int newsubTaskId3 = newTaskManager.addSubTask(new SubTask("Повторить слова", "-", TaskState.IN_PROGRESS, newEpicid));

        newTaskManager.getEpic(newEpicid);
        newTaskManager.getSubTask(newsubTaskId2);
        newTaskManager.removeTask(epicId);
        newTaskManager.removeTask(newsubTaskId1);

        TaskManager newTaskManager2 = Managers.getFileBackendManager(file);

        myAssert(newTaskManager2.getHistory().toString().equals(newTaskManager.getHistory().toString()));
        myAssert(newTaskManager2.getEpics().toString().equals(newTaskManager.getEpics().toString()));
        myAssert(newTaskManager2.getSubTasks().toString().equals(newTaskManager.getSubTasks().toString()));
        myAssert(newTaskManager2.getTasks().toString().equals(newTaskManager.getTasks().toString()));
    }
}
