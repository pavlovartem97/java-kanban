package manager;

import com.google.gson.Gson;

public class Managers {

    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    private static final String url = "http://localhost:8078/";

    public static String getDefaultUrl() {
        return url;
    }

    static public TaskManager getDefault() {
        return new HttpTaskManager(url);
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    static public HttpTaskManager loadTaskManagerFromServer(String url) {
        return HttpTaskManager.loadFromServer(url);
    }

    static public FileBackedTasksManager getFileBackendManager(String fileName) {
        return FileBackedTasksManager.loadFromFile(fileName);
    }
}
