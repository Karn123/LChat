package LZMUDPChatting;

import java.util.*;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class LZMUDPServer extends Thread
{
	private static final int Server_Port = 10052;//服务端端口号
	private static DatagramSocket receiveSocket;// 接受消息的Socket
	private static DatagramPacket recPacket;// 接收到的数据报
	// 用来存储客户端socketAddress的List
	public static List<SocketAddress> socketAddressesList;
	// 哈希表，用来将用户昵称与SocketAddress进行映射
	public static HashMap<String, SocketAddress> hashTable;

	// 线程函数，接收消息，每次接收到一条消息都要及时反馈
	public void run()
	{
		while (true)
		{
			try
			{
				receiveSocket.receive(recPacket);
				// 一定要用此法读
				String msg = new String(recPacket.getData(), 0, recPacket.getLength());
				String compare = msg.split(" ")[0];
				if (!compare.equals("上线"))
				{// 如果不是客户端用来检验服务器是否能连接上的消息则将此消息转发给所有其它客户端，
					// 否则，将新登录的用户信息放到哈希表里，将用户名和SocketAdress进行映射

					// 判断是不是悄悄话
					String copyMsg = msg.trim();
					//发送者只要输入"/"+昵称+空格+消息即可，但是服务器接收时会收到是谁发来的
					// 如 Katy:/Riri hello!(Katy对日日悄悄说hello!)
					try
					{	//如果是命令
						if (copyMsg.contains(":") && (copyMsg.split(":")[1].charAt(0) == '/')
								&& (!copyMsg.split(":")[1].equals("/shake")))
						{// 确保不是窗口抖动消息，如果是窗口抖动，则要把此消息转发给所有客户端
							checkCommand(copyMsg);
						}
						// 如果不是则将此消息转发给所有客户端
						else
						{
							boolean flag = true;
							for (SocketAddress socket : socketAddressesList)
							{
								if (recPacket.getSocketAddress().equals(socket))
								{
									flag = false;
									break;
								}
							}
							if (flag == true) socketAddressesList.add(recPacket.getSocketAddress());
							msg = "   " + msg;
							System.out.println(
									"Receive message:" + msg + "\t" + String.valueOf(recPacket.getSocketAddress()));
							sendMsgToAllClients(msg);// 发给所有客户端
						}
					}
					catch(Exception e)
					{//如果不是正常命令，则将此消息发给所有客户端
						msg="   "+msg;
						sendMsgToAllClients(msg);// 发给所有客户端
					}
				}
				else
				{// 新用户登陆
					receiveSocket.send(recPacket);
					SocketAddress sockAddress = recPacket.getSocketAddress();
					String name = msg.split(" ")[1];
					// 把name和socketAddress放到hashTable里
					hashTable.put(name, sockAddress);
				}

			}
			catch (IOException e)
			{
				receiveSocket.close();
			}
		}
	}

	// 将消息转发给所有客户端
	public void sendMsgToAllClients(String msg)
	{
		try
		{
			for (SocketAddress socketAddress : socketAddressesList)
			{
				byte[] Data = msg.getBytes();
				DatagramPacket tmpDp = new DatagramPacket(Data, Data.length, socketAddress);
				receiveSocket.send(tmpDp);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception Catched!");
			receiveSocket.close();
		}
	}

	// 对悄悄话命令进行处理并把悄悄话发送给指定的用户
	public void checkCommand(String msg)
	{
		// 发送悄悄话的人
		String senderName = msg.split(":")[0];
		String info = msg.split(":")[1];
		String targetNamePart = info.split(" ")[0];
		// 悄悄话内容信息
		String Msg = info.split(" ")[1];
		// 想要发悄悄话的对象
		String targetName = targetNamePart.substring(1, targetNamePart.length());
		try
		{//如果悄悄话发送对象用户不存在，则抛出异常，显示警告信息
			// 根据哈希表拿到socketAddress
			SocketAddress address = hashTable.get(targetName);
			String message = "   \"" + senderName + "\"悄悄对你说:" + Msg;
			String message1 = "   //你悄悄对 \"" + targetName + "\"说:" + Msg;
			byte[] data = message.getBytes();// 发给接受悄悄话方
			byte[] data_1 = message1.getBytes();// 反馈给自己
			DatagramPacket dP = new DatagramPacket(data, data.length, address);
			DatagramPacket dP_1 = new DatagramPacket(data_1, data_1.length, recPacket.getSocketAddress());
			try
			{ // 发送悄悄话
				receiveSocket.send(dP);
				// 反馈给自己，说明发送成功
				receiveSocket.send(dP_1);
			}
			catch (IOException e)
			{
				return;
			}
		}
		catch(Exception e)
		{	
			String alertMsg="警告!";//警告客户端此人不存在！
			byte[] d=alertMsg.getBytes();
			DatagramPacket dP= new DatagramPacket(d, d.length,recPacket.getSocketAddress());
			try
			{
				receiveSocket.send(dP);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return;
		}
	}

	public static void main(String[] args) throws SocketException
	{
		try
		{// 初始化
			hashTable = new HashMap<String, SocketAddress>();
			receiveSocket = new DatagramSocket(Server_Port);
			socketAddressesList = new ArrayList<SocketAddress>();
		}
		catch (Exception e)
		{
			System.out.println("Exception Catched!");
		}
		System.out.println("服务器端已启动....");
		byte[] data = new byte[1024];
		recPacket = new DatagramPacket(data, data.length);
		// 启动接受消息线程
		LZMUDPServer receiveThread = new LZMUDPServer();
		receiveThread.start();
	}
}