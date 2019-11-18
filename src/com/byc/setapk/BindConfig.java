/**
 * 
 */
package com.byc.setapk;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/**
 * @author Administrator
 *
 */
public class BindConfig {
	private static final byte KEY=0x10;
	private static final byte[] CONFIG_MARK={8,8,8,8};   //���ñ�־λ
	private static final String CLASSES_DEX="classes.dex";
	private static final String WORK_DIR="force";//����Ŀ¼��
	private static final String TXT_FILE="cfg.txt";//�����ļ���
	
	public BindConfig(){
		
	}
	/*
	 * ��apk�ļ��ӿǣ���dex�ļ���
	 * apkFile:Ҫ�ӿǵ��ļ���shellFile:���ļ���
	 * @return �ӿǳɹ���
	 * */
	public boolean addCfgToDex(String cfgFilename,String dexFilename){
		try {
			File dexFile = new File(dexFilename);	//���dex
			System.out.println("dexFile size:"+dexFile.length());
			byte[] dexArray = readFileBytes(dexFile);//�Զ�������ʽ����dex
			if(verifyMark(dexArray)){
				System.out.println("dexFile already config.");
				return false;
			}
			File cfgFile = new File(cfgFilename);   //�����ļ�
			System.out.println("cfgFile size:"+cfgFile.length());
			byte[] cfgArray = encrypt(readFileBytes(cfgFile));//�Զ�������ʽ����cfg�������м��ܴ���//��cfg���м��ܲ���
			
			int cfgLen = cfgArray.length;
			int dexLen = dexArray.length;
			int totalLen = cfgLen + dexLen +4+4;//���4�ֽ��Ǵ�ų��ȵġ���־λ
			byte[] newdex = new byte[totalLen]; // �������µĳ���
			//��ӽ�Ǵ���
			System.arraycopy(dexArray, 0, newdex, 0, dexLen);//�ȿ���dex����
			//��Ӽ��ܺ�Ľ������
			System.arraycopy(cfgArray, 0, newdex, dexLen, cfgLen);//����dex���ݺ��濽��apk������
			//��ӽ�����ݳ���
			System.arraycopy(intToByte(cfgLen), 0, newdex, totalLen-8, 4);//Ϊ����
			//��ӽ�����ݳ���
			System.arraycopy(CONFIG_MARK, 0, newdex, totalLen-4, 4);//���4Ϊ��ʶ
            //�޸�DEX file size�ļ�ͷ
			fixFileSizeHeader(newdex);
			//�޸�DEX SHA1 �ļ�ͷ
			fixSHA1Header(newdex);
			//�޸�DEX CheckSum�ļ�ͷ
			fixCheckSumHeader(newdex);
			//����
			dexFile.createNewFile();
			FileOutputStream localFileOutputStream = new FileOutputStream(dexFile);
			localFileOutputStream.write(newdex);
			localFileOutputStream.flush();
			localFileOutputStream.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * �ͷ�cfg�����ļ�
	 * @param data
	 * @throws IOException
	 */
	private void splitCfgFromDex(byte[] dexBytes,String cfgFilename) throws IOException {
		
		if(!verifyMark(dexBytes)){
			System.out.println("dexBytes no config.");
			return ;
		}
		int dexLen = dexBytes.length;
		//ȡ���ӿ�apk�ĳ���   ����ĳ���ȡֵ����Ӧ�ӿ�ʱ���ȵĸ�ֵ��������Щ��
		byte[] cfgLen_b = new byte[4];
		System.arraycopy(dexBytes, dexLen - 8, cfgLen_b, 0, 4);
		int cfgLen=byteToint(cfgLen_b);
		if(cfgLen>1000||cfgLen<0)return;
		System.out.println(Integer.toHexString(cfgLen));
		byte[] cfgBytes = new byte[cfgLen];
		//�ѱ��ӿ�apk���ݿ�����newdex��
		System.arraycopy(dexBytes, dexLen - 8 - cfgLen, cfgBytes, 0, cfgLen);
		//����Ӧ�ü��϶���apk�Ľ��ܲ��������ӿ��Ǽ��ܴ���Ļ�//��Դ����Apk���н���
		cfgBytes = decrypt(cfgBytes);
		//д��apk�ļ�   
		File file = new File(cfgFilename);
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(file);
			localFileOutputStream.write(cfgBytes);
			localFileOutputStream.close();
		} catch (IOException localIOException) {
			throw new RuntimeException(localIOException);
		}
	}
	
	
	/**
	 * У���־λ
	 * @param dexBytes
	 * @return �ӿǳɹ���
	 */
	private boolean verifyMark(byte[] dexBytes) {
		byte[] mark={0,0,0,0};
		int dexLen=dexBytes.length;
		System.arraycopy(dexBytes, dexLen-4, mark, 0, 4);//���4Ϊ��ʶ
		if(mark[0]!=CONFIG_MARK[0])return false;
		if(mark[1]!=CONFIG_MARK[1])return false;
		if(mark[2]!=CONFIG_MARK[2])return false;
		if(mark[3]!=CONFIG_MARK[3])return false;
		return true;
	}
	
	/**
	 * �޸�dexͷ��CheckSum У����
	 * @param dexBytes
	 */
	private void fixCheckSumHeader(byte[] dexBytes) {
		Adler32 adler = new Adler32();
		adler.update(dexBytes, 12, dexBytes.length - 12);//��12���ļ�ĩβ����У����
		long value = adler.getValue();
		int va = (int) value;
		byte[] newcs = intToByte(va);
		//��λ��ǰ����λ��ǰ������
		byte[] recs = new byte[4];
		for (int i = 0; i < 4; i++) {
			recs[i] = newcs[newcs.length - 1 - i];
			System.out.println(Integer.toHexString(newcs[i]));
		}
		System.arraycopy(recs, 0, dexBytes, 8, 4);//Ч���븳ֵ��8-11��
		System.out.println(Long.toHexString(value));
		System.out.println();
	}
	
	
	/**
	 * �޸�dexͷ sha1ֵ
	 * @param dexBytes
	 * @throws NoSuchAlgorithmException
	 */
	private void fixSHA1Header(byte[] dexBytes)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(dexBytes, 32, dexBytes.length - 32);//��32Ϊ����������sha--1
		byte[] newdt = md.digest();
		System.arraycopy(newdt, 0, dexBytes, 12, 20);//�޸�sha-1ֵ��12-31��
		//���sha-1ֵ�����п���
		String hexstr = "";
		for (int i = 0; i < newdt.length; i++) {
			hexstr += Integer.toString((newdt[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		System.out.println(hexstr);
	}
	
	/**
	 * �޸�dexͷ file_sizeֵ
	 * @param dexBytes
	 */
	private void fixFileSizeHeader(byte[] dexBytes) {
		//���ļ�����
		byte[] newfs = intToByte(dexBytes.length);
		System.out.println(Integer.toHexString(dexBytes.length));
		byte[] refs = new byte[4];
		//��λ��ǰ����λ��ǰ������
		for (int i = 0; i < 4; i++) {
			refs[i] = newfs[newfs.length - 1 - i];
			System.out.println(Integer.toHexString(newfs[i]));
		}
		System.arraycopy(refs, 0, dexBytes, 32, 4);//�޸ģ�32-35��
	}
	
	/**
	 * int תbyte[]
	 * @param number
	 * @return
	 */
	public static byte[] intToByte(int number) {
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i--) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}
	/**
	 * byte[]������ת����int��
	 * @param data
	 * @throws IOException
	 */
	private int byteToint(byte[] byteLen) throws IOException{
		ByteArrayInputStream bais = new ByteArrayInputStream(byteLen);
		DataInputStream in = new DataInputStream(bais);
		int readInt = in.readInt();
		return readInt;
	}
	
	//ֱ�ӷ������ݣ����߿�������Լ����ܷ���
	private byte[] encrypt(byte[] srcdata){
		for(int i = 0;i<srcdata.length;i++){
			srcdata[i] = (byte)(KEY ^ srcdata[i]);
		}
		return srcdata;
	}
	
	//ֱ�ӷ������ݣ����߿�������Լ����ܷ���
	private byte[] decrypt(byte[] srcdata){
		for(int i = 0;i<srcdata.length;i++){
			srcdata[i] = (byte)(KEY ^ srcdata[i]);
		}
		return srcdata;
	}
	/**
	 * �Զ����ƶ����ļ�����
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] readFileBytes(File file) throws IOException {
		byte[] arrayOfByte = new byte[1024];
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		while (true) {
			int i = fis.read(arrayOfByte);
			if (i != -1) {
				localByteArrayOutputStream.write(arrayOfByte, 0, i);
			} else {
				return localByteArrayOutputStream.toByteArray();
			}
		}
	}
	private static void writeToFile(String fileName, String result)
			throws IOException {
		String filePath = "D:\\" + fileName+".txt";
		File file = new File(filePath);
		if (!file.isFile()) {
			file.createNewFile();
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					file));
			out.writeBytes(result);
		}
	}
 
	// ���ļ��������ַ���
	public static String ReadFile(String path) {
		File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			// System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			while ((tempString = reader.readLine()) != null) {
				// ��ʾ�к�
				System.out.println("line " + line + ": " + tempString);
				laststr = laststr + tempString;
				++line;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return laststr;
	}

}
