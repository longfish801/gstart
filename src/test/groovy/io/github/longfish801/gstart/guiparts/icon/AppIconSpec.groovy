/*
 * AppIconSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.icon;

import groovy.util.logging.Slf4j;

import spock.lang.Specification;

import java.awt.Image;

/**
 * AppIconクラスのテスト。
 * 
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AppIconSpec extends Specification {
	def 'アイコン画像を取得できること'(){
		when:
		Image iconImage = AppIcon.getIcon();
		
		then:
		iconImage != null;
	}
}
