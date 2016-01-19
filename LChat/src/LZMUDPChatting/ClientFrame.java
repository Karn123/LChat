package LZMUDPChatting;

import java.awt.Dimension;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import LZMUDPChatting.MyInterface.alertMessage;

import java.awt.Color;

class ClientFrame extends JFrame implements ActionListener, MyInterface.ChatFunctions, MyInterface.alertMessage
{// �����
	static JButton sendMsgButton; // ������Ϣ��ť
	static JButton clearMsgButton;// �����Ϣ��ť
	static JButton ExitButton;// �˳���¼��ť
	static JLabel user_name; // ��ʾ�û��ǳ�
	static JTextArea displayMsgTextArea; // ��ʾ��
	static JTextArea inputTextArea; // �����
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String path = "LChatImages/"; // ͼƬ���·��
	protected JPanel contentPane;

	private static DatagramSocket SenderSocket;// ������Ϣ��Socket
	private static DatagramPacket SenderPacket;// ������Ϣ���ݱ�
	private static int Server_Port = 0; // ��ʼ��Ϊ0
	private static String serIp = null; // ��ʼ��
	private static InetAddress serverIp;
	private LZMUDPClient parent; // ������

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public ClientFrame(LZMUDPClient Parent) throws IOException
	{
		super.paint(getGraphics());
		parent = Parent;

		// ���ӳɹ���ӵ�¼�����õ�������ip��port
		Server_Port = parent.getPort();
		serIp = parent.getIP();
		serverIp = InetAddress.getByName(serIp);
		
		// ���ƽ���
		setTitle("�����ң�Let's Play!");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 654, 507);
		// �ô������
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		// ���Ʊ���ͼƬ
		String Path=path + "ChatBg.jpg";
		ImageIcon background = new ImageIcon(Path);
		JLabel lblNewLabel = new JLabel(background);
		lblNewLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
		// �����ݴ���ת��ΪJPanel���������÷���setOpaque()��ʹ���ݴ���͸��
		JPanel imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// �ѱ���ͼƬ��ӵ��ֲ㴰�����ײ���Ϊ����
		getLayeredPane().add(lblNewLabel, new Integer(Integer.MIN_VALUE));

		getContentPane().setBackground(UIManager.getColor("InternalFrame.borderLight"));

		clearMsgButton = new JButton("���");
		clearMsgButton.setBackground(UIManager.getColor("Button.shadow"));
		clearMsgButton.setFont(new Font("����", Font.PLAIN, 18));
		clearMsgButton.setBounds(399, 445, 102, 32);
		clearMsgButton.addActionListener(this);
		getLayeredPane().add(clearMsgButton, new Integer(Integer.MAX_VALUE));
		// ������ʾ�����Ϸ�

		sendMsgButton = new JButton("������Ϣ");
		sendMsgButton.setBackground(UIManager.getColor("Button.shadow"));
		sendMsgButton.setFont(new Font("����", Font.PLAIN, 18));
		sendMsgButton.setBounds(289, 445, 108, 33);
		sendMsgButton.addActionListener(this);
		getLayeredPane().add(sendMsgButton, new Integer(Integer.MAX_VALUE));
		// ������ʾ�����Ϸ�
		ExitButton = new JButton("�˳�");
		ExitButton.addActionListener(this);
		ExitButton.setBackground(SystemColor.menu);
		ExitButton.setFont(new Font("����", Font.PLAIN, 16));
		ExitButton.setBounds(537, 384, 78, 27);
		contentPane.add(ExitButton);
		// ��ʾ��
		displayMsgTextArea = new JTextArea(14, 67);
		displayMsgTextArea.setBounds(2, 2, 610, 147);
		displayMsgTextArea.setWrapStyleWord(true);
		displayMsgTextArea.setBackground(new Color(245, 255, 250));
		displayMsgTextArea.setFont(new Font("����", Font.BOLD, 18));
		displayMsgTextArea.setLineWrap(true);
		displayMsgTextArea.setEditable(false);
		displayMsgTextArea.setOpaque(false);

		// Ϊ����������ʾ����ӱ���ͼƬ
		JScrollPane jScrollPane = new JScrollPane(displayMsgTextArea);
		jScrollPane.setBorder(null);
		jScrollPane.setBounds(0, 0, 641, 314);
		jScrollPane.setOpaque(false);
		jScrollPane.getViewport().setOpaque(false);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ImageIcon imageIcon = new ImageIcon("LChatImages/Bg.png");
		JPanel jPanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			{
				this.setOpaque(false);
				getContentPane().setLayout(null);
			}

			public void paintComponent(Graphics g)
			{
				g.drawImage(imageIcon.getImage(), 0, 0, this);
				super.paintComponents(g);
			}
		};
		jPanel.setBounds(0, 0, 641, 314);
		jPanel.add(jScrollPane);
		getContentPane().add(jPanel);

		// �����
		inputTextArea = new JTextArea();
		inputTextArea.setBackground(SystemColor.info);
		inputTextArea.setWrapStyleWord(true);
		inputTextArea.setLineWrap(true);
		inputTextArea.setFont(new Font("����", Font.BOLD, 18));
		inputTextArea.setBounds(0, 324, 512, 154);
		inputTextArea.addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					sendMessage();
					e.consume();// ���һ��Ҫ��
				}
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
			}
		});
		getContentPane().add(inputTextArea);

		// �ǳ�label
		JLabel lblNewLabel_1 = new JLabel("�ǳ�:");
		lblNewLabel_1.setFont(new Font("����", Font.BOLD, 18));
		lblNewLabel_1.setBounds(512, 334, 56, 27);
		getContentPane().add(lblNewLabel_1);

		String name = Parent.getUserName();
		user_name = new JLabel(name);
		user_name.setFont(new Font("����", Font.BOLD, 18));
		user_name.setBounds(556, 334, 102, 27);
		getContentPane().add(user_name);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				printExitMsg();
				System.exit(0);
			}
		});

		// ��ȡ���û��������¼
		getChattingHistory();

		// ������ͨ�Ų���,���Ƚ��������socket��ֵ��SenderSocket��Ȼ����������TimeOut
		// (��Ϊ֮ǰ�ڸ�������������timeoutΪ4s����������Ӧ������Ϊ0)
		SenderSocket = parent.getSocket();
		SenderSocket.setSoTimeout(0);
		String mString = "----------�û�" + user_name.getText() + "����--------";
		SendMessage(mString);
		myUDPReciveMsgThread recvThread = new myUDPReciveMsgThread(this,displayMsgTextArea, SenderSocket);

		// ʵ�ֽ�������
		JButton button = new JButton("����");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				String address = "D:\\LChatScreenShot";
				File f = new File(address);
				if (!f.exists()) f.mkdir();
				String inputValue = JOptionPane.showInputDialog("������ͼƬ����");
				ScreenShot shot = new ScreenShot(address + "\\" + inputValue, "png");
				shot.snapShot();
				alertMsg("�����ɹ�!��ȥD:\\LChatScreenShot�ļ����²鿴!");
			}
		});
		button.setFont(new Font("����", Font.PLAIN, 16));
		button.setBackground(SystemColor.menu);
		button.setBounds(537, 428, 78, 27);
		contentPane.add(button);
		recvThread.start();
	}

	// ��ӡ�˳���������Ϣ
	private void printExitMsg()
	{
		String exitMsg = " �û� \"" + user_name.getText() + "\" ������";
		byte[] data = exitMsg.getBytes();
		SenderPacket = new DatagramPacket(data, data.length, serverIp, Server_Port);
		try
		{
			saveHistoryMsg();// ������û��������¼
			SenderSocket.send(SenderPacket);
		}
		catch (IOException e)
		{
			alertMsg("IOException Caught!");
		}
		finally
		{
			SenderSocket.close();
			System.exit(0);
		}
	}

	// ��ø��û���������ʷ��¼
	private void getChattingHistory() throws IOException
	{
		String address = "D:\\LChattingHistory";
		File f = new File(address);
		if (!f.exists()) f.mkdir();
		String historyTxt = address + "\\" + user_name.getText() + ".txt";
		File file = new File(historyTxt);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				alertMsg("��ʷ��¼�ļ�����ʧ�ܣ�");
			}
		}
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		FileInputStream in = null;
		try
		{
			in = new FileInputStream(file);
			in.read(filecontent);
		}
		catch (FileNotFoundException e)
		{
			alertMsg("Error!");
		}
		in.close();
		String History = new String(filecontent, 0, filecontent.length);
		String mString = "";
		if (!History.equals(""))
		{
			mString = "-----------------��ʷ��¼------------------" + "\n";
			displayMsgTextArea.append(History + "\n");
			displayMsgTextArea.append(mString);
		}
	}

	// ������û���ʷ��¼
	private void saveHistoryMsg() throws IOException
	{
		String hiString = displayMsgTextArea.getText();
		String address = "D:\\LChattingHistory";
		File f = new File(address);
		if (!f.exists()) f.mkdir();
		String historyTxt = address + "\\" + user_name.getText() + ".txt";
		File file = new File(historyTxt);
		if (!file.exists()) file.createNewFile();
		FileWriter fileWriter = new FileWriter(historyTxt, false); // false����׷��
		fileWriter.write(hiString);
		fileWriter.close();
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if ((evt.getSource() == sendMsgButton))
		{
			sendMessage();
		}
		else if ((evt.getSource() == ExitButton))
		{
			printExitMsg();
		}
		else clearInputText();
	}

	// ����������Ϣ
	@Override
	public void alertMsg(String msg)
	{
		JOptionPane.showMessageDialog(null, msg, "��ʾ", JOptionPane.WARNING_MESSAGE);
	}

	// ��������
	private void clearInputText()
	{
		inputTextArea.requestFocus();
		inputTextArea.select(0, 0);
		inputTextArea.setText(null);
	}

	// ����ָ����Ϣ
	public void SendMessage(String mString)
	{
		byte[] data = mString.getBytes();
		SenderPacket = new DatagramPacket(data, data.length, serverIp, Server_Port);
		try
		{
			SenderSocket.send(SenderPacket);
		}
		catch (IOException e)
		{
			alertMsg("Error!");
		}
	}

	@Override
	public void sendMessage()
	{
		if (!(inputTextArea.getText().equals("")))
		{
			byte[] data;
			String mString = inputTextArea.getText();
			clearInputText();
			String s = user_name.getText() + ":" + mString;
			data = s.getBytes();

			SenderPacket = new DatagramPacket(data, data.length, serverIp, Server_Port);
			try
			{
				SenderSocket.send(SenderPacket);
				clearInputText();
			}
			catch (IOException e)
			{
				System.out.println("IOException Catched!!");
			}
		}
		else alertMsg("��Ϣ����Ϊ�գ�");
	}

	@Override
	public void recvMessage()
	{
		// ��myUDPReceiveMsgThread�߳���ʵ��
	}
	//�������ܺ���
	public void ShakeFrame(ClientFrame ShakeFrame)
	{
		int x = ShakeFrame.getX();
		int y = ShakeFrame.getY();
		for (int i = 0; i < 20; i++) 
		{
			if ((i & 1) == 0) 
			{
				x += 6;
				y += 5;
			} 
			else
			{
				x -= 6;
				y -= 5;
			}
			ShakeFrame.setLocation(x, y);
			try
			{
				Thread.sleep(30);
			} 
			catch (InterruptedException e1) 
			{
				e1.printStackTrace();
			}
		}
	}
}

// ��һ���߳���������Ϣ
class myUDPReciveMsgThread extends Thread
{
	public JTextArea textArea;
	public DatagramPacket dPacket;
	public DatagramSocket dSocket;
	public ClientFrame clientFrame;
	// ��displayTextArea������������Ϣ��ʾ����senderSocket����������ͨ��
	public myUDPReciveMsgThread(ClientFrame clientFrame,JTextArea textArea, DatagramSocket dSocket)
	{
		this.clientFrame=clientFrame;
		this.textArea = textArea;
		this.dSocket = dSocket;
	}

	public void run()
	{
		byte[] data = new byte[1024];
		dPacket = new DatagramPacket(data, data.length);
		while (true)
		{
			try
			{
				dSocket.receive(dPacket);
				String feedbackMsg = new String(dPacket.getData(), 0, dPacket.getLength());
				if(feedbackMsg.contains(":")&&feedbackMsg.split(":")[1].equals("/shake"))
					clientFrame.ShakeFrame(clientFrame);
				else if(feedbackMsg.equals("����!"))
					JOptionPane.showMessageDialog(null, "��Ҫ�������Ļ����û�������!", "��ʾ", JOptionPane.WARNING_MESSAGE);
				else
				{
					if (textArea.getText().equals(""))
					{
						textArea.append(feedbackMsg);
					}
					else textArea.append("\n" + feedbackMsg);
					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
		dSocket.close();
	}
}