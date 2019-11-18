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
 * description: 服务器主程序。 
 * @author 唐世杰，彭浩
 * @date 2019/1/2
 */
public class ServerApp {
    private ServerSocket server;//服务器socket
    private boolean stop = true;//服务器是否停止的标识符

    private JFrame frame;
    private JTextField maxTextField;//最大连接数文本框
    private JTextField portTextField;//端口文本框
    private JTextField roomNameTextField;//聊天室名称文本框
    private JTextField msgTextField;//消息发送文本框
    private SimpleTextPane textPane;//消息显示文本面板
    private JList userList;//用户列表
    private List<MessageTask> clients = new ArrayList<>();//在线客户端线程集合

    /**
     * 建立线程
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
     * 启动线程
     */
    public ServerApp() {
        initialize();
    }

    /**
     * 改变窗口
     */
    private void initialize() {
        final String currentTheme = UIManager.getLookAndFeel().getClass().getName();
        frame = new JFrame();
        frame.setTitle("服务器");
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
        roomNameTextField.setText("工程学院梦之队");
        northPanel.add(roomNameTextField);

        final JButton startButton = new JButton("\u542F\u52A8");
        // 启动按钮事件
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 启动socket服务器，创建线程池管理客户端的连接信息
                if (stop == true) {
                    stop = false;
                    startServer(Integer.parseInt(portTextField.getText()), Integer.parseInt(maxTextField.getText()));
                    startButton.setEnabled(false);
                }
            }
        });
        northPanel.add(startButton);

        JButton stopButton = new JButton("\u505C\u6B62");
        // 停止按钮事件
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
        //发送消息按钮事件
        AbstractAction sendAction=new AbstractAction() {
            Random rd = new Random();
            Color[] colors = {null, Color.RED, Color.BLUE, Color.MAGENTA};

            public void actionPerformed(ActionEvent e) {
                String content = "服务器广播：" + msgTextField.getText();
                textPane.append(content + "\n", colors[rd.nextInt(4)]);
                dispathMessage(new Message(content), null);
                msgTextField.setText("");
            }
        };
        //为发送消息按钮注册监听器，同时设置快捷键为enter（回车键）
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
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);//指定JTextPane插入符的更新策略，让滚动条始终显示在最下方
        scrollPane_1.setViewportView(textPane);
    }

    /**
     * 服务器转发消息
     * @param message 消息内容
     * @param org 消息发送者
     */
    private void dispathMessage(Message message, User org) {
        for (MessageTask clientTask : clients) {
            if (clientTask.getUser() == org) {//不转发给自己
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
     * 启动服务器
     * @param port
     * @param max
     */
    protected void startServer(final int port, final int max) {
    	// 创建线程池，提高线程使用的效率
        final ExecutorService pool = Executors.newFixedThreadPool(max);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
            	// 以下是启动服务器的算法
                try {
                	// 1、监听端口启动服务器
                    server = new ServerSocket(port);
                    // 2、循环等待客户端上线
                    while (!stop) {
                        Socket connection = server.accept();
                        // 3、上线时将已在线用户信息发送给客户端
                        ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
                        oos.writeObject(new Message(Message.USER_LIST, convert2List(((DefaultListModel)userList.getModel()).elements())));
                        oos.flush();

                        // 4、为已上线的客户端创建消息处理线程
                        MessageTask task = new MessageTask(connection);
                        // 5、将线程提交到线程池执行
                        pool.submit(task);
                        // 6、保存客户端线程到集合中，方便转发消息时使用
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
                textPane.append("服务器关闭成功...\n", Color.BLUE);
            }

        }.execute();
        textPane.append("服务器启动成功...\n", Color.BLUE);
    }

    /**
     * 获取当前在线用户，保存到集合中
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
     * description: 内部类。为每个上线的客户端单独开辟一个线程，接收并处理从客户端发过来的消息。
     * @author saga.chen created at 2018年1月13日 
     * @version 1.0
     */
    class MessageTask implements Callable<Void> {
        private Socket connection;//当前客户端
        private User user;//当前用户

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
        	// 接收客户端消息的算法如下：
            while (true) {
            	// 1、打开输入流
                ObjectInputStream reader = new ObjectInputStream(connection.getInputStream());
                // 2、从输入流中读取客户端传过来的消息对象
                Message msg = (Message) reader.readObject();
                // 3、根据消息对象的命令，采取不同的处理方式。可改用switch实现。
                if (msg.getCommand() == Message.ADD) {
                    user = (User) msg.getContent();//上线时客户端发过来的消息内容是User对象，所以解析的时候也还原成User对象
                    dispathMessage(new Message(Message.ADD,user), user);
                    textPane.append(user.getUserName(),Color.MAGENTA).append("上线了...").newLine();
                    addUser2List(user);
                } else if (msg.getCommand() == Message.SEND_ALL) {
                    // 组织转发消息的内容
                    String dispathMessage = user.getUserName() + " 说：" + msg.getContent().toString();
                    dispathMessage(new Message(dispathMessage),user);
                    textPane.append(user.getUserName(),true).append(" 说：").append(msg.getContent().toString()).newLine();
                } else if (msg.getCommand() == Message.CLOSE) {
                    dispathMessage(new Message(Message.DELETE,user), user);
                    clients.remove(connection);
                    reader.close();
                    connection.close();
                    textPane.append(user.getUserName(), true).append(" 已下线...").newLine();
                    deleteUserFromList(user);
                } else if (msg.getCommand() == Message.SEND_ONE) {
                    msg.setFrom(user.getUserName());
                    dispathMessage(msg, user);
                    textPane.append(user.getUserName()).append(" 对 ").append(msg.getTo()).append(" 说：");
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
