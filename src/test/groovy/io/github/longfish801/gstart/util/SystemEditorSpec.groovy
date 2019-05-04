/*
 * SystemEditorSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import spock.lang.Specification;

/**
 * SystemEditorクラスのテスト。
 * @version 1.0.00 2017/07/26
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SystemEditorSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', SystemEditorSpec.class);
	
/*
	def '指定されたファイルをエディタで開きます'(){
		when:
		SystemEditor.exec(new File(testDir, 'test.txt'));
		then:
		noExceptionThrown();
	}
*/
	
	def '行番号指定でエディタを起動するためのコマンドを返します'(){
		expect:
		SystemEditor.getCommand('test.txt', 123) == new File(testDir, "${InetAddress.localHost.hostName}.txt").getText();
	}
}
