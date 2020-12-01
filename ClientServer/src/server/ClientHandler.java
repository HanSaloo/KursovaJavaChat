package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {
		
		// екземпляр сервера
    private Server server;
		// вихідні повідомлення
    private PrintWriter outMessage;
		// вхідні повідомлення
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
		// клієнт сокет
    private Socket clientSocket = null;
		// кількість клієнтів в чаті, статичне поле
    private static int clients_count = 0;

		// конструктор, який приймає клієнт сокет і сервер
    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
		// перегрузка методу run(), який викликається коли
		// викликається new Thread(client).start();
    @Override
    public void run() {
        try {
            while (true) {
								// сервер відправляє повідомлення
                server.sendMessageToAllClients("Новий участник зайшов в чат!");
                server.sendMessageToAllClients("Клієнтів в чаті = " + clients_count);
                break;
            }

            while (true) {
                // Якщо від клієнта прийшло повідомлення
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
										// якщо клієнт відправляє це повідомлення, то цикл переривається і
										// клієнт виходить з чату
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
										// виводимо в консоль повідомлення (тестове)
                    System.out.println(clientMessage);
										// відправляєм повідомлення всім клієнтам
                    server.sendMessageToAllClients(clientMessage);
                }
								// зупиняєм потік на на 110 мс
                Thread.sleep(110);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
		// відправляєм повідомлення
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
		// клієнт виходить з чату
    public void close() {
				// видаляєм клієнта зі списку
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клієнтів в чаті = " + clients_count);
    }
}
