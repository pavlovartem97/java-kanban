public class Main {

    // Почему-то обычный assert не срабатывает, поэтому написан для тестов в таком виде
    static void myAssert(boolean statement){
        if (!statement){
            throw new AssertionError();
        }
    }
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        int taskId1 = taskManager.addTask( new Task("Спорт", "Отжиматься 10 раз", TaskState.NEW) );
        int taskId2 = taskManager.addTask( new Task("Прогулка", "Погулять по парку", TaskState.DONE) );
        System.out.println(taskManager.getTasks());
        taskManager.removeTask(taskId2);
        myAssert(taskManager.getTasks().size() == 1);

        Task task = taskManager.getTask(taskId1);
        task.setTaskName("Выгулять собаку");
        task.setState(TaskState.IN_PROGRESS);
        myAssert(taskManager.getTasks().get(0).getState() == TaskState.IN_PROGRESS);
        System.out.println(taskManager.getTasks());

        int epicId = taskManager.addEpicTask( new Epic("Покупки", "Сходить в магазин за едой") );
        myAssert(taskManager.getEpic(epicId).getState() == TaskState.NEW);

        int subTaskId1 = taskManager.addSubTask(new SubTask("Список покупок", "Составить список покупок", TaskState.DONE, epicId));
        myAssert(taskManager.getEpic(epicId).getState()== TaskState.DONE);

        int subTaskId2 = taskManager.addSubTask(new SubTask("Купить продукты", "Дойти до магазина и купить продукты", TaskState.NEW, epicId));
        myAssert(taskManager.getEpic(epicId).getState() == TaskState.IN_PROGRESS);

        myAssert(subTaskId1 != subTaskId2);
        myAssert(epicId != subTaskId1);
        myAssert(taskId1 != subTaskId2);
        myAssert(taskId2 != subTaskId1);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        myAssert(taskManager.getSubTasks().equals(taskManager.getAllSubtasksInEpic(taskManager.getEpic(epicId))));

        taskManager.removeTask(subTaskId2);
        myAssert(taskManager.getEpic(epicId).getState() == TaskState.NEW);
        System.out.println(taskManager.getEpics());
        myAssert(taskManager.getAllSubtasksInEpic(taskManager.getEpic(epicId)).size() == 1);

        SubTask subTask = taskManager.getSubTask(subTaskId1);
        subTask.setTaskDescription("Купить молочные продукты");
        subTask.setState(TaskState.DONE);
        taskManager.updateSubTask(subTask);
        myAssert(taskManager.getEpic(epicId).getState() == TaskState.DONE);

        taskManager.removeAllSubTasks();
        myAssert(taskManager.getSubTasks().size() == 0);
        myAssert(taskManager.getEpic(epicId).getState() == TaskState.NEW);

        int subTaskId3 = taskManager.addSubTask(new SubTask("Купить продукты", "Дойти до магазина и купить продукты", TaskState.IN_PROGRESS, epicId));
        taskManager.removeAllEpicTasks();
        myAssert(taskManager.getSubTasks().size() == 0);

        System.out.println("Поехали!");
    }
}
