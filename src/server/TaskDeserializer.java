package server;

import com.google.gson.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private static final Gson gson = new Gson();
    private final Map<String, Class<? extends Task>> taskTypeRegistry = new HashMap<>();

    public TaskDeserializer() {
        taskTypeRegistry.put("SUBTASK", SubTask.class);
        taskTypeRegistry.put("EPIC", Epic.class);
        taskTypeRegistry.put("TASK", Task.class);
    }

    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject taskObject = json.getAsJsonObject();
        String taskElementName = "type";
        JsonElement taskTypeElement = taskObject.get(taskElementName);

        Class<? extends Task> animalType = taskTypeRegistry.get(taskTypeElement.getAsString());
        return gson.fromJson(taskObject, animalType);
    }
}