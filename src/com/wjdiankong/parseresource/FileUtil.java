/**
 * 
 */
package com.wjdiankong.parseresource;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *����byte���飬�����ļ�
 *
 * @author seesun2012@163.com
 *
 */
public class FileUtil {

	/**
     * ����byte���飬�����ļ�
     * filePath  �ļ�·��
     * fileName  �ļ����ƣ���Ҫ����׺����*.jpg��*.java��*.xml��
     */
    public static void saveFile(byte[] bfile,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
        	file = new File(fileName);
            if(file.exists()){//�ж��ļ�Ŀ¼�Ƿ����
            	file.delete();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    
	/**
     * ����byte���飬�����ļ�
     * filePath  �ļ�·��
     * fileName  �ļ����ƣ���Ҫ����׺����*.jpg��*.java��*.xml��
     */
    public static void getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists() && !dir.isDirectory()){//�ж��ļ�Ŀ¼�Ƿ����
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}


