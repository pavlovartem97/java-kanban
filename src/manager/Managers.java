package manager;

import java.io.File;

public class Managers {

    static public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    static public FileBackedTasksManager getFileBackendManager(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }

    ;
}
