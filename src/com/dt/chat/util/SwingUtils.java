package com.dt.chat.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.window.Window;

import com.dt.chat.ui.theme.NimbusTheme;
import com.dt.chat.ui.theme.SwingTheme;
import com.dt.chat.ui.theme.Theme;
import com.dt.chat.ui.theme.WinTheme;
/**
 * ���������
 * @author �򺣱�
 * @date 2019/1/3
 */
public class SwingUtils {
	private static Map<String, Theme> themes = new HashMap<>();

	static {
		Theme swing = new SwingTheme();
		Theme win = new WinTheme();
		Theme nimbus = new NimbusTheme();
		themes.put(Theme.DEFAULT, swing);
		themes.put(Theme.WINDOWS, win);
		themes.put(Theme.NIMBUS, nimbus);
	}


	/**
	 * �����򴰿��ƶ�����Ļ���в���ʾ
	 *
	 * @param window
	 */
	public static void moveWindowToCenterAndShow(Window window) {
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = window.getSize();
		int x = (screen.width - size.width) / 2;
		int y = (screen.height - size.height) / 2;
		window.setLocation(x, y);
		window.setVisible(true);
		window.requestFocus();
	}

	/**
	 * ��ձ������
	 *
	 * @param t
	 */
	public static void clearTable(JTable t) {
		if (t.getModel() instanceof DefaultTableModel) {
			((DefaultTableModel) t.getModel()).setRowCount(0);
		}
	}

	/**
	 * Ϊ��ť��˵�ע���¼�����������ӿ�ݼ�
	 *
	 * @param button
	 * @param action
	 * @param enterKey
	 */
	public static void registerEventHandler(AbstractButton button, KeyStroke enterKey, Action action) {
		button.addActionListener(action);// ��Ӽ�����
		// ��ӿ�ݼ�
		button.getActionMap().put(button.getActionCommand(), action);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, button.getActionCommand());
	}

	/**
	 * ���õ�ǰ����ΪlookAndFeel
	 * @param lookAndFeel
	 */
	public static void setTheme(String lookAndFeel) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ��ǰ����ı���
	 * @param currentTheme
	 * @return
	 */
	public static double getThemeRate(String currentTheme){
		return themes.get(currentTheme).getRate();
	}
}
