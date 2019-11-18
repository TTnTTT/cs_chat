package com.dt.chat.ui.theme;

/**
 * description: 封装常用的Swing皮肤，解决不同皮肤之间像素比例的差距
 * 
 * @author 唐世杰
 * @date 2018/12/27
 */

public abstract class Theme {
	public static String DEFAULT = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static String NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

	private String name;// 皮肤名称
	private double rate;// 文本框的压缩比例。通常以windows皮肤为基准，nimbus皮肤是它的0.6倍

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