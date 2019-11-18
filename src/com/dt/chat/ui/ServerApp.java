package com.dt.chat.ui;

import com.dt.chat.custom.NumberTextFiled;
import com.dt.chat.custom.SimpleTextPane;
import com.dt.chat.model.Message;
import com.dt.chat.model.User;
import com.dt.chat.ui.theme.Theme;
import com.dt.chat.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description: ������������ 
 * @author �����ܣ����
 * @date 2019/1/2
 */
public class ServerApp {
    private ServerSocket server;//������socket
    private boolean stop = true;//�������Ƿ�ֹͣ�ı�ʶ��

    private JFrame frame;
    private JTextField maxTextField;//����������ı���
    private JTextField portTextField;//�˿��ı���
    private JTextField roomNameTextField;//�����������ı���
    private JTextField msgTextField;//��Ϣ�����ı���
    private SimpleTextPane textPane;//��Ϣ��ʾ�ı����
    private JList userList;//�û��б�
    private List<MessageTask> clients = new ArrayList<>();//���߿ͻ����̼߳���

    /**
     * �����߳�
     */
    public static void main(String[] args) {
        SwingUtils.setTheme(Theme.WINDOWS);


        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerApp window = new ServerApp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * �����߳�
     */
    public ServerApp() {
        initialize();
    }

    /**
     * �ı䴰��
     */
    private void initialize() {
        final String currentTheme = UIManager.getLookAndFeel().getClass().getName();
        frame = new JFrame();
        frame.setTitle("������");
        frame.setBounds(100, 100, 640, 480);
        frame.setMinimumSize(new Dimension(590, 440));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel northPanel = new JPanel();
        northPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                "\u670D\u52A1\u5668\u4FE1\u606F", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
        FlowLayout flowLayout = (FlowLayout) northPanel.getLayout();
        frame.getContentPane().add(northPanel, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("\u4EBA\u6570\u4E0A\u9650");
        northPanel.add(lblNewLabel);

        maxTextField = new NumberTextFiled();
        maxTextField.setColumns((int) (6 * SwingUtils.getThemeRate(currentTheme)));
        maxTextField.setText("30");
        northPanel.add(maxTextField);

        JLabel lblNewLabel_1 = new JLabel("\u7AEF\u53E3");
        northPanel.add(lblNewLabel_1);

        portTextField = new NumberTextFiled();
        portTextField.setColumns((int) (6 * SwingUtils.getThemeRate(currentTheme)));
        portTextField.setText("4080");
        northPanel.add(portTextField);

        JLabel lblNewLabel_2 = new JLabel("\u804A\u5929\u5BA4\u540D\u79F0");
        northPanel.add(lblNewLabel_2);

        roomNameTextField = new JTextField();
        roomNameTextField.setColumns((int) (16 * SwingUtils.getThemeRate(currentTheme)));
        roomNameTextField.setText("����ѧԺ��֮��");
        northPanel.add(roomNameTextField);

        final JButton startButton = new JButton("\u542F\u52A8");
        // ������ť�¼�
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ����socket�������������̳߳ع���ͻ��˵�������Ϣ
                if (stop == true) {
                    stop = false;
                    startServer(Integer.parseInt(portTextField.getText()), Integer.parseInt(maxTextField.getText()));
                    startButton.setEnabled(false);
                }
            }
        });
        northPanel.add(startButton);

        JButton stopButton = new JButton("\u505C\u6B62");
        // ֹͣ��ť�¼�
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (stop == false) {
                    stop = true;
                    try {
                        server.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    startButton.setEnabled(true);
                }
            }
        });
        northPanel.add(stopButton);

        JPanel southPanel = new JPanel();
        southPanel.setBorder(
                new TitledBorder(null, "\u529F\u80FD\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
        frame.getContentPane().add(southPanel, BorderLayout.SOUTH);

        msgTextField = new JTextField();
        southPanel.add(msgTextField);
        msgTextField.setColumns((int) (75 * SwingUtils.getThemeRate(currentTheme)));

        JButton sendButton = new JButton("\u53D1\u9001");
        //������Ϣ��ť�¼�
        AbstractAction sendAction=new AbstractAction() {
            Random rd = new Random();
            Color[] colors = {null, Color.RED, Color.BLUE, Color.MAGENTA};

            public void actionPerformed(ActionEvent e) {
                String content = "�������㲥��" + msgTextField.getText();
                textPane.append(content + "\n", colors[rd.nextInt(4)]);
                dispathMessage(new Message(content), null);
                msgTextField.setText("");
            }
        };
        //Ϊ������Ϣ��ťע���������ͬʱ���ÿ�ݼ�Ϊenter���س�����
        SwingUtils.registerEventHandler(sendButton,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),sendAction);

        southPanel.add(sendButton);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(120);
        splitPane.setOneTouchExpandable(true);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(new TitledBorder(null, "\u7528\u6237\u5217\u8868", TitledBorder.LEADING,
                TitledBorder.TOP, null, Color.BLUE));
        splitPane.setLeftComponent(scrollPane);

        userList = new JList(new DefaultListModel());
//        userList.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() >= 2) {
//                    Object username = userList.getSelectedValue();
//                    msgTextField.setText("@" + username + " ");
//                }
//            }
//        });
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(userList);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setViewportBorder(
                new TitledBorder(null, "\u804A\u5929\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
        splitPane.setRightComponent(scrollPane_1);

        textPane = new SimpleTextPane();
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);//ָ��JTextPane������ĸ��²��ԣ��ù�����ʼ����ʾ�����·�
        scrollPane_1.setViewportView(textPane);
    }

    /**
     * ������ת����Ϣ
     * @param message ��Ϣ����
     * @param org ��Ϣ������
     */
    private void dispathMessage(Message message, User org) {
        for (MessageTask clientTask : clients) {
            if (clientTask.getUser() == org) {//��ת�����Լ�
                continue;
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(clientTask.getConnection().getOutputStream());
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * ����������
     * @param port
     * @param max
     */
    protected void startServer(final int port, final int max) {
    	// �����̳߳أ�����߳�ʹ�õ�Ч��
        final ExecutorService pool = Executors.newFixedThreadPool(max);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
            	// �������������������㷨
                try {
                	// 1�������˿�����������
                    server = new ServerSocket(port);
                    // 2��ѭ���ȴ��ͻ�������
                    while (!stop) {
                        Socket connection = server.accept();
                        // 3������ʱ���������û���Ϣ���͸��ͻ���
                        ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
                        oos.writeObject(new Message(Message.USER_LIST, convert2List(((DefaultListModel)userList.getModel()).elements())));
                        oos.flush();

                        // 4��Ϊ�����ߵĿͻ��˴�����Ϣ�����߳�
                        MessageTask task = new MessageTask(connection);
                        // 5�����߳��ύ���̳߳�ִ��
                        pool.submit(task);
                        // 6������ͻ����̵߳������У�����ת����Ϣʱʹ��
                        clients.add(task);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                pool.shutdown();
                textPane.append("�������رճɹ�...\n", Color.BLUE);
            }

        }.execute();
        textPane.append("�����������ɹ�...\n", Color.BLUE);
    }

    /**
     * ��ȡ��ǰ�����û������浽������
     * @param elements
     * @return
     */
    private List<User> convert2List(Enumeration elements) {
        List<User> users = new ArrayList<>();
        while (elements.hasMoreElements()) {
            users.add((User) elements.nextElement());
        }
        return users;
    }

    /**
     * description: �ڲ��ࡣΪÿ�����ߵĿͻ��˵�������һ���̣߳����ղ�����ӿͻ��˷���������Ϣ��
     * @author saga.chen created at 2018��1��13�� 
     * @version 1.0
     */
    class MessageTask implements Callable<Void> {
        private Socket connection;//��ǰ�ͻ���
        private User user;//��ǰ�û�

        public MessageTask(Socket connection) {
            this.connection = connection;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Void call() throws Exception {
        	// ���տͻ�����Ϣ���㷨���£�
            while (true) {
            	// 1����������
                ObjectInputStream reader = new ObjectInputStream(connection.getInputStream());
                // 2�����������ж�ȡ�ͻ��˴���������Ϣ����
                Message msg = (Message) reader.readObject();
                // 3��������Ϣ����������ȡ��ͬ�Ĵ���ʽ���ɸ���switchʵ�֡�
                if (msg.getCommand() == Message.ADD) {
                    user = (User) msg.getContent();//����ʱ�ͻ��˷���������Ϣ������User�������Խ�����ʱ��Ҳ��ԭ��User����
                    dispathMessage(new Message(Message.ADD,user), user);
                    textPane.append(user.getUserName(),Color.MAGENTA).append("������...").newLine();
                    addUser2List(user);
                } else if (msg.getCommand() == Message.SEND_ALL) {
                    // ��֯ת����Ϣ������
                    String dispathMessage = user.getUserName() + " ˵��" + msg.getContent().toString();
                    dispathMessage(new Message(dispathMessage),user);
                    textPane.append(user.getUserName(),true).append(" ˵��").append(msg.getContent().toString()).newLine();
                } else if (msg.getCommand() == Message.CLOSE) {
                    dispathMessage(new Message(Message.DELETE,user), user);
                    clients.remove(connection);
                    reader.close();
                    connection.close();
                    textPane.append(user.getUserName(), true).append(" ������...").newLine();
                    deleteUserFromList(user);
                } else if (msg.getCommand() == Message.SEND_ONE) {
                    msg.setFrom(user.getUserName());
                    dispathMessage(msg, user);
                    textPane.append(user.getUserName()).append(" �� ").append(msg.getTo()).append(" ˵��");
                    textPane.append(msg.getContent().toString()).newLine();
                }
            }
        }

        public Socket getConnection() {
            return connection;
        }


        public User getUser() {
            return user;
        }

    }

    private void addUser2List(User user) {
        ((DefaultListModel)userList.getModel()).addElement(user);
    }

    private void deleteUserFromList(User user) {
        ((DefaultListModel) userList.getModel()).removeElement(user);
    }
}
