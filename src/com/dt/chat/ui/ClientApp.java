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
 * description: �ͻ��������� 
 * @author �򺣱룬�����
 * @date 2019/1/2
 */
public class ClientApp {
    private JFrame frame;//������
    private JTextField ipTextField;//ip��ַ�ı���
    private JTextField portTextField;//�˿��ı���
    private JTextField usernameTextField;//�û����ı���
    private JTextField msgTextField;//������Ϣ�ı���
    private SimpleTextPane textPane;//��Ϣ��ʾ�ı����
    private JList userList;//�û��б�ؼ�
    private Socket client;//����ͻ��˵�socket����
    private boolean connected = false;//�Ƿ����ӷ�������״̬��ʶ��
    private User user;//��ǰ������û�����
    private MessageThread messageThread;//�ͻ��˽�����Ϣ���߳�

    /**
     * �����߳�
     */
    public static void main(String[] args) {
        SwingUtils.setTheme(Theme.WINDOWS);//���ó�������⣨Ƥ����

        EventQueue.invokeLater(new Runnable() {//�����¼��ɷ����������ͻ���������
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
     *�����߳�
     */
    public ClientApp() {
        initialize();
    }

    /**
     * �ı䴰�ڷ��
     */
    private void initialize() {
    	final String currentTheme = UIManager.getLookAndFeel().getClass().getName();
    	frame = new JFrame();
        frame.setTitle("��֮��-����Զϲ��JAVA");
        frame.setBounds(100, 100, 640, 480);
        frame.setMinimumSize(new Dimension(590, 440));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//���õ㴰���ϵ�xʱ��Ĭ�ϲ����κβ��������ô����¼�������������ʲô
        frame.addWindowListener(new WindowAdapter() {//���������ڹر��¼�
            @Override
            public void windowClosing(WindowEvent e) {
                disconnection();//���ڹر�ʱ�ȶϿ�socket����
                System.exit(0);//���˳�����
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
        usernameTextField.setText("�������û���"); 
        northPanel.add(usernameTextField);

        final JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(550, 10));
        progressBar.setVisible(false);
        final JButton connectButton = new JButton("\u8FDE\u63A5");
        //���Ӱ�ť�ĵ���¼�
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (connected) {
                    return;
                }
                progressBar.setVisible(true);
                //����socket�����ӷŵ���̨�߳�ִ�У�����Swing�߳�������������һ����������Ч��
                SwingWorker<Void, String> task = new SwingWorker<Void, String>() {

                    @Override
                    protected Void doInBackground() throws Exception {

                        try {
                            Thread.sleep(500);
                            //���ݷ�����ip�Ͷ˿ڴ���socket������������ɹ��ͱ�ʾ���ӷ������ɹ�
                            client = new Socket(ipTextField.getText().trim(),
                                    Integer.parseInt(portTextField.getText().trim()));
                            connected=true;
                            publish("�Ѹ���������������...");
                            setProgress(50);

                            Thread.sleep(200);
                            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                            publish("�������...");
                            setProgress(60);

                            Thread.sleep(200);
                            //��װ�û���Ϣ
                            user = new User();
                            user.setUserName(usernameTextField.getText().trim());
                            user.setIp(client.getLocalAddress().getHostAddress());
                            //��װ��¼����������Ϣ����״ε�¼����Ҫ�ǽ��ͻ��˵��û���Ϣ���͹�ȥ��
                            Message msg = new Message(Message.ADD, user);
                            //�������������Ϣ���͵�������
                            oos.writeObject(msg);
                            oos.flush();
                            publish("����������͵�¼��Ϣ...");
                            setProgress(90);

                            Thread.sleep(200);
                            //��Ϊ������Ϣ��ͨ��������ʵ�֣�����������read������������ǰ�̣߳����Ա��뽫������Ϣ�Ĳ���
                            //�ŵ��µ��߳���ȥִ�У���֤��ǰ�߳����������У����⡰������������ر���Ҫ��
                            messageThread = new MessageThread(client);
                            messageThread.start();
                            publish("������Ϣ�����߳�...");
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
                        if (user != null) {//������ӳɹ������޸����촰�ڱ���
                            ClientApp.this.frame.setTitle(user.getUserName()+" - ������");
                        }
                    }
                };
                //������̨�̵߳�״̬�仯���Ӷ�ͬ����������״̬������Ҫ��Ч��ͬѧ��ֱ�Ӻ������
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
        //�Ͽ���ť���¼�����
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
        //Ϊ������Ϣ��ť�������������Դ�����Ϣ�ķ���
        AbstractAction sendAction=new AbstractAction() {
        	//�������д��������������ɫ����������ʾ��Ч�������Ŀɺ���
            Random rd = new Random();
            Color[] colors = {null, Color.RED, Color.BLUE, Color.MAGENTA};

            public void actionPerformed(ActionEvent e) {
                if (client == null) {//�ͻ���û�������ӣ���ֱ���˳�
                    return;
                }
                // ��������Ϣ���͵��㷨����
                // 1����ȡ��Ϣ�ı��������
                String text = msgTextField.getText();
                // 2�������Զ��巽���жϸ���Ϣ�Ƿ�Ϊ��˽������Ϣ
                boolean isPM=checkPrivateMessage(text);
                // 3����װsocketͨ�ŵ���Ϣģ�ͣ�ע������Ⱥ�ĺ�˽�ĵĲ�ͬ
                Message msg = null;
                if (!isPM) {//Ⱥ�Ĵ���ʽ
                    textPane.append("�� ˵��", true).append(text + "\n", colors[rd.nextInt(4)]);
                    msg = new Message(Message.SEND_ALL, text);
                } else {//˽�Ĵ���ʽ��
                	// �Ҷ��塰@username xxxx����ʽ����ϢΪ˽����Ϣ��������Ҫ���մ˸�ʽ����
                    int spaceIndex = text.indexOf(" ");//��һ���ո���ֵ�λ��
                    // �ڶ����ַ�����һ���ո�֮ǰΪ˽�����û���
                    String friendName = text.substring(1, spaceIndex);//��������
                    // ��һ���ո�֮��������ַ�ΪҪ˵�Ļ�
                    String word = text.substring(spaceIndex + 1);//Ҫ˵�Ļ�
                    textPane.append("�� �� ", true).append(friendName, true).append(" ˵��", true);
                    textPane.append(word + "\n", colors[rd.nextInt(4)]);
                    msg = new Message(Message.SEND_ONE, word);
                    msg.setTo(friendName);
                }

                // 4�����������������Ϣ
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
        //Ϊ�û��б�����¼���������˫��ĳ���û���ʱ�Զ�����˽����Ϣ��ǰ׺
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
     * ���������������Ϣ
     * @param msg
     */
    private void sendMessage(Message msg) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(msg);//��msg����д��socket�����
            oos.flush();//����ˢ��
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * ��鷢����Ϣ�Ƿ���˽�ı�־�����ﶨ�塰@username xxxx����ʽ���ı�Ϊ˽����
     * @param text
     * @return
     */
    private boolean checkPrivateMessage(String text) {
        if (!text.startsWith("@")) {
            return false;
        }
        int spaceIndex = text.indexOf(" ");
        if (spaceIndex <= 1) {//ƥ�䡰@ ���������
            return false;
        }

        return true;
    }

    /**
     * �Ͽ���������������
     */
    private void disconnection() {
        try {
            if (messageThread!=null && messageThread.isAlive()) {
                Message closeMsg = new Message(Message.CLOSE, null);
                sendMessage(closeMsg);
                client.close();
                connected=false;
                textPane.append("�ѶϿ���������������...", Color.RED).newLine();
                if (user != null) {//������ӳɹ������޸����촰�ڱ���
                    ClientApp.this.frame.setTitle(user.getUserName()+" - �ѶϿ�");
                }
                ((DefaultListModel)userList.getModel()).removeAllElements();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * ��ĳ���û���ӵ��û��б�����ʾ
     * @param user
     */
    private void addUser2List(User user) {
        ((DefaultListModel)userList.getModel()).addElement(user);
    }
    /**
     * ���û��б�ɾ��ĳ���û�
     * @param user
     */
    private void deleteUserFromList(User user) {
        ((DefaultListModel) userList.getModel()).removeElement(user);
    }

    /**
     * description: �ڲ��ࡣ����ѭ�����շ��������͵���Ϣ�� 
     * @author saga.chen created at 2018��1��13�� 
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
                    // ����������Ϣ���㷨����
                	// 1����socket������������װ�ɿɻ�ԭ���л������������
                    ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
                    // 2��������������read���������յ������ݻ�ԭ��Message���󣨱���Ҫ���͵�Ҳ�Ǵ����͵Ķ���
                    Message msg = (Message) reader.readObject();//���÷����л�������socket��������Ķ���ԭ
                    // 3��������Ϣ����Ĳ�ͬ�����ȡ��ͬ�Ĵ���ʽ���ɸ���switch�﷨ʵ��
                    if (msg.getCommand() == Message.SEND_ALL) {
                        textPane.append(msg.getContent().toString()).newLine();
                    } else if (msg.getCommand() == Message.ADD) {
                        User other = (User) msg.getContent();
                        addUser2List(other);
                        textPane.append(other.getUserName(), true).append(" ������...").newLine();
                    } else if (msg.getCommand() == Message.USER_LIST) {
                        List<User> users = (List) msg.getContent();
                        for (User tmp : users) {
                            addUser2List(tmp);
                        }
                    } else if (msg.getCommand() == Message.DELETE) {
                        User other = (User) msg.getContent();
                        deleteUserFromList((User) msg.getContent());
                        textPane.append(other.getUserName(), true).append(" ������...").newLine();
                    } else if (msg.getCommand() == Message.SEND_ONE) {
                        if (user.getUserName().equals(msg.getTo())) {
                            textPane.append(msg.getFrom(),true).append(" �� �� ˵��",true);
                        }else{
                            textPane.append(msg.getFrom()).append(" �� ").append(msg.getTo()).append(" ˵��");
                        }
                        textPane.append(msg.getContent().toString()).newLine();
                    }
                }
            } catch (EOFException e) {//
                System.out.println("�������ѹر�...");
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("�������ѹر�...");
                //�������رգ�����û��б�
                ((DefaultListModel)userList.getModel()).removeAllElements();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}