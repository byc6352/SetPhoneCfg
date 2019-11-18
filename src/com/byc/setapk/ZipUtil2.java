/**
 * 
 */
package com.byc.setapk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * @author ASUS
 *
 */
public class ZipUtil2 {
	/*
	 * 实现从zip中删除指定文件(任意位置)
	 * */
	public static boolean delFileFromZip(String zipFile, String delfile) {//throws ZipException
		try{
		ZipFile zip = new ZipFile(zipFile);
		zip.setFileNameCharset("GBK");
		checkzipFile(zip);
		// 获取文件头
		List<FileHeader> list = zip.getFileHeaders();
		String dname =null;
		boolean flag = true;
		     // 如果采用forEach会出现并发异常,采用for可以解决，
		     // 但是索引移位问题会遗漏head
		for (int i = 0; i < list.size(); i++) {
			dname = list.get(i).getFileName();
		         // 在匹配的时候可以用正则或者endwith
			if (dname.endsWith(delfile)) {
				zip.removeFile(dname);
				--i;// 解决索引移位问题
				flag = false;
				System.out.println(dname + "文件删除成功！");
			}
		}
		if (flag) {
			System.out.println("没有找到你要删除的文件！");
		}
		return true;
		}catch(ZipException e){
			e.printStackTrace();
		}
		return false;
	}
	/*
	 * 实现添加文件到zip
	 * */
	public static boolean addFileToZip(String zipFilename, String afilename){
		try{
			ZipFile zipFile = new ZipFile(zipFilename);
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			File aFile=new File(afilename);
			parameters.setFileNameInZip(aFile.getName());
			parameters.setSourceExternalStream(true);
			InputStream is = new FileInputStream(afilename);
			zipFile.addStream(is, parameters);
			is.close();//SecurityException IOException  throws ZipException 
			return true;
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ZipException e){
			e.printStackTrace();
		}
		return false;
	}
	 private static void checkzipFile(ZipFile zip) throws ZipException {
		 if (!zip.isValidZipFile()) {
			 throw new ZipException("此文件不存在或不合法!");
		 }
		 if (zip.isSplitArchive()) {
			 throw new ZipException("此文件为分卷文件,不能删除!");
		 }
	 }
}
