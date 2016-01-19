package LZMUDPChatting;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.sun.javafx.sg.prism.NGWebView;

import sun.awt.image.BytePackedRaster;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.KeyAdapter;



public class LZMUDPClient extends JFrame implements ActionListener,
													MyInterface.alertMessage
{
	/**
	 SERVER_IP="180.160.52.164";
     SERVER_PORT=10052;
	*/
	private static final long serialVersionUID = 1L;
	static  String path="LChatImages/";//图片读取路径
	private JPanel contentPane; 
	private JTextField userNameTextField;//显示输入框
	private JTextField ServerIPStr;  //Ip输入框
	private JTextField serverPortStr; //Port输入框
	private JButton submitButton;   //提交按钮
	public  DatagramSocket senderSocket; //发送消息的Socket
	public  String userName=""; //用户昵称
	public  String server_ip;  
	public 	int  server_port;
	public  static LZMUDPClient f; 	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args)
	{
		f = new LZMUDPClient();
		f.setVisible(true);
	}
	
	public LZMUDPClient()
	{	//绘制界面
		setTitle("填写信息");
		setBounds(100,100,550,350);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
	
		//让窗体居中
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
					(screenSize.height - getHeight()) / 2);
		//绘制背景图片
		path+="Bg.png";
		ImageIcon background=new ImageIcon(path);
		JLabel lblNewLabel = new JLabel(background);
		lblNewLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		JPanel imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// 把背景图片添加到分层窗格的最底层作为背景
		getLayeredPane().add(lblNewLabel, new Integer(Integer.MIN_VALUE));
		contentPane.setLayout(null);
		
		submitButton = new JButton("确定");
		submitButton.setBackground(Color.LIGHT_GRAY);
		submitButton.setBounds(244, 253, 86, 29);
		contentPane.add(submitButton);
		submitButton.addActionListener(this);
		submitButton.setFont(new Font("宋体", Font.BOLD, 18));
		
		JLabel label2 = new JLabel("服务器IP:");
		label2.setBounds(114, 156, 105, 21);
		contentPane.add(label2);
		label2.setFont(new Font("宋体", Font.BOLD, 18));
		userNameTextField = new JTextField(17);
		userNameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar()==KeyEvent.VK_SPACE)
					e.consume();
			}
		});
		userNameTextField.setBounds(224, 100, 154, 27);
		contentPane.add(userNameTextField);
		userNameTextField.setFont(new Font("宋体", Font.BOLD, 18));
		userNameTextField.setMaximumSize(userNameTextField.getPreferredSize());
		ServerIPStr = new JTextField(12);
		ServerIPStr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar()==KeyEvent.VK_SPACE)
					e.consume();
			}
		});
		ServerIPStr.setBounds(224, 153, 154, 29);
		contentPane.add(ServerIPStr);
		ServerIPStr.setFont(new Font("宋体", Font.BOLD, 18));
		ServerIPStr.setMaximumSize(ServerIPStr.getPreferredSize());
		//imagePanel.add(lblNewLabel);

		JLabel label1 = new JLabel("你的昵称:");
		label1.setBounds(114, 103, 91, 21);
		contentPane.add(label1);
		label1.setFont(new Font("宋体", Font.BOLD, 18));
		
		JLabel lblNewLabel_5 = new JLabel("欢迎来到LChatting!!!");
		lblNewLabel_5.setFont(new Font("宋体", Font.BOLD, 18));
		lblNewLabel_5.setBounds(177, 52, 212, 29);
		contentPane.add(lblNewLabel_5);
		
		JLabel lblNewLabel_1 = new JLabel("服务器端口号:");
		lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 18));
		lblNewLabel_1.setBounds(86, 207, 133, 29);
		contentPane.add(lblNewLabel_1);
		//Enter键绑定确定按钮
		serverPortStr = new JTextField();
		serverPortStr.setFont(new Font("宋体", Font.BOLD, 18));
		serverPortStr.setBounds(224, 209, 154, 27);
		serverPortStr.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				// TODO Auto-generated method stub
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
					isLegal();//检测是否能连上服务器
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar()==KeyEvent.VK_SPACE)
					e.consume();
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
			}
			
		});
		contentPane.add(serverPortStr);
		serverPortStr.setColumns(10);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		try
		{
			senderSocket = new DatagramSocket();
		}
		catch (SocketException e2)
		{
			System.out.println("SocketException Catched!");
		}
	}
	
	//弹出警告信息
	@Override
	public void alertMsg(String msg)
	{
		JOptionPane.showMessageDialog(null, msg, "提示", JOptionPane.WARNING_MESSAGE); 
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{//此函数用来判断用户输入信息是否合法，如果合法则隐藏该frame并显示父窗口
		if(e.getSource()==submitButton)
		{
			isLegal();
		}
	}
	//判断用户输入信息是否合法，如果合法则隐藏该frame并显示父窗口
	public void isLegal()
	{
		String name=userNameTextField.getText();
		int port=Integer.valueOf(serverPortStr.getText());
		String ip=ServerIPStr.getText();
		//这里不能用==来判断，必须用equals来判断
		if(name.equals(""))
			alertMsg("昵称不能为空！");
		else
		{
			if(isSuccessfullyConnected(ip,port))
			{
				alertMsg("成功连接服务器!");
				setInfo(name, ip, port);
				setVisible(false);
				ClientFrame clientFrame = null;
				try
				{
					//加载子窗口，并将父类传递给子类
					clientFrame = new ClientFrame(f);
				}
				catch (IOException e1)
				{
					System.out.println("IOException Catched!");
				}
				clientFrame.setVisible(true);
			}
			else 
				alertMsg("连接服务器失败!");
		}
	}
	//检测是否成功连接上服务器，4s钟如果还没连上就连接失败
	public boolean isSuccessfullyConnected(String ip,int port) 
	{
		InetAddress serverIp;
		try
		{
			serverIp = InetAddress.getByName(ip);
		}
		catch (UnknownHostException e1)
		{
			return false;
		}
		//检测连接信息
		String testMsg="上线"+" "+userNameTextField.getText();
		byte[] data=testMsg.getBytes();
		DatagramPacket senderPacket = new DatagramPacket(data, data.length, serverIp, port );
		byte[] buf=new byte[1024];
		DatagramPacket recvPacket=new DatagramPacket(buf, buf.length);
		try
		{
			senderSocket.send(senderPacket);
		}
		catch (IOException e1)
		{
			alertMsg("IOException Caught in LZMUDPClient.java 249");
		};
		try
		{//社置时间片等待时间为4s
			senderSocket.setSoTimeout(4000);
			try
			{
				senderSocket.receive(recvPacket);
			}
			catch (IOException e)
			{
				return false;
			}
		}
		catch (SocketException e)
		{
			
			return false;
		}
		return true;
	}
	

	public void setInfo(String userName,String server_ip,int server_port)
	{
		this.userName=userName;
		this.server_ip=server_ip;
		this.server_port=server_port;
	}
	
	public String getIP()
	{
		return server_ip;
	}
	
	public int getPort()
	{
		return server_port;
	}
	
	public static String getPath()
	{
		return path;
	}

	public static void setPath(String path)
	{
		LZMUDPClient.path = path;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public static LZMUDPClient getF()
	{
		return f;
	}

	public static void setF(LZMUDPClient f)
	{
		LZMUDPClient.f = f;
	}
	public DatagramSocket getSocket()
	{
		return this.senderSocket;
	}
}