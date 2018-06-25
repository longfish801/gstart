/*
 * AppTrayIcon.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.icon;

import groovy.util.logging.Slf4j;
import java.awt.Image;
import java.awt.TrayIcon;
import javax.imageio.ImageIO;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassSlurper;

/**
 * トレイアイコンを取得します。<br>
 * トレイアイコンは、リソース名"AppTrayIcon.png"で参照したイメージから作成します。
 * @version 1.0.00 2015/08/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AppTrayIcon {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(AppTrayIcon.class);
	/** トレイアイコン */
	static TrayIcon trayIcon = null;
	
	/**
	 * トレイアイコンを取得します。
	 * @return トレイアイコン
	 */
	static TrayIcon getIcon() {
		if (trayIcon == null){
			URL url = new ExistResource(AppTrayIcon.class).get(constants.extension);
			trayIcon = new TrayIcon(ImageIO.read(url));
		}
		return trayIcon;
	}
}
