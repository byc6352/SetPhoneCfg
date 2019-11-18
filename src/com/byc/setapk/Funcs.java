/**
 * 
 */
package com.byc.setapk;

import java.io.File;


/**
 * @author Administrator
 *
 */
public class Funcs {
	/**
	 * 删除文件或文件夹
	 * 
	 * @param path
	 *            文件或文件夹的路径
	 */
	public static void deleteFile(String path) {
		deleteFile(new File(path));
	}
	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 *            文件或文件夹
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			System.out.println("The file to be deleted does not exist! File's path is: "+file.getPath());
		} else {
			deleteFileRecursively(file);
		}
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 *            文件或文件夹
	 */
	private static void deleteFileRecursively(File file) {
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				File item = new File(file, fileName);
				if (item.isDirectory()) {
					deleteFileRecursively(item);
				} else {
					if (!item.delete()) {
						System.out.println("Failed in recursively deleting a file, file's path is: "+
								item.getPath());
					}
				}
			}
			if (!file.delete()) {
				System.out.println("Failed in recursively deleting a directory, directories' path is: "+
						file.getPath());
			}
		} else {
			if (!file.delete()) {
				System.out.println("Failed in deleting this file, its path is: "+
						file.getPath());
			}
		}
	}
}
