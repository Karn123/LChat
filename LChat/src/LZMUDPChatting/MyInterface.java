package LZMUDPChatting;

public class MyInterface
{
	//���������Ľӿ�
	interface alertMessage
	{
		void alertMsg(String msg);
	}

	//�ӷ���Ϣ�ӿ�
	interface ChatFunctions
	{
		void sendMessage();
		void recvMessage();
	}
}
