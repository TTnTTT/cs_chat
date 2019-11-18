package com.dt.chat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * ����Ҫ�����ļ��Ĵ�����Ҫ��ͼƬ���д���
 * @author ������
 * @date 2019/1/3
 */
public class FileUtil {

	/**
	 * �ļ�ת������
	 * 
	 * @param imageFile
	 * @return
	 */
	private static InputStream image2InputStream(File imageFile) {
		try {
			FileInputStream fis = new FileInputStream(imageFile);
			return fis;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ͼƬ�ļ�ת�ֽ�����
	 * 
	 * @param imageFile
	 * @return
	 */
	public static byte[] image2Bytes(File imageFile) {
		return inputStreamToBytes(image2InputStream(imageFile));
	}

	/**
	 * ������ת�ֽ�����
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStreamToBytes(InputStream is) {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int len;
		byte[] buff = new byte[1024 * 8];
		try {
			while ((len = is.read(buff)) != -1) {
				bytestream.write(buff, 0, len);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			is.close();
			return imgdata;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * �ֽ�����ת������
	 * 
	 * @param buf
	 * @return
	 */
	public static InputStream bytesToInputStream(byte[] buf) {
		if (buf == null)
			return null;
		return new ByteArrayInputStream(buf);
	}

	/**
	 * ����������תͼƬ
	 * 
	 * @param imgArr
	 */
	public static void createImage(byte[] imgArr) {
		try {
			FileOutputStream fos = new FileOutputStream(new File("head.png"));
			fos.write(imgArr, 0, imgArr.length);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}