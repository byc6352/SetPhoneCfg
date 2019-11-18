/**
 * 
 */
package com.wjdiankong.parseresource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;
//import net.sf.json.JSONObject;
/**
 * @author Administrator
 *
 */
public class Setresource {
	private static Setresource current;
	public Map <String,String> mapEditStr=new HashMap<>(); //要修改的键值对集合
	private String mResourcefile;//resources.arsc
	
	public Setresource(String filename){
		mResourcefile=filename;
	}
	public static synchronized Setresource getInstance(String filename) {
        if(current == null) {
            current = new Setresource(filename);
        }
        return current;
   }
	public static synchronized Setresource getInstance() {
        return current;
   }
	
	public Map <String,String> inputStr(){
		return mapEditStr;
	}
	
	public void Execute(){
		byte[] srcByte = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try{
			//fis = new FileInputStream("resource/resources_gdt1.arsc");
			File file=new File(mResourcefile);
			if(!file.exists())return;
			
			fis = new FileInputStream(mResourcefile);
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len=fis.read(buffer)) != -1){
				bos.write(buffer, 0, len);
			}
			srcByte = bos.toByteArray();
		}catch(Exception e){
			System.out.println("read res file error:"+e.toString());
		}finally{
			try{
				fis.close();
				bos.close();
			}catch(Exception e){
				System.out.println("close file error:"+e.toString());
			}
		}
		
		if(srcByte == null){
			System.out.println("get src error...");
			return;
		}
		
		System.out.println("parse restable header...");
		ParseResourceUtils.parseResTableHeaderChunk(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		System.out.println("parse resstring pool chunk...");
		ParseResourceUtils.parseResStringPoolChunk(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		System.out.println("parse package chunk...");
		ParseResourceUtils.parsePackage(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		System.out.println("parse typestring pool chunk           ...");
		ParseResourceUtils.parseTypeStringPoolChunk(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		System.out.println("parse keystring pool chunk...");
		ParseResourceUtils.parseKeyStringPoolChunk(srcByte);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		int resCount = 0;
		while(!ParseResourceUtils.isEnd(srcByte.length)){
			resCount++;
			boolean isSpec = ParseResourceUtils.isTypeSpec(srcByte);
			if(isSpec){
				System.out.println("parse restype spec chunk...");
				ParseResourceUtils.parseResTypeSpec(srcByte);
				System.out.println("++++++++++++++++++++++++++++++++++++++");
				System.out.println();
			}else{
				System.out.println("parse restype info chunk...");
				ParseResourceUtils.parseResTypeInfo(srcByte);
				System.out.println("++++++++++++++++++++++++++++++++++++++");
				System.out.println();
			}
		}
		FileUtil.saveFile(srcByte, mResourcefile);
		System.out.println("res count:"+resCount);
	}
	
	public void loadEditStrFromfile(String filename){
		File cfgFile = new File(filename);
		if (!cfgFile.exists())return;
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while((str = br.readLine()) != null) {
				int index=str.indexOf(":");
				if(index>0){
					String key=str.substring(0,index);
					int i=key.indexOf("\"");
					int j=key.lastIndexOf("\"");
					key=key.substring(i+1,j);
					String value=str.substring(index+1,str.length());
					i=value.indexOf("\"");
					j=value.lastIndexOf("\"");
					value=value.substring(i+1,j);
					mapEditStr.put(key, value);
				}

            }

		}catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
	}
	
	// 读文件，返回字符串
	public static String ReadFile(String path) {
			File file = new File(path);
			BufferedReader reader = null;
			String laststr = "";
			try {
				// System.out.println("以行为单位读取文件内容，一次读一整行：");
				reader = new BufferedReader(new FileReader(file));
				String tempString = null;
				int line = 1;
				// 一次读入一行，直到读入null为文件结束
				while ((tempString = reader.readLine()) != null) {
					// 显示行号
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
