import manager.HttpTaskManager;
import manager.Managers;
import manager.TaskManager;
import server.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.IOException;

public class Main {

    static void myAssert(boolean statement) {
        if (!statement) {
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager taskManager = Managers.getDefault();

        int epicId = taskManager.addEpicTask(new Epic("Покупки", "Сходить в магазин за едой"));
        int subTaskId1 = taskManager.addSubTask(
                new SubTask("Список покупок", "Составить список покупок", TaskState.DONE, epicId));
        int subTaskId2 = taskManager.addSubTask(
                new SubTask("Купить продукты", "Дойти до магазина и купить продукты", TaskState.NEW, epicId));
        int taskId = taskManager.addTask(new Task("Помыть посуду", "", TaskState.IN_PROGRESS));

        taskManager.getSubTask(subTaskId2);
        taskManager.getTask(taskId);
        taskManager.getSubTask(subTaskId1);
        taskManager.removeTask(subTaskId2);

        HttpTaskManager newTaskManager = Managers.loadTaskManagerFromServer(Managers.getDefaultUrl());

        System.out.println("Исходный список задач");
        System.out.println("История тасков: " + taskManager.getHistory());
        System.out.println("Эпики:" + taskManager.getEpics());
        System.out.println("Сабтаски:" + taskManager.getSubTasks());
        System.out.println("Таски:" + taskManager.getTasks());

        System.out.println("Список задача загруженный из newTaskManager");
        System.out.println("История тасков: " + newTaskManager.getHistory());
        System.out.println("Эпики:" + newTaskManager.getEpics());
        System.out.println("Сабтаски:" + newTaskManager.getSubTasks());
        System.out.println("Таски:" + newTaskManager.getTasks());

        myAssert(taskManager.getHistory().toString().equals(newTaskManager.getHistory().toString()));
        myAssert(taskManager.getEpics().toString().equals(newTaskManager.getEpics().toString()));
        myAssert(taskManager.getSubTasks().toString().equals(newTaskManager.getSubTasks().toString()));
        myAssert(taskManager.getTasks().toString().equals(newTaskManager.getTasks().toString()));

        int newEpic = newTaskManager.addEpicTask(new Epic("Английский", "Сделать домашнее задание"));
        int newsubTask = newTaskManager.addSubTask(new SubTask("Прослушать аудио", "", TaskState.DONE, newEpic));
        int newsub = newTaskManager.addSubTask(new SubTask("Сделать упражнение", "-", TaskState.NEW, newEpic));

        newTaskManager.getEpic(newEpic);
        newTaskManager.getSubTask(newsub);
        newTaskManager.removeTask(epicId);
        newTaskManager.removeTask(newsubTask);

        TaskManager newTaskManager2 = Managers.loadTaskManagerFromServer(Managers.getDefaultUrl());
        myAssert(newTaskManager2.getHistory().toString().equals(newTaskManager.getHistory().toString()));
        myAssert(newTaskManager2.getEpics().toString().equals(newTaskManager.getEpics().toString()));
        myAssert(newTaskManager2.getSubTasks().toString().equals(newTaskManager.getSubTasks().toString()));
        myAssert(newTaskManager2.getTasks().toString().equals(newTaskManager.getTasks().toString()));

        kvServer.stop();
    }
}
