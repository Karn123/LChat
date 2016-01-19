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
	private String fileName; // 文件前缀名
	private String defaultName = "LChat";// 默认前缀名
	static int serialNum = 0;
	private String imageFormat; // 图像文件的格式
	private String defaultImageFormat = "png";// 默认图片格式
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	// 默认构造函数初始化文件前缀名为LChat,图片格式为png
	public ScreenShot()
	{
		fileName = defaultName;
		imageFormat = defaultImageFormat;
	}

	// 支持JPG和PNG存储
	public ScreenShot(String s, String format)
	{
		fileName = s;
		imageFormat = format;
	}

	// 对屏幕进行截屏
	public void snapShot()
	{
		try
		{
			// 拷贝屏幕到一个BufferedImage对象screenshot
			BufferedImage screenshot = (new Robot()).createScreenCapture(
					new Rectangle(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight()));
			serialNum++;
			// 根据文件前缀变量和文件格式变量，自动生成文件名
			String name = fileName + String.valueOf(serialNum) + "." + imageFormat;
			File f = new File(name);

			// 将screenshot对象写入图像文件
			ImageIO.write(screenshot, imageFormat, f);

		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}
}