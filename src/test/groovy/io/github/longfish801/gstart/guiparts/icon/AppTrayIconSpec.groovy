/*
 * AppTrayIconSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.icon;

import groovy.util.logging.Slf4j;

import java.awt.TrayIcon;

import spock.lang.Specification;

/**
 * AppTrayIconクラスのテスト。
 * 
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AppTrayIconSpec extends Specification {
	def 'トレイアイコンを取得できること'(){
		when:
		TrayIcon trayIcon = AppTrayIcon.getIcon();
		
		then:
		trayIcon != null;
	}
}
