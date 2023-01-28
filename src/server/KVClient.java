package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {

    private final HttpClient client = HttpClient.newHttpClient();

    private final String apiToken;

    private final String url;

    public String getApiToken() {
        return apiToken;
    }

    public KVClient(String url) throws IOException, InterruptedException {
        this.url = url;
        URI uri = URI.create(url + "register/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            apiToken = response.body();
            System.out.println("Регистрация на сервере прошла успешно, ваш токен: " + apiToken);
        } else {
            throw new IOException("Ошибка запроса при регистрации на сервере. Попорбуйте еще раз");
        }
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Данные успешно загружены на сервер");
        } else {
            throw new IOException("Ошибка запроса при загрузке данных на сервер");
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Данные с сервера успешно получены");
            return response.body();
        } else {
            throw new IOException("Ошибка запроса при получении данных с сервера");
        }
    }
}
