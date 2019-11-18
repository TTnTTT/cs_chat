package com.dt.chat.ui;

import com.dt.chat.custom.SimpleTextPane;
import com.dt.chat.model.Message;
import com.dt.chat.model.User;
import com.dt.chat.ui.theme.Theme;
import com.dt.chat.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Random;
/**
 * description: 客户端主程序 
 * @author 向海彪，田何理
 * @date 2019/1/2
 */
public class ClientApp {
    private JFrame frame;//主窗口
    private JTextField ipTextField;//ip地址文本框
    private JTextField portTextField;//端口文本框
    private JTextField usernameTextField;//用户名文本框
    private JTextField msgTextField;//发送消息文本框
    private SimpleTextPane textPane;//消息显示文本面板
    private JList userList;//用户列表控件
    private Socket client;//代表客户端的socket对象
    private boolean connected = false;//是否连接服务器的状态标识符
    private User user;//当前聊天的用户对象
    private MessageThread messageThread;//客户端接收消息的线程

    /**
     * 建立线程
     */
    public static void main(String[] args) {
        SwingUtils.setTheme(Theme.WINDOWS);//设置程序的主题（皮肤）

        EventQueue.invokeLater(new Runnable() {//采用事件派发机制启动客户端主窗口
            public void run() {
                try {
                    ClientApp window = new ClientApp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *启动线程
     */
    public ClientApp() {
        initialize();
    }

    /**
     * 改变窗口风格
     */
    private void initialize() {
    	final String currentTheme = UIManager.getLookAndFeel().getClass().getName();
    	frame = new JFrame();
        frame.setTitle("梦之队-我永远喜欢JAVA");
        frame.setBounds(100, 100, 640, 480);
        frame.setMinimumSize(new Dimension(590, 440));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//设置点窗口上的x时，默认不做任何操作，改用窗口事件监听器决定做什么
        frame.addWindowListener(new WindowAdapter() {//监听主窗口关闭事件
            @Override
            public void windowClosing(WindowEvent e) {
                disconnection();//窗口关闭时先断开socket连接
                System.exit(0);//再退出程序
            }
        });

        JPanel northPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(0, 80));
        northPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u8FDE\u63A5\u4FE1\u606F",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
        frame.getContentPane().add(northPanel, BorderLayout.NORTH);

        JLabel lblNewLabel = new JLabel("\u670D\u52A1\u5668IP");
        northPanel.add(lblNewLabel);

        ipTextField = new JTextField();

        ipTextField.setColumns((int) (16 * SwingUtils.getThemeRate(currentTheme)));
        ipTextField.setText("127.0.0.1");
        northPanel.add(ipTextField);

        JLabel lblNewLabel_1 = new JLabel("\u7AEF\u53E3");
        northPanel.add(lblNewLabel_1);

        portTextField = new JTextField();
        portTextField.setColumns((int) (6 * SwingUtils.getThemeRate(currentTheme)));
        portTextField.setText("4080");
        northPanel.add(portTextField);

        JLabel lblNewLabel_2 = new JLabel("\u7528\u6237\u540D");
        northPanel.add(lblNewLabel_2);

        usernameTextField = new JTextField();
        usernameTextField.setColumns((int) (16 * SwingUtils.getThemeRate(currentTheme)));
        usernameTextField.setText("请输入用户名"); 
        northPanel.add(usernameTextField);

        final JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(550, 10));
        progressBar.setVisible(false);
        final JButton connectButton = new JButton("\u8FDE\u63A5");
        //连接按钮的点击事件
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (connected) {
                    return;
                }
                progressBar.setVisible(true);
                //将跟socket的连接放到后台线程执行，避免Swing线程阻塞。并做了一个进度条特效。
                SwingWorker<Void, String> task = new SwingWorker<Void, String>() {

                    @Override
                    protected Void doInBackground() throws Exception {

                        try {
                            Thread.sleep(500);
                            //根据服务器ip和端口创建socket对象，如果创建成果就表示连接服务器成功
                            client = new Socket(ipTextField.getText().trim(),
                                    Integer.parseInt(portTextField.getText().trim()));
                            connected=true;
                            publish("已跟服务器建立连接...");
                            setProgress(50);

                            Thread.sleep(200);
                            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                            publish("打开输出流...");
                            setProgress(60);

                            Thread.sleep(200);
                            //封装用户信息
                            user = new User();
                            user.setUserName(usernameTextField.getText().trim());
                            user.setIp(client.getLocalAddress().getHostAddress());
                            //封装登录服务器的消息命令。首次登录，主要是将客户端的用户信息发送过去。
                            Message msg = new Message(Message.ADD, user);
                            //调用输出流将消息发送到服务器
                            oos.writeObject(msg);
                            oos.flush();
                            publish("向服务器发送登录信息...");
                            setProgress(90);

                            Thread.sleep(200);
                            //因为接收消息是通过输入流实现，而输入流的read方法会阻塞当前线程，所以必须将接收消息的操作
                            //放到新的线程里去执行，保证当前线程能正常运行，避免“假死”。这点特别重要！
                            messageThread = new MessageThread(client);
                            messageThread.start();
                            publish("启动消息监听线程...");
                            setProgress(100);
                        } catch (NumberFormatException | IOException e1) {
                            connected=false;
                            publish(e1.getMessage());
                            setProgress(100);
                            e1.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        for (String string : chunks) {
                            textPane.append(string, Color.BLUE).newLine();
                        }
                    }

                    @Override
                    protected void done() {
                        progressBar.setValue(0);
                        progressBar.setVisible(false);
                        connectButton.setEnabled(false);
                        if (user != null) {//如果连接成功，则修改聊天窗口标题
                            ClientApp.this.frame.setTitle(user.getUserName()+" - 已连接");
                        }
                    }
                };
                //监听后台线程的状态变化，从而同步进度条的状态。不需要特效的同学可直接忽略这里。
                task.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            progressBar.setValue((Integer) evt.getNewValue());
                        }
                    }
                });
                task.execute();
            }
        });

        northPanel.add(connectButton);

        JButton cutoffButton = new JButton("\u65AD\u5F00");
        //断开按钮的事件处理
        cutoffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnection();
                connectButton.setEnabled(true);
            }
        });
        northPanel.add(cutoffButton);

        northPanel.add(progressBar);

        JPanel southPanel = new JPanel();
        southPanel.setBorder(
                new TitledBorder(null, "\u529F\u80FD\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
        frame.getContentPane().add(southPanel, BorderLayout.SOUTH);

        msgTextField = new JTextField();
        southPanel.add(msgTextField);
        msgTextField.setColumns((int) (75 * SwingUtils.getThemeRate(currentTheme)));

        JButton sendButton = new JButton("\u53D1\u9001");
        //为发送消息按钮创建监听程序，以处理消息的发送
        AbstractAction sendAction=new AbstractAction() {
        	//以下两行代码是利用随机颜色产生文字显示特效，不懂的可忽略
            Random rd = new Random();
            Color[] colors = {null, Color.RED, Color.BLUE, Color.MAGENTA};

            public void actionPerformed(ActionEvent e) {
                if (client == null) {//客户端没建立连接，则直接退出
                    return;
                }
                // 以下是消息发送的算法流程
                // 1、获取消息文本框的内容
                String text = msgTextField.getText();
                // 2、调用自定义方法判断该消息是否为“私发”消息
                boolean isPM=checkPrivateMessage(text);
                // 3、封装socket通信的消息模型，注意区分群聊和私聊的不同
                Message msg = null;
                if (!isPM) {//群聊处理方式
                    textPane.append("我 说：", true).append(text + "\n", colors[rd.nextInt(4)]);
                    msg = new Message(Message.SEND_ALL, text);
                } else {//私聊处理方式。
                	// 我定义“@username xxxx”形式的消息为私发消息，所以需要按照此格式解析
                    int spaceIndex = text.indexOf(" ");//第一个空格出现的位置
                    // 第二个字符到第一个空格之前为私发的用户名
                    String friendName = text.substring(1, spaceIndex);//朋友姓名
                    // 第一个空格之后的所有字符为要说的话
                    String word = text.substring(spaceIndex + 1);//要说的话
                    textPane.append("我 对 ", true).append(friendName, true).append(" 说：", true);
                    textPane.append(word + "\n", colors[rd.nextInt(4)]);
                    msg = new Message(Message.SEND_ONE, word);
                    msg.setTo(friendName);
                }

                // 4、调用输出流发送消息
                sendMessage(msg);
                msgTextField.setText("");
            }
        };
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
        //为用户列表添加事件监听器，双击某个用户名时自动生成私发消息的前缀
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    Object username = userList.getSelectedValue();
                    msgTextField.setText("@" + username + " ");
                }
            }
        });
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(userList);

        final JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setViewportBorder(
                new TitledBorder(null, "\u804A\u5929\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
        splitPane.setRightComponent(scrollPane_1);

        textPane = new SimpleTextPane();
        DefaultCaret caret = (DefaultCaret)textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane_1.setViewportView(textPane);
    }

    /**
     * 调用输出流发送消息
     * @param msg
     */
    private void sendMessage(Message msg) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(msg);//将msg对象写入socket输出流
            oos.flush();//立即刷新
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 检查发送消息是否有私聊标志。这里定义“@username xxxx”格式的文本为私发。
     * @param text
     * @return
     */
    private boolean checkPrivateMessage(String text) {
        if (!text.startsWith("@")) {
            return false;
        }
        int spaceIndex = text.indexOf(" ");
        if (spaceIndex <= 1) {//匹配“@ ”这种情况
            return false;
        }

        return true;
    }

    /**
     * 断开跟服务器的连接
     */
    private void disconnection() {
        try {
            if (messageThread!=null && messageThread.isAlive()) {
                Message closeMsg = new Message(Message.CLOSE, null);
                sendMessage(closeMsg);
                client.close();
                connected=false;
                textPane.append("已断开跟服务器的连接...", Color.RED).newLine();
                if (user != null) {//如果连接成功，则修改聊天窗口标题
                    ClientApp.this.frame.setTitle(user.getUserName()+" - 已断开");
                }
                ((DefaultListModel)userList.getModel()).removeAllElements();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 将某个用户添加到用户列表中显示
     * @param user
     */
    private void addUser2List(User user) {
        ((DefaultListModel)userList.getModel()).addElement(user);
    }
    /**
     * 从用户列表删除某个用户
     * @param user
     */
    private void deleteUserFromList(User user) {
        ((DefaultListModel) userList.getModel()).removeElement(user);
    }

    /**
     * description: 内部类。用于循环接收服务器发送的消息。 
     * @author saga.chen created at 2018年1月13日 
     * @version 1.0
     */
    class MessageThread extends Thread {
        private Socket socket;

        public MessageThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (connected) {
                    // 以下是收消息的算法流程
                	// 1、打开socket的输入流，包装成可还原序列化对象的输入流
                    ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
                    // 2、调用输入流的read方法，将收到的数据还原成Message对象（必须要求发送的也是此类型的对象）
                    Message msg = (Message) reader.readObject();//利用反序列化技术将socket输入流里的对象还原
                    // 3、根据消息对象的不同命令，采取不同的处理方式，可改用switch语法实现
                    if (msg.getCommand() == Message.SEND_ALL) {
                        textPane.append(msg.getContent().toString()).newLine();
                    } else if (msg.getCommand() == Message.ADD) {
                        User other = (User) msg.getContent();
                        addUser2List(other);
                        textPane.append(other.getUserName(), true).append(" 已上线...").newLine();
                    } else if (msg.getCommand() == Message.USER_LIST) {
                        List<User> users = (List) msg.getContent();
                        for (User tmp : users) {
                            addUser2List(tmp);
                        }
                    } else if (msg.getCommand() == Message.DELETE) {
                        User other = (User) msg.getContent();
                        deleteUserFromList((User) msg.getContent());
                        textPane.append(other.getUserName(), true).append(" 已下线...").newLine();
                    } else if (msg.getCommand() == Message.SEND_ONE) {
                        if (user.getUserName().equals(msg.getTo())) {
                            textPane.append(msg.getFrom(),true).append(" 对 我 说：",true);
                        }else{
                            textPane.append(msg.getFrom()).append(" 对 ").append(msg.getTo()).append(" 说：");
                        }
                        textPane.append(msg.getContent().toString()).newLine();
                    }
                }
            } catch (EOFException e) {//
                System.out.println("输入流已关闭...");
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("服务器已关闭...");
                //服务器关闭，清空用户列表
                ((DefaultListModel)userList.getModel()).removeAllElements();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}