/*
 * ScriptHelperSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import spock.lang.Specification;

/**
 * ScriptHelperのテスト。
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ScriptHelperSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ScriptHelperSpec.class);
	
	def 'スクリプトファイルのFileインスタンスを返します'(){
		when:
		File scriptFile = ScriptHelper.thisScript(ScriptHelperSpec.class);
		then:
		noExceptionThrown();
		
		when:
		ScriptHelper.thisScript(null);
		then:
		thrown(IllegalArgumentException);
		
		when:
		Object result = new GroovyShell().run(new File(testDir, 'testThis.groovy'), []);
		then:
		result == 'testThis.groovy';
	}
	
	def 'スクリプトファイルが格納されているフォルダのFileインスタンスを返します'(){
		when:
		File scriptDir = ScriptHelper.parentDir(ScriptHelperSpec.class);
		then:
		noExceptionThrown();
		
		when:
		Object result = new GroovyShell().run(new File(testDir, 'testParent.groovy'), []);
		then:
		result  == 'ScriptHelperSpec';
	}
}
