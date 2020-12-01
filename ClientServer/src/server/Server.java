package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.out;

public class Server {
		// порт, який буде перевіряти наш сервер
    static final int PORT = 3443;
		// список клієнтів, які будуть підключатись до серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server() {
				// сокет клієнта, це такий собі потік, який буде підключатись до серверу
				// маючи адресу і порт
        Socket clientSocket = null;
				// серверний сокет
        ServerSocket serverSocket = null;
        try {
						// створюєм серверний сокет на первному порті
            serverSocket = new ServerSocket(PORT);
            out.println("Сервер пашить!");
						// запускаєм вічний цикл
            while (true) {
								// таким чином чекаєм на відгук підключення від сервера
                clientSocket = serverSocket.accept();
								// створюєм обробку клієнту, який буде підключатись до серверу
								// а this - це і є сервер
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
								// кожне підключення клієнта обробляєм через новий потік
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
								// закриваєм підключення
                clientSocket.close();
                out.println("Сервер наївся і спит!");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
		
		// відправляєм повідомлення всім клієнтам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

		// видаляєм клієнтів при виході з чату
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
