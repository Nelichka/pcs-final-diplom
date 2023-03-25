import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    static final int PORT = 8989;

    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        // System.out.println(engine.search("бизнес"));

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989
        // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен...");

            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    String request = in.readLine();
                    String response = engine.search(request).toString();
                    out.println(response);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}






