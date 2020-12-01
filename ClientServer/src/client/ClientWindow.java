package client;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame{
    //хостове імя серверу
    private static final String SERVER_HOST = "localhost";
    //порт серверу
    private static final int SERVER_PORT = 3443;
    // сокет клієнту
    private Socket clientSocket;
    //вхідні повідомлення
    private Scanner inMessage;
    //вихідні повідомлення
    private PrintWriter outMessage;

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    //назва клієнту
    private String clientName = "";
    //Getter для клієнту
    public String getClientName() {
        return this.clientName;
    }

    //консруктор але не Лєго
    public ClientWindow() {
        try {
            //зєднання з сервером
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //конфігурація вікна
        setBounds(600, 300, 600, 500);

        setTitle("Telegram XS MAX Pro Ultra 2.0 Infinite Remastered Lite");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        // мітка з кількістю користувачів
        JLabel jlNumberOfClients = new JLabel("Живих користувачів ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Відправити");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Писати в модний чат сюди: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Назвися: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);
        // події після натиску на кнопку
        jbSendMessage.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped (KeyEvent e){
                    boolean isShiftDown = e.isShiftDown();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && isShiftDown == true) {
                    sendMsg();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        jbSendMessage.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    jtfMessage.grabFocus();
                }
            }
        });


        // очистка стрічки фокусу
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // очистка імені фокусу
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
        // пусковик серверу
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // вічний цикл
                    while (true) {
                        //отримання повідомлення
                        if (inMessage.hasNext()) {
                            //зчитування
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Людей в чаті = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // вивід повідомлення
                                jtaTextAreaMessage.append(inMes);
                                // перевід на нову стрічку
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // події закриття відкна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // перевірка імені чи не є воно порожнім
                    if (!clientName.isEmpty() && clientName != "Назвися: ") {
                        outMessage.println(clientName + " покинув нас...");
                    } else {
                        outMessage.println("Він залишив нас, так і не назвавшись...");
                    }
                    // відправка службового повідомлення яке є сигналом того що клієнт вийшов з чату
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        // відобаження форми
        setVisible(true);
    }

    // відправлення повідомлень
    public void sendMsg() {
        // формування повідомлення для відправки
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // відправляємо повідомлення
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }

}

