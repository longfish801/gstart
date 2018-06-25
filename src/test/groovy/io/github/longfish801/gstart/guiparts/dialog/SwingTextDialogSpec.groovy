/*
 * SwingTextDialogSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * SwingTextDialogクラスのテスト。
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwingTextDialogSpec extends Specification {
	def 'ダイアログの表示と終了ができること'(){
		given:
		SwingTextDialog dialog = new SwingTextDialog();
		
		when:
		dialog.show(null, 'TEST MESSAGE', 'TEST TEXT');
		then:
		dialog.swing.message.text == 'TEST MESSAGE';
		dialog.swing.textArea.text == 'TEST TEXT';
		
		when:
		dialog.closeWindow();
		then:
		noExceptionThrown();
	}
}
