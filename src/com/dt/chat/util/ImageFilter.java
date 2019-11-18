package com.dt.chat.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;



/**
 * 图片文件过滤器，只需要所需文件的类型。
 * @author 向海彪
 *@date 2019/1/3
 */
public class ImageFilter extends FileFilter {
	@Override
	public String getDescription() {
		return "图片(*.jpeg *.jpg *.png)";
	}

	@Override
	public boolean accept(File f) {
		if (f == null) {
			return false;
		}
		if (f.isDirectory()) {
			return true;
		}
		String fileName = f.getName();
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex != -1) {
			String suffix = fileName.substring(dotIndex + 1);
			if ("jpeg".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix))
				return true;
			else
				return false;
		} else {
			return false;
		}
	}
}
