package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.KVClient;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager{

    private final String url;

    private Gson gson = Managers.getGson();

    private KVClient kvClient;

    public HttpTaskManager(String url) {
        super(url);
        this.url = url;
        try {
            kvClient = new KVClient(url);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static HttpTaskManager loadFromServer(String url){
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        httpTaskManager.load();
        return httpTaskManager;
    }

    @Override
    protected void save() {
        try {
            kvClient.put("task", gson.toJson(tasks));
            kvClient.put("subtask", gson.toJson(subTasks));
            kvClient.put("epic", gson.toJson(epics));
            kvClient.put("prioritizedTasks", gson.toJson(getPrioritizedTasks()
                    .stream().map(task -> task.getTaskId()).collect(Collectors.toList())));
            kvClient.put("history", gson.toJson(historyManager.getHistory()
                    .stream().map(task -> task.getTaskId()).collect(Collectors.toList())));
            kvClient.put("uniqueId", gson.toJson(uniqueIdNumber));
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    protected void load() {
        try{
            uniqueIdNumber = gson.fromJson(kvClient.load("uniqueId"), Integer.class);
            tasks = gson.fromJson(kvClient.load("task"),
                    new TypeToken<HashMap<Integer, Task>>(){}.getType());
            epics = gson.fromJson(kvClient.load("epic"),
                    new TypeToken<HashMap<Integer, Epic>>(){}.getType());
            subTasks = gson.fromJson(kvClient.load("subtask"),
                    new TypeToken<HashMap<Integer, SubTask>>(){}.getType());

            List<Integer> historyTasksIds = gson.fromJson(kvClient.load("history"),
                    new TypeToken<List<Integer>>(){}.getType());
            List<Integer> prioritizedTasksIds = gson.fromJson(kvClient.load("prioritizedTasks"),
                    new TypeToken<List<Integer>>(){}.getType());

            Collections.reverse(prioritizedTasksIds);
            for (Integer id : prioritizedTasksIds){
                if (tasks.containsKey(id)){
                    prioritizedTasks.add(tasks.get(id));
                } else if (subTasks.containsKey(id)) {
                    prioritizedTasks.add(subTasks.get(id));
                } else if (epics.containsKey(id)) {
                    prioritizedTasks.add(epics.get(id));
                }
            }
            for (Integer id : historyTasksIds){
                if (tasks.containsKey(id)){
                    historyManager.addTask(tasks.get(id));
                } else if (subTasks.containsKey(id)) {
                    historyManager.addTask(subTasks.get(id));
                } else if (epics.containsKey(id)) {
                    historyManager.addTask(epics.get(id));
                }
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
