package com.dt.chat.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.window.Window;

import com.dt.chat.ui.theme.NimbusTheme;
import com.dt.chat.ui.theme.SwingTheme;
import com.dt.chat.ui.theme.Theme;
import com.dt.chat.ui.theme.WinTheme;
/**
 * 界面的美化
 * @author 向海彪
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
	 * 将程序窗口移动到屏幕正中并显示
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
	 * 清空表格数据
	 *
	 * @param t
	 */
	public static void clearTable(JTable t) {
		if (t.getModel() instanceof DefaultTableModel) {
			((DefaultTableModel) t.getModel()).setRowCount(0);
		}
	}

	/**
	 * 为按钮或菜单注册事件监听，并添加快捷键
	 *
	 * @param button
	 * @param action
	 * @param enterKey
	 */
	public static void registerEventHandler(AbstractButton button, KeyStroke enterKey, Action action) {
		button.addActionListener(action);// 添加监听器
		// 添加快捷键
		button.getActionMap().put(button.getActionCommand(), action);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, button.getActionCommand());
	}

	/**
	 * 设置当前主题为lookAndFeel
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
	 * 获取当前主题的倍率
	 * @param currentTheme
	 * @return
	 */
	public static double getThemeRate(String currentTheme){
		return themes.get(currentTheme).getRate();
	}
}
