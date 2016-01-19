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
	private static final int Server_Port = 10052;//����˶˿ں�
	private static DatagramSocket receiveSocket;// ������Ϣ��Socket
	private static DatagramPacket recPacket;// ���յ������ݱ�
	// �����洢�ͻ���socketAddress��List
	public static List<SocketAddress> socketAddressesList;
	// ��ϣ���������û��ǳ���SocketAddress����ӳ��
	public static HashMap<String, SocketAddress> hashTable;

	// �̺߳�����������Ϣ��ÿ�ν��յ�һ����Ϣ��Ҫ��ʱ����
	public void run()
	{
		while (true)
		{
			try
			{
				receiveSocket.receive(recPacket);
				// һ��Ҫ�ô˷���
				String msg = new String(recPacket.getData(), 0, recPacket.getLength());
				String compare = msg.split(" ")[0];
				if (!compare.equals("����"))
				{// ������ǿͻ�����������������Ƿ��������ϵ���Ϣ�򽫴���Ϣת�������������ͻ��ˣ�
					// ���򣬽��µ�¼���û���Ϣ�ŵ���ϣ������û�����SocketAdress����ӳ��

					// �ж��ǲ������Ļ�
					String copyMsg = msg.trim();
					//������ֻҪ����"/"+�ǳ�+�ո�+��Ϣ���ɣ����Ƿ���������ʱ���յ���˭������
					// �� Katy:/Riri hello!(Katy����������˵hello!)
					try
					{	//���������
						if (copyMsg.contains(":") && (copyMsg.split(":")[1].charAt(0) == '/')
								&& (!copyMsg.split(":")[1].equals("/shake")))
						{// ȷ�����Ǵ��ڶ�����Ϣ������Ǵ��ڶ�������Ҫ�Ѵ���Ϣת�������пͻ���
							checkCommand(copyMsg);
						}
						// ��������򽫴���Ϣת�������пͻ���
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
							sendMsgToAllClients(msg);// �������пͻ���
						}
					}
					catch(Exception e)
					{//���������������򽫴���Ϣ�������пͻ���
						msg="   "+msg;
						sendMsgToAllClients(msg);// �������пͻ���
					}
				}
				else
				{// ���û���½
					receiveSocket.send(recPacket);
					SocketAddress sockAddress = recPacket.getSocketAddress();
					String name = msg.split(" ")[1];
					// ��name��socketAddress�ŵ�hashTable��
					hashTable.put(name, sockAddress);
				}

			}
			catch (IOException e)
			{
				receiveSocket.close();
			}
		}
	}

	// ����Ϣת�������пͻ���
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

	// �����Ļ�������д��������Ļ����͸�ָ�����û�
	public void checkCommand(String msg)
	{
		// �������Ļ�����
		String senderName = msg.split(":")[0];
		String info = msg.split(":")[1];
		String targetNamePart = info.split(" ")[0];
		// ���Ļ�������Ϣ
		String Msg = info.split(" ")[1];
		// ��Ҫ�����Ļ��Ķ���
		String targetName = targetNamePart.substring(1, targetNamePart.length());
		try
		{//������Ļ����Ͷ����û������ڣ����׳��쳣����ʾ������Ϣ
			// ���ݹ�ϣ���õ�socketAddress
			SocketAddress address = hashTable.get(targetName);
			String message = "   \"" + senderName + "\"���Ķ���˵:" + Msg;
			String message1 = "   //�����Ķ� \"" + targetName + "\"˵:" + Msg;
			byte[] data = message.getBytes();// �����������Ļ���
			byte[] data_1 = message1.getBytes();// �������Լ�
			DatagramPacket dP = new DatagramPacket(data, data.length, address);
			DatagramPacket dP_1 = new DatagramPacket(data_1, data_1.length, recPacket.getSocketAddress());
			try
			{ // �������Ļ�
				receiveSocket.send(dP);
				// �������Լ���˵�����ͳɹ�
				receiveSocket.send(dP_1);
			}
			catch (IOException e)
			{
				return;
			}
		}
		catch(Exception e)
		{	
			String alertMsg="����!";//����ͻ��˴��˲����ڣ�
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
		{// ��ʼ��
			hashTable = new HashMap<String, SocketAddress>();
			receiveSocket = new DatagramSocket(Server_Port);
			socketAddressesList = new ArrayList<SocketAddress>();
		}
		catch (Exception e)
		{
			System.out.println("Exception Catched!");
		}
		System.out.println("��������������....");
		byte[] data = new byte[1024];
		recPacket = new DatagramPacket(data, data.length);
		// ����������Ϣ�߳�
		LZMUDPServer receiveThread = new LZMUDPServer();
		receiveThread.start();
	}
}