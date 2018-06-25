/*
 * ScriptTask.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor;

import groovy.util.logging.Slf4j;
import io.github.longfish801.gstart.taskexecutor.notice.NoticeTask;

/**
 * Groovyスクリプトの実行タスクです。
 * @version 1.0.00 2015/12/11
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ScriptTask extends NoticeTask {
	/** スクリプト */
	Script script = null;
	
	/**
	 * コンストラクタ。
	 * @param sign タスク識別情報
	 * @param script Script
	 */
	ScriptTask(String sign, Script script){
		this.sign = sign;
		this.script = script;
	}
	
	/** {@inheritDoc} */
	@Override
	Object process(Object... args){
		return script.run();
	}
}
