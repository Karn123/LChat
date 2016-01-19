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
{// 聊天框
	static JButton sendMsgButton; // 发送消息按钮
	static JButton clearMsgButton;// 清空消息按钮
	static JButton ExitButton;// 退出登录按钮
	static JLabel user_name; // 显示用户昵称
	static JTextArea displayMsgTextArea; // 显示框
	static JTextArea inputTextArea; // 输入框
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String path = "LChatImages/"; // 图片相对路径
	protected JPanel contentPane;

	private static DatagramSocket SenderSocket;// 发送消息的Socket
	private static DatagramPacket SenderPacket;// 发送消息数据报
	private static int Server_Port = 0; // 初始化为0
	private static String serIp = null; // 初始化
	private static InetAddress serverIp;
	private LZMUDPClient parent; // 父窗体

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public ClientFrame(LZMUDPClient Parent) throws IOException
	{
		super.paint(getGraphics());
		parent = Parent;

		// 连接成功后从登录界面拿到服务器ip和port
		Server_Port = parent.getPort();
		serIp = parent.getIP();
		serverIp = InetAddress.getByName(serIp);
		
		// 绘制界面
		setTitle("聊天室，Let's Play!");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 654, 507);
		// 让窗体居中
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		// 绘制背景图片
		String Path=path + "ChatBg.jpg";
		ImageIcon background = new ImageIcon(Path);
		JLabel lblNewLabel = new JLabel(background);
		lblNewLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// 把背景图片添加到分层窗格的最底层作为背景
		getLayeredPane().add(lblNewLabel, new Integer(Integer.MIN_VALUE));

		getContentPane().setBackground(UIManager.getColor("InternalFrame.borderLight"));

		clearMsgButton = new JButton("清空");
		clearMsgButton.setBackground(UIManager.getColor("Button.shadow"));
		clearMsgButton.setFont(new Font("宋体", Font.PLAIN, 18));
		clearMsgButton.setBounds(399, 445, 102, 32);
		clearMsgButton.addActionListener(this);
		getLayeredPane().add(clearMsgButton, new Integer(Integer.MAX_VALUE));
		// 让它显示在最上方

		sendMsgButton = new JButton("发送消息");
		sendMsgButton.setBackground(UIManager.getColor("Button.shadow"));
		sendMsgButton.setFont(new Font("宋体", Font.PLAIN, 18));
		sendMsgButton.setBounds(289, 445, 108, 33);
		sendMsgButton.addActionListener(this);
		getLayeredPane().add(sendMsgButton, new Integer(Integer.MAX_VALUE));
		// 让它显示在最上方
		ExitButton = new JButton("退出");
		ExitButton.addActionListener(this);
		ExitButton.setBackground(SystemColor.menu);
		ExitButton.setFont(new Font("宋体", Font.PLAIN, 16));
		ExitButton.setBounds(537, 384, 78, 27);
		contentPane.add(ExitButton);
		// 显示框
		displayMsgTextArea = new JTextArea(14, 67);
		displayMsgTextArea.setBounds(2, 2, 610, 147);
		displayMsgTextArea.setWrapStyleWord(true);
		displayMsgTextArea.setBackground(new Color(245, 255, 250));
		displayMsgTextArea.setFont(new Font("宋体", Font.BOLD, 18));
		displayMsgTextArea.setLineWrap(true);
		displayMsgTextArea.setEditable(false);
		displayMsgTextArea.setOpaque(false);

		// 为聊天内容显示框添加背景图片
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

		// 输入框
		inputTextArea = new JTextArea();
		inputTextArea.setBackground(SystemColor.info);
		inputTextArea.setWrapStyleWord(true);
		inputTextArea.setLineWrap(true);
		inputTextArea.setFont(new Font("宋体", Font.BOLD, 18));
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
					e.consume();// 这句一定要有
				}
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
			}
		});
		getContentPane().add(inputTextArea);

		// 昵称label
		JLabel lblNewLabel_1 = new JLabel("昵称:");
		lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 18));
		lblNewLabel_1.setBounds(512, 334, 56, 27);
		getContentPane().add(lblNewLabel_1);

		String name = Parent.getUserName();
		user_name = new JLabel(name);
		user_name.setFont(new Font("宋体", Font.BOLD, 18));
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

		// 获取该用户的聊天记录
		getChattingHistory();

		// 下面是通信部分,首先将父窗体的socket赋值给SenderSocket，然后重新设置TimeOut
		// (因为之前在父窗体中设置了timeout为4s，但是这里应该重置为0)
		SenderSocket = parent.getSocket();
		SenderSocket.setSoTimeout(0);
		String mString = "----------用户" + user_name.getText() + "上线--------";
		SendMessage(mString);
		myUDPReciveMsgThread recvThread = new myUDPReciveMsgThread(this,displayMsgTextArea, SenderSocket);

		// 实现截屏功能
		JButton button = new JButton("截屏");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				String address = "D:\\LChatScreenShot";
				File f = new File(address);
				if (!f.exists()) f.mkdir();
				String inputValue = JOptionPane.showInputDialog("请输入图片名称");
				ScreenShot shot = new ScreenShot(address + "\\" + inputValue, "png");
				shot.snapShot();
				alertMsg("截屏成功!请去D:\\LChatScreenShot文件夹下查看!");
			}
		});
		button.setFont(new Font("宋体", Font.PLAIN, 16));
		button.setBackground(SystemColor.menu);
		button.setBounds(537, 428, 78, 27);
		contentPane.add(button);
		recvThread.start();
	}

	// 打印退出聊天室信息
	private void printExitMsg()
	{
		String exitMsg = " 用户 \"" + user_name.getText() + "\" 已下线";
		byte[] data = exitMsg.getBytes();
		SenderPacket = new DatagramPacket(data, data.length, serverIp, Server_Port);
		try
		{
			saveHistoryMsg();// 保存此用户的聊天记录
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

	// 获得该用户的聊天历史记录
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
				alertMsg("历史记录文件创建失败！");
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
			mString = "-----------------历史记录------------------" + "\n";
			displayMsgTextArea.append(History + "\n");
			displayMsgTextArea.append(mString);
		}
	}

	// 保存该用户历史记录
	private void saveHistoryMsg() throws IOException
	{
		String hiString = displayMsgTextArea.getText();
		String address = "D:\\LChattingHistory";
		File f = new File(address);
		if (!f.exists()) f.mkdir();
		String historyTxt = address + "\\" + user_name.getText() + ".txt";
		File file = new File(historyTxt);
		if (!file.exists()) file.createNewFile();
		FileWriter fileWriter = new FileWriter(historyTxt, false); // false代表不追加
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

	// 弹出警告信息
	@Override
	public void alertMsg(String msg)
	{
		JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.WARNING_MESSAGE);
	}

	// 清除输入框
	private void clearInputText()
	{
		inputTextArea.requestFocus();
		inputTextArea.select(0, 0);
		inputTextArea.setText(null);
	}

	// 发送指定消息
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
		else alertMsg("消息不能为空！");
	}

	@Override
	public void recvMessage()
	{
		// 用myUDPReceiveMsgThread线程来实现
	}
	//震屏功能函数
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

// 用一个线程来接受消息
class myUDPReciveMsgThread extends Thread
{
	public JTextArea textArea;
	public DatagramPacket dPacket;
	public DatagramSocket dSocket;
	public ClientFrame clientFrame;
	// 将displayTextArea传进来进行消息显示，将senderSocket传进来进行通信
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
				else if(feedbackMsg.equals("警告!"))
					JOptionPane.showMessageDialog(null, "您要发送悄悄话的用户不存在!", "提示", JOptionPane.WARNING_MESSAGE);
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