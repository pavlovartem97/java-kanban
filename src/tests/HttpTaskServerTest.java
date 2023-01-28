package tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = Managers.getGson();
    private KVServer kvServer;

    private HttpTaskServer httpTaskServer;

    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    private static int epicId;

    private static int subTaskId1;

    private static int subTaskId2;

    private static int taskId;

    private static final LocalDateTime startDate = LocalDateTime.of(
            LocalDate.of(2023, 1, 1), LocalTime.of(10, 0));

    private static void initializeTaskManager() {
        epicId = taskManager.addEpicTask(new Epic("Epic task", "Epic task description"));
        subTaskId1 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );
        subTaskId2 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );

        Task task = new Task("Common Task 1", "", TaskState.IN_PROGRESS);
        task.setStartTime(startDate);
        task.setDuration(90);
        taskId = taskManager.addTask(task);
    }

    private void postEpic(Epic task) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void postSubTask(SubTask task) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void postTask(Task task) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void postTasks() throws IOException, InterruptedException {
        postEpic(taskManager.getEpic(epicId));
        postSubTask(taskManager.getSubTask(subTaskId1));
        postSubTask(taskManager.getSubTask(subTaskId2));
        postTask(taskManager.getTask(taskId));
    }

    private List<Task> getHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
    }

    private List<Task> getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
    }

    @BeforeAll
    public static void beforeAll() {
        initializeTaskManager();
    }

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        postTasks();
    }

    @AfterEach
    public void afterAll() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    private List<Task> getAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
    }

    private List<SubTask> getAllSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(),
                new TypeToken<List<SubTask>>() {
                }.getType());
    }

    private List<Epic> getAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(),
                new TypeToken<List<Epic>>() {
                }.getType());
    }

    private Task getTaskById(int id) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(), Task.class);
    }

    private SubTask getSubTaskById(int id) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(), SubTask.class);
    }

    private Epic getEpicById(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);

        return gson.fromJson(response.body(), Epic.class);
    }

    private void deleteEpicById(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void deleteSubTaskById(int id) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void deleteTaskById(int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void deleteAllSubTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    private List<SubTask> getEpicSubtasks(int epicId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        return gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getAllTasks(), taskManager.getTasks());
    }

    @Test
    public void getAllSubtasksTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getAllSubtask(), taskManager.getSubTasks());
        Assertions.assertEquals(getAllSubtask().size(), 2);
    }

    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getAllEpics(), taskManager.getEpics());
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getTaskById(taskId), taskManager.getTask(taskId));
    }

    @Test
    public void getSubTaskByIdTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getSubTaskById(subTaskId1), taskManager.getSubTask(subTaskId1));
        Assertions.assertEquals(getSubTaskById(subTaskId2), taskManager.getSubTask(subTaskId2));
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getEpicById(epicId), taskManager.getEpic(epicId));
    }

    @Test
    public void deleteAllSubtasksTest() throws IOException, InterruptedException {
        deleteAllSubTasks();
        Assertions.assertTrue(getAllSubtask().isEmpty());
    }

    @Test
    public void deleteAllTasksTest() throws IOException, InterruptedException {
        deleteAllTasks();
        Assertions.assertTrue(getAllTasks().isEmpty());
    }

    @Test
    public void deleteAllEpicsTest() throws IOException, InterruptedException {
        deleteAllEpic();
        Assertions.assertTrue(getAllEpics().isEmpty());
        Assertions.assertTrue(getAllSubtask().isEmpty());
    }

    private void deleteAllEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Assertions.assertFalse(getAllTasks().isEmpty());
        deleteTaskById(taskId);
        Assertions.assertTrue(getAllTasks().isEmpty());
    }

    @Test
    public void deleteAllSubtaskByIdTest() throws IOException, InterruptedException {
        Assertions.assertEquals(getAllSubtask().size(), 2);
        deleteSubTaskById(subTaskId1);
        Assertions.assertEquals(getAllSubtask().size(), 1);
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        Assertions.assertFalse(getAllEpics().isEmpty());
        Assertions.assertFalse(getAllSubtask().isEmpty());
        deleteEpicById(epicId);
        Assertions.assertTrue(getAllEpics().isEmpty());
        Assertions.assertTrue(getAllSubtask().isEmpty());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = taskManager.getTask(taskId);
        task.setTaskName("New task description");
        task.setState(TaskState.DONE);

        postTask(task);
        Task taskFromServer = getTaskById(taskId);
        Assertions.assertEquals(task, taskFromServer);
        Assertions.assertEquals(getAllTasks().size(), 1);
    }

    @Test
    public void updateSubTaskTest() throws IOException, InterruptedException {
        SubTask task = taskManager.getSubTask(subTaskId1);
        task.setTaskName("New task description");
        task.setState(TaskState.DONE);

        postSubTask(task);
        SubTask taskFromServer = getSubTaskById(subTaskId1);
        Assertions.assertEquals(task, taskFromServer);
        Assertions.assertEquals(getAllSubtask().size(), 2);
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        Epic task = taskManager.getEpic(epicId);
        task.setTaskName("New task description");

        postEpic(task);
        Epic taskFromServer = getEpicById(epicId);
        Assertions.assertEquals(task, taskFromServer);
        Assertions.assertEquals(getAllEpics().size(), 1);

        deleteSubTaskById(subTaskId1);
        SubTask subtask = getSubTaskById(subTaskId2);
        subtask.setState(TaskState.DONE);
        postSubTask(subtask);
        Assertions.assertEquals(getEpicById(epicId).getState(), TaskState.DONE);
        Assertions.assertEquals(getEpicById(epicId).getAllSubTasksId().size(), 1);
    }

    @Test
    public void getEpicSubTasksTest() throws IOException, InterruptedException {
        List<SubTask> subTasksInEpic = getEpicSubtasks(epicId);
        Assertions.assertEquals(subTasksInEpic, taskManager.getAllSubtasksInEpic(epicId));
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        getTaskById(taskId);
        getSubTaskById(subTaskId1);
        List<Task> tasks = getHistory();
        Assertions.assertEquals(tasks.size(), 2);
        Assertions.assertEquals(tasks.get(0).getTaskId(), taskId);
        Assertions.assertEquals(tasks.get(1).getTaskId(), subTaskId1);

        getSubTaskById(subTaskId2);
        getEpicById(epicId);
        tasks = getHistory();
        Assertions.assertEquals(tasks.size(), 4);
        Assertions.assertEquals(tasks.get(2).getTaskId(), subTaskId2);
        Assertions.assertEquals(tasks.get(3).getTaskId(), epicId);
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        List<Task> tasks = getPrioritizedTasks();
        Assertions.assertEquals(tasks.size(), 3);
        Assertions.assertEquals(tasks.get(0), taskManager.getPrioritizedTasks().get(0));
        Assertions.assertEquals(tasks.get(1).getTaskId(), taskManager.getPrioritizedTasks().get(1).getTaskId());
        Assertions.assertEquals(tasks.get(2).getTaskId(), taskManager.getPrioritizedTasks().get(2).getTaskId());
    }
}
