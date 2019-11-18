/**
 * 
 */
package com.byc.setapk;

/**
 * @author Administrator
 *
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ִ��windows��cmd�������
 * @author dufei
 *
 */
public class CMDUtil {

	/**
	 * ִ��һ��cmd����
	 * @param cmdCommand cmd����
	 * @return ����ִ�н���ַ�����������쳣����null
	 */
	public static String excuteCMDCommand(String cmdCommand) 
	{
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while((line=bufferedReader.readLine()) != null) 
			{
				stringBuilder.append(line+"\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ִ��bat�ļ���
	 * @param file bat�ļ�·��
	 * @param isCloseWindow ִ����Ϻ��Ƿ�ر�cmd����
	 * @return bat�ļ����log
	 */
	public static String excuteBatFile(String file, boolean isCloseWindow) 
	{
		String cmdCommand = null;
		if(isCloseWindow) 
		{
			cmdCommand = "cmd.exe /c "+file;
		}else 
		{
			cmdCommand = "cmd.exe /k "+file;
		}
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while((line=bufferedReader.readLine()) != null) 
			{
				stringBuilder.append(line+"\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ִ��bat�ļ�,�¿�����
	 * @param file bat�ļ�·��
	 * @param isCloseWindow ִ����Ϻ��Ƿ�ر�cmd����
	 * @return bat�ļ����log
	 */
	public static String excuteBatFileWithNewWindow(String file, boolean isCloseWindow) 
	{
		String cmdCommand = null;
		if(isCloseWindow) 
		{
			cmdCommand = "cmd.exe /c start"+file;
		}else 
		{
			cmdCommand = "cmd.exe /k start"+file;
		}
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while((line=bufferedReader.readLine()) != null) 
			{
				stringBuilder.append(line+"\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

