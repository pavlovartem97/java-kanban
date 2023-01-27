package tests;

import manager.Managers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;

public class HttpTaskServerTest {

    private static final String KVServerurl = Managers.getDefaultUrl();

    private static final String httpTaskServerUrl = "http://localhost:8078/";

    private static KVServer kvServer;

    private static HttpTaskServer httpTaskServer;

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterAll
    public static void afterAll(){
        kvServer.stop();
        httpTaskServer.stop();
    }
}
