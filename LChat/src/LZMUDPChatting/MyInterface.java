package LZMUDPChatting;

public class MyInterface
{
	//弹出警告框的接口
	interface alertMessage
	{
		void alertMsg(String msg);
	}

	//接发消息接口
	interface ChatFunctions
	{
		void sendMessage();
		void recvMessage();
	}
}
