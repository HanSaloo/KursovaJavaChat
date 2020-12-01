package client;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {
    //server hostname
    private static final String SERVER_HOST = "localhost";
    //server port
    private static final int SERVER_PORT = 3443;
    // clients socket
    private Socket clientSocket;
    //Inputed message
    private Scanner inMessage;
    //Outputed message
    private PrintWriter outMessage;

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    //Client name
    private String clientName = "";
    //Getter for client
    public String getClientName() {
        return this.clientName;
    }

    //Constructor
    public ClientWindow() {
        try {
            //connecting to server
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //form config
        setBounds(600, 300, 600, 500);
        setTitle("Client like Telegram");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        // label, count users in chat
        JLabel jlNumberOfClients = new JLabel("Users online ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Sender");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Put here your message: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Put here your nickname: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);
        // event after press button
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if client name, and messages not empty,go to sent
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // text line focus
                    jtfMessage.grabFocus();
                }
            }
        });
        // focus clear event line
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // focus clear name line
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
        // server starter
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // infinity cycle
                    while (true) {
                        //re we get inputed messages
                        if (inMessage.hasNext()) {
                            //reader
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Users in chat = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // out message
                                jtaTextAreaMessage.append(inMes);
                                // add transition on new line
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // event for closing client window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // check not empty name and name not default
                    if (!clientName.isEmpty() && clientName != "Put your name: ") {
                        outMessage.println(clientName + " exit from chat!");
                    } else {
                        outMessage.println("The participant left the chat without introducing himself!");
                    }
                    // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        // visualise form
        setVisible(true);
    }

    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }
}

