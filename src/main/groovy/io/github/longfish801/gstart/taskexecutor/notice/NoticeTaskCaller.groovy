/*
 * NoticeTaskCaller.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor.notice;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * 通知タスクを非同期実行するためのクラスです。
 * @version 1.0.00 2017/06/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class NoticeTaskCaller {
	/** タスク一覧 */
	List<NoticeTask> taskList = Collections.synchronizedList(new ArrayList<NoticeTask>());
	
	/**
	 * 通知タスクを非同期実行します。<br>
	 * 受けつけたタスクはメンバ変数のタスク一覧に格納します。実行終了時に一覧から削除します。<br>
	 * タスクの実行スレッドをタスクのメンバ変数threadに格納します。<br>
	 * タスク識別子が nullの場合は通知タスクのクラスの単純名を使います。<br>
	 * 処理成功時は、戻り値をタスクのメンバ変数retに格納し、タスクを引数としてscsClosを呼びます。<br>
	 * 例外発生時は、例外をタスクのメンバ変数excに格納し、タスクを引数としてexcClosを呼びます。
	 * @param task タスク
	 * @param args 可変長の引数
	 * @return 通知タスクの実行スレッド
	 * @see NoticeTask
	 */
	NoticeTask runAsync(NoticeTask task, Object... args){
		ArgmentChecker.checkNotNull('task', task);
		taskList << task;
		if (task.sign == null) task.sign = task.class.simpleName;
		task.thread = Thread.start {
			try {
				task.ret = task.process(*args);
				if (task.scsClos != null) task.scsClos.call(task);
			} catch (exc) {
				task.exc = exc;
				if (task.excClos != null) task.excClos.call(task);
			} finally {
				taskList.remove(task);
			}
		}
		return task;
	}
}
