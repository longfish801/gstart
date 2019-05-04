/*
 * ScriptTaskSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor;

import groovy.util.logging.Slf4j;

import io.github.longfish801.gstart.taskexecutor.notice.NoticeTask;
import io.github.longfish801.shared.PackageDirectory;
import spock.lang.Specification;
import spock.lang.Timeout;

/**
 * ScriptTaskのテスト。
 * 
 * @version 1.0.00 2016/08/18
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ScriptTaskSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ScriptTaskSpec.class);
	/** 例外が発生したか否か */
	private boolean isErr = false;
	
	@Timeout(10)
	def 'タスクを実行し、結果を取得できること'(){
		given:
		TaskExecutor executor = new TaskExecutor();
		executor.setShell(new GroovyShell());
		
		when:
		ScriptTask task = executor.runAsync(new File(testDir, 'test01.groovy'));
		while (task.ret == null && isErr == false) Thread.sleep(1000);
		
		then:
		task.ret == 'This is TEST01';
	}
}
