package LZMUDPChatting;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ScreenShot
{
	private String fileName; // �ļ�ǰ׺��
	private String defaultName = "LChat";// Ĭ��ǰ׺��
	static int serialNum = 0;
	private String imageFormat; // ͼ���ļ��ĸ�ʽ
	private String defaultImageFormat = "png";// Ĭ��ͼƬ��ʽ
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	// Ĭ�Ϲ��캯����ʼ���ļ�ǰ׺��ΪLChat,ͼƬ��ʽΪpng
	public ScreenShot()
	{
		fileName = defaultName;
		imageFormat = defaultImageFormat;
	}

	// ֧��JPG��PNG�洢
	public ScreenShot(String s, String format)
	{
		fileName = s;
		imageFormat = format;
	}

	// ����Ļ���н���
	public void snapShot()
	{
		try
		{
			// ������Ļ��һ��BufferedImage����screenshot
			BufferedImage screenshot = (new Robot()).createScreenCapture(
					new Rectangle(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight()));
			serialNum++;
			// �����ļ�ǰ׺�������ļ���ʽ�������Զ������ļ���
			String name = fileName + String.valueOf(serialNum) + "." + imageFormat;
			File f = new File(name);

			// ��screenshot����д��ͼ���ļ�
			ImageIO.write(screenshot, imageFormat, f);

		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}
}