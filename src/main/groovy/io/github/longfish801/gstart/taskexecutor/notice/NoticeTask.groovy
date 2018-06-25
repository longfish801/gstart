/*
 * NoticeTask.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor.notice;

import java.lang.Thread as JavaThread;
import groovy.util.logging.Slf4j;

/**
 * 通知タスクです。<br>
 * 実行状況や結果を Closureで通知します。<br>
 * {@link NoticeTaskCaller}で実行することを想定しています。
 * @version 1.0.00 2017/06/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
abstract class NoticeTask {
	/** 識別子 */
	String sign = null;
	/** 戻り値 */
	Object ret = null;
	/** Exception */
	Exception exc = null;
	/** 処理成功時に呼ぶクロージャ（引数は本タスク） */
	Closure scsClos = null;
	/** 例外発生時に呼ぶクロージャ（引数は本タスク） */
	Closure excClos = null;
	/** 本タスクの実行スレッド */
	JavaThread thread = null;
	
	/**
	 * タスクを実行します。<br>
	 * 本メソッドをオーバーライドし、実体となる処理を記述してください。
	 * @param args 可変長の引数
	 * @return タスク実行結果
	 */
	abstract Object process(Object... args);
}
