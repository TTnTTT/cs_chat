package com.dt.chat.custom;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * description:简化JTextPane的常用操作，对追加文本、修改局部文本的颜色、字体等更加方便
 *
 * @author 向海彪 
 *@date 2018/12/29
 */
public class SimpleTextPane extends JTextPane {

	/**
	 * 换行
	 * @return
	 */
	public SimpleTextPane newLine() {
		String wrap = System.getProperty("line.separator");//采用系统默认的换行符
		return append(wrap);
	}

	/**
	 * 采用默认样式追加文本
	 *
	 * @param content
	 * @return
	 */
	public SimpleTextPane append(String content) {
		return append(content, new SimpleAttributeSet());
	}

	/**
	 * 采用指定颜色追加文本
	 *
	 * @param content
	 * @param color
	 * @return
	 */
	public SimpleTextPane append(String content, Color color) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attr, color);
		}
		return append(content, attr);
	}

	/**
	 * 采用指定字号追加文本
	 *
	 * @param content
	 * @param fontSize
	 * @return
	 */
	public SimpleTextPane append(String content, int fontSize) {
		if (fontSize < 9)//限制最小字体为9号，不然就看不清了
			fontSize = 9;
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, fontSize);
		return append(content, attr);
	}

	/**
	 * 是否采用斜体追加文本
	 *
	 * @param content
	 * @param italic
	 * @return
	 */
	public SimpleTextPane append(String content, boolean italic) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setItalic(attr, italic);
		return append(content, attr);
	}

	/**
	 * 用自定义属性追加文本
	 *
	 * @param content
	 * @param attr
	 */
	public SimpleTextPane append(String content, AttributeSet attr) {
		try {
			this.getStyledDocument().insertString(this.getDocument().getLength(), content, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return this;
	}

}