/*
 * ClinfoTaskSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor;

import groovy.util.logging.Slf4j;
import io.github.longfish801.clmap.Clinfo;
import io.github.longfish801.clmap.Clmap;
import io.github.longfish801.clmap.ClmapServer;
import io.github.longfish801.gstart.taskexecutor.notice.NoticeTask;
import spock.lang.Specification;
import spock.lang.Timeout;

/**
 * ClinfoTaskのテスト。
 * @version 1.0.00 2016/08/18
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClinfoTaskSpec extends Specification {
	@Timeout(10)
	def 'タスクを実行し、結果を取得できること'(){
		given:
		String clmapText = '''\
			#! clmap testRun001
			#> map run001
			#>> closure
			return 'This is TEST001';
		'''.stripIndent();
		TaskExecutor executor = new TaskExecutor();
		executor.setShell(new GroovyShell());
		Clmap clmap = new ClmapServer().soak(clmapText).getAt('clmap:testRun001');
		ClinfoTask task = new ClinfoTask(clmap.cl('run001'));
		
		when:
		executor.runAsync(task);
		while (task.ret == null) Thread.sleep(1000);
		
		then:
		task.ret == 'This is TEST001';
	}

	@Timeout(10)
	def 'タスクを実行し、例外発生時に例外オブジェクトを取得できること'(){
		given:
		String clmapText = '''\
			#! clmap testRun002
			#> map run002
			#>> closure
			throw new Exception('This is TEST002');
		'''.stripIndent();
		TaskExecutor executor = new TaskExecutor();
		executor.setShell(new GroovyShell());
		Clmap clmap = new ClmapServer().soak(clmapText).getAt('clmap:testRun002');
		ClinfoTask task = new ClinfoTask(clmap.cl('run002'));
		
		when:
		executor.runAsync(task);
		while (task.exc == null) Thread.sleep(1000);
		
		then:
		task.exc.cause.message == 'This is TEST002';
	}
}
