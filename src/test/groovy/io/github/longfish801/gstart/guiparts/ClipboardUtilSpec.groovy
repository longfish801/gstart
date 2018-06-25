/*
 * ClipboardUtilSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts;

import groovy.util.logging.Slf4j;

import spock.lang.Specification;

/**
 * ClipboardUtilクラスのテスト。
 * 
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClipboardUtilSpec extends Specification {
	def 'クリップボードへの文字列の設定と参照ができること'(){
		when:
		ClipboardUtil.setText('This is TEST');
		
		then:
		ClipboardUtil.getText() == 'This is TEST';
	}
}
