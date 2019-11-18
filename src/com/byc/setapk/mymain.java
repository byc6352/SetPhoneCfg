package com.byc.setapk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

import com.wjdiankong.parseresource.Setresource;


public class mymain {
	
	//private static String apkfile="force/myphone.apk";//原APK文件；
	//private static String unzippath="force/myphone/";//解压后的目录；
	private static final String CLASSES_DEX="classes.dex";//
	private static final String RESOURES_ARSC="resources.arsc";//
	
	//private static String WORK_PATH="force/";//工作目录
	private static String WORK_PATH="";//工作目录
	
	private static final String APK_FILE_NAME="ct.apk";//
	
	private static final String APP_ICON_FILE_NAME="ic_launcher.png";//
	
	private static String DEX_CFG_FIILE="dex.cfg";//参数配置文件
	private static String RSC_CFG_FIILE="rsc.cfg";//资源配置文件
	
	private static String SIGN_BAT="sign.bat";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try{
			String apkfilename=WORK_PATH+APK_FILE_NAME;
			String apkname=APK_FILE_NAME.substring(0,APK_FILE_NAME.length()-4);
			String unzippath=WORK_PATH+apkname+"/";
			Funcs.deleteFile(WORK_PATH+apkname);
			ZipUtil.unzip(apkfilename, unzippath);//解压;

			String dexCfgFilename=WORK_PATH+DEX_CFG_FIILE;//参数配置文件
			String dexFilename=unzippath+CLASSES_DEX;    //参数配置文件
			BindConfig bindConfig=new BindConfig();
			bindConfig.addCfgToDex(dexCfgFilename, dexFilename);
			
			String rscCfgFilename=WORK_PATH+RSC_CFG_FIILE;//资源配置文件 
			String rscFilename=unzippath+RESOURES_ARSC;
			Setresource setresource=Setresource.getInstance(rscFilename);
			setresource.loadEditStrFromfile(rscCfgFilename);
			setresource.Execute();
			//ico
			String newIconFilename=WORK_PATH+APP_ICON_FILE_NAME;
			String oldIconFilename=unzippath+"/res/drawable/"+APP_ICON_FILE_NAME;
			replaceAppIcon(newIconFilename,oldIconFilename);
			
			//save
			String cfgfilename=apkname+"_cfg.apk";
			String newfilename=WORK_PATH+cfgfilename;
			Funcs.deleteFile(newfilename);
			ZipUtil.zip(unzippath, newfilename);
			System.out.println("apk zip complete.");
			Funcs.deleteFile(unzippath);
			
			//sign
			//String signfilename=apkname+"_cfg_sign.apk";
			//signapk(cfgfilename,signfilename);
			//System.out.println("apk signapk complete.");
		}catch(Exception e){
			System.out.println("unzip file error:"+e.toString());
		}
	}
	private static void replaceAppIcon(String newFilename,String oldFilename)throws IOException{
		File newfile=new File(newFilename);
		if(!newfile.exists())return;
		File oldfile=new File(oldFilename);
		if(oldfile.exists())oldfile.delete();
		Files.copy(newfile.toPath(), oldfile.toPath());
	}
	
	private static void signapk(String apkfilename,String newfilename){
		String signbat=WORK_PATH+SIGN_BAT;
		File signFile=new File(signbat);
		String signFilePath=signFile.getAbsolutePath();
		//String result = CMDUtil.excuteBatFile(signFilePath,false);
		//System.out.println(result);
		//callCmd("cmd.exe "+signFilePath);
	}
	
	private static void  callCmd(String locationCmd){
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
               in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            System.out.println("sb:" + sb.toString());
            System.out.println("callCmd execute finished");           
        } catch (IOException e) {
            System.out.println(e);
        }
     }
	
/*
 * 			String cmd = "ipconfig";
			String result = CMDUtil.excuteCMDCommand(cmd);
			System.out.println(result);
 * 
 * */
}
