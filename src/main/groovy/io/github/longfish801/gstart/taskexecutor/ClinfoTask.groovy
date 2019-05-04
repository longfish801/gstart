/*
 * ClinfoTask.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor;

import groovy.util.logging.Slf4j;
import io.github.longfish801.clmap.Clinfo;
import io.github.longfish801.gstart.taskexecutor.notice.NoticeTask;

/**
 * Clinfoの実行タスクです。
 * @version 1.0.00 2016/03/25
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClinfoTask extends NoticeTask {
	/** Clinfo */
	Clinfo clinfo = null;
	
	/**
	 * コンストラクタ。
	 * @param clinfo Clinfo
	 */
	ClinfoTask(Clinfo clinfo) {
		this.sign = clinfo.combiKey;
		this.clinfo = clinfo;
	}
	
	/** {@inheritDoc} */
	@Override
	Object process(Object... args){
		return clinfo.call(*args);
	}
}
