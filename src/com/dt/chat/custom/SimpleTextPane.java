package com.dt.chat.custom;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * description:��JTextPane�ĳ��ò�������׷���ı����޸ľֲ��ı�����ɫ������ȸ��ӷ���
 *
 * @author �򺣱� 
 *@date 2018/12/29
 */
public class SimpleTextPane extends JTextPane {

	/**
	 * ����
	 * @return
	 */
	public SimpleTextPane newLine() {
		String wrap = System.getProperty("line.separator");//����ϵͳĬ�ϵĻ��з�
		return append(wrap);
	}

	/**
	 * ����Ĭ����ʽ׷���ı�
	 *
	 * @param content
	 * @return
	 */
	public SimpleTextPane append(String content) {
		return append(content, new SimpleAttributeSet());
	}

	/**
	 * ����ָ����ɫ׷���ı�
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
	 * ����ָ���ֺ�׷���ı�
	 *
	 * @param content
	 * @param fontSize
	 * @return
	 */
	public SimpleTextPane append(String content, int fontSize) {
		if (fontSize < 9)//������С����Ϊ9�ţ���Ȼ�Ϳ�������
			fontSize = 9;
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, fontSize);
		return append(content, attr);
	}

	/**
	 * �Ƿ����б��׷���ı�
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
	 * ���Զ�������׷���ı�
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