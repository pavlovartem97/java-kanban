package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskState;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private static final String fileName = "data.csv";

    private static final Gson gson = new Gson();

    private final TaskManager taskManager = Managers.getDefault();

    private final HttpServer server;

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void initTasks() {
        int epicId = taskManager.addEpicTask(new Epic("Epic task", "Epic task description"));
        int subTaskId1 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );
        int subTaskId2 = taskManager.addSubTask(
                new SubTask("Sub task 1", "Task description", TaskState.NEW, epicId)
        );

        LocalDateTime startDate = LocalDateTime.of(
                LocalDate.of(2023, 1, 1), LocalTime.of(10, 00));

        Task task = new Task("Common Task 1", "", TaskState.IN_PROGRESS);
        task.setStartTime(startDate);
        task.setDuration(90);
        int taskId = taskManager.addTask(task);
    }

    public HttpTaskServer() throws IOException {
        initTasks();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::taskHandler);
        server.createContext("/tasks/subtask", this::subTaskHandler);
        server.createContext("/tasks/epic", this::epicHandler);
        server.createContext("/tasks", this::prioritizedTaskHandler);
        server.createContext("/tasks/history", this::historyHandler);
    }

    private void historyHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/tasks/history/$", path)) {
                    String response = gson.toJson(taskManager.getHistory());
                    sendText(httpExchange, response);
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Сервер не может обработать запрос " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void prioritizedTaskHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/tasks/$", path)) {
                    String response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(httpExchange, response);
                } else {
                    httpExchange.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("Сервер не может обработать запрос " + requestMethod);
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void epicHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        String response = gson.toJson(taskManager.getEpics());
                        sendText(httpExchange, response);
                        return;
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            String response = gson.toJson(taskManager.getEpic(id.get()));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.addEpicTask(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        taskManager.removeAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            taskManager.removeTask(id.get());
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Сервер не может обработать запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void subTaskHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String response = gson.toJson(taskManager.getSubTasks());
                        sendText(httpExchange, response);
                        return;
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            String response = gson.toJson(taskManager.getSubTask(id.get()));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("^/tasks/subtask/epic/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            String response = gson.toJson(taskManager.getAllSubtasksInEpic(id.get()));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        SubTask task = gson.fromJson(body, SubTask.class);
                        taskManager.addSubTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        taskManager.removeAllSubTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            taskManager.removeTask(id.get());
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Сервер не может обработать запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void taskHandler(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        String response = gson.toJson(taskManager.getTasks());
                        sendText(httpExchange, response);
                        return;
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            String response = gson.toJson(taskManager.getTask(id.get()));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        taskManager.removeAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/\\?id=", "");
                        Optional<Integer> id = getIdFromString(pathId);
                        if (id.isPresent()) {
                            taskManager.removeTask(id.get());
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        } else {
                            System.out.println("Получен некорректный идентификатор " + pathId);
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Сервер не может обработать запрос " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private Optional<Integer> getIdFromString(String strId) {
        try {
            return Optional.of(Integer.parseInt(strId));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
