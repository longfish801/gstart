/*
 * SwingExceptionDialogSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * SwingExceptionDialogクラスのテスト。
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwingExceptionDialogSpec extends Specification {
	def '例外ダイアログの表示と終了ができること'(){
		given:
		SwingExceptionDialog dialog = new SwingExceptionDialog();
		
		when:
		dialog.show(null, new IOException('This is TEST.'), 'TEST MESSAGE');
		then:
		noExceptionThrown();
		
		when:
		dialog.closeWindow();
		then:
		noExceptionThrown();
	}
}
