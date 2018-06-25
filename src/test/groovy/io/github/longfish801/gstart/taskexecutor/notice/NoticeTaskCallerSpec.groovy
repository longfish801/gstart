/*
 * NoticeTaskCallerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor.notice;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;
import spock.lang.Timeout;

/**
 * NoticeTaskCallerクラスのテスト。
 * @version 1.0.00 2017/06/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class NoticeTaskCallerSpec extends Specification {
	@Timeout(10)
	def '通知タスクを非同期実行します'(){
		given:
		NoticeTaskCaller caller = new NoticeTaskCaller();
		NoticeTask task = null;
		
		when: 'タスクを実行して戻り値が返ること'
		task = caller.runAsync(new SomeTask());
		task.thread.join();
		then:
		task.sign == 'SomeTask';
		task.ret == 'Hello, SomeTask';
		
		when: '引数チェックがされること'
		caller.runAsync(null);
		then:
		thrown(IllegalArgumentException);
		
		when: '識別子と引数の指定ができること'
		task = caller.runAsync(new ArgTask(sign: 'arg'), 'Hello');
		task.thread.join();
		then:
		task.sign == 'arg';
		task.ret == 'HelloHello';
		
		when: '実行中の複数のタスクがタスク一覧に格納されていること'
		NoticeTask task1 = caller.runAsync(new LongTask(sign: 'task1'));
		NoticeTask task2 = caller.runAsync(new LongTask(sign: 'task2'));
		NoticeTask task3 = caller.runAsync(new LongTask(sign: 'task3'));
		then:
		caller.taskList.collect { it.sign } == [ 'task1', 'task2', 'task3' ];
		
		when: '全タスク実行終了時にタスク一覧が空であること'
		task1.thread.join();
		task2.thread.join();
		task3.thread.join();
		then:
		caller.taskList.size() == 0;
		
		when: '処理成功時にクロージャが呼ばれること'
		task = caller.runAsync(new SomeTask(scsClos : { it.ret = 'Overwrite' }));
		task.thread.join();
		then:
		task.ret == 'Overwrite';
		
		when: '例外発生時にクロージャが呼ばれること'
		task = caller.runAsync(new ExcTask(excClos : { it.ret = 'Overwrite' }));
		task.thread.join();
		then:
		task.ret == 'Overwrite';
		task.exc.class.name == 'java.lang.NumberFormatException';
	}
	
	/**
	 * 文字列を返すタスクです。
	 */
	class SomeTask extends NoticeTask {
		/** {@inheritDoc} */
		@Override
		Object process(Object... args){
			return 'Hello, SomeTask';
		}
	}
	
	/**
	 * 引数の処理があるタスクです。
	 */
	class ArgTask extends NoticeTask {
		/** {@inheritDoc} */
		@Override
		Object process(Object... args){
			return (args[0] as String) * 2;
		}
	}
	
	/**
	 * 実行に時間を要するタスクです。
	 */
	class LongTask extends NoticeTask {
		/** {@inheritDoc} */
		@Override
		Object process(Object... args){
			Thread.sleep(1000);
			return null;
		}
	}
	
	/**
	 * 例外が発生するタスクです。
	 */
	class ExcTask extends NoticeTask {
		/** {@inheritDoc} */
		@Override
		Object process(Object... args){
			Integer.parseInt('abc');
			return null;
		}
	}
}
