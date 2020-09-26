import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

class ClientData {
    String name;
    public BufferedReader reader;
    public PrintWriter writer;

    public ClientData(String name, BufferedReader reader, PrintWriter writer) {
        this.name = name;
        this.reader = reader;
        this.writer = writer;
    }
}


public class Server {
    static int port;
    public static HashMap<String, ClientData> clients = new HashMap<>();

    public static void main(String[] args) {
        if(args.length >= 1){
            port = Integer.parseInt(args[1]);
        }
        listenSocket();
    }

    static void listenSocket() {
        try {
            if(port == 0){
                port = 6666;
            }
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    Socket socket;
    String name;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            writer.println("Enter your name please");
            String name = reader.readLine();
            this.name = name;
            System.out.println(name + " is connected");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("users");
            for (ClientData c :
                    Server.clients.values()) {
                c.writer.println("newuser "+name);
                stringBuilder.append(" ").append(c.name);
            }
            writer.println(stringBuilder.toString());
            Server.clients.put(name.trim(), new ClientData(name.trim(), reader, writer));
            chat();
        } catch (Exception e) {

        }
    }

    void chat() {
        BufferedReader reader = null;
        PrintWriter writer;
        try {
            reader = Server.clients.get(this.name).reader;

            String message;
            while (!(message = reader.readLine()).equals("close")) {
                String[] messageArray = message.split(" ");
                String to = messageArray[0];
                System.out.println(to);
                if(!Server.clients.containsKey(to)){
                    continue;
                }
                StringBuilder realMessage = new StringBuilder();
                for (int i = 1; i < messageArray.length; i++) {
                    realMessage.append(messageArray[i]).append(" ");
                }
                ClientData clientData1 = Server.clients.get(to.trim());
                clientData1.writer.println(this.name + " " + realMessage.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}