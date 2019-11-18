package com.dt.chat.ui.theme;

/**
 * description: ��װ���õ�SwingƤ���������ͬƤ��֮�����ر����Ĳ��
 * 
 * @author ������
 * @date 2018/12/27
 */

public abstract class Theme {
	public static String DEFAULT = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static String NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	private String name;// Ƥ������
	private double rate;// �ı����ѹ��������ͨ����windowsƤ��Ϊ��׼��nimbusƤ��������0.6��

	public Theme(String name, double rate) {
		this.name = name;
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}