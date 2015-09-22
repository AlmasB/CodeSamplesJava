package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.server.Server;

@ServerEndpoint(value = "/game")
public class WebSocketTest {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        System.out.println("Connected ... " + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        switch (message) {
            case "quit":
                try {
                    session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Game ended"));
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println(message + " from " + session.getId());
                try {
                    session.getBasicRemote().sendText("Hi from java! Got: " + message);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return message;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }

    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        Server server = new Server("localhost", 55555, "/websockets", null, WebSocketTest.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            server.stop();
        }
    }
}
