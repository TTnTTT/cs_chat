package com.dt.chat.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;



/**
 * ͼƬ�ļ���������ֻ��Ҫ�����ļ������͡�
 * @author �򺣱�
 *@date 2019/1/3
 */
public class ImageFilter extends FileFilter {
	@Override
	public String getDescription() {
		return "ͼƬ(*.jpeg *.jpg *.png)";
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
