package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // Наиболее эффективный контейнер для вставки в конце и удаления из начала
    private final LinkedList<Task> tasks = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        tasks.addLast(task);
        if (tasks.size() > 10){
            tasks.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasks;
    }
}
