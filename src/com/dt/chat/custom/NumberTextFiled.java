
package com.dt.chat.custom;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * description: 扩展标准文本输入框，限制只能输入数字
 * @author 田何理
 * @date 2018/12/29
 *  
 */
public class NumberTextFiled extends JTextField {

	@Override
	protected Document createDefaultModel() {
		return new NumberDocument();
	}
	
	class NumberDocument extends PlainDocument{
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if(str==null) {
				return;
			}
			char[] chars = str.toCharArray();
			StringBuffer digits=new StringBuffer(chars.length);
					
			for (char c : chars) {//将原输入的字符，只保留数字
				if(c>='0'&&c<='9') {
					digits.append(c);
				}
			}
			super.insertString(offs, digits.toString(), a);//插入只有数字的字符到文本框
		}
	}

}