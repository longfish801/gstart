/*
 * TaskExecutor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.taskexecutor;

import groovy.util.logging.Slf4j;
import io.github.longfish801.clmap.Clinfo;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.gstart.taskexecutor.notice.NoticeTask;
import io.github.longfish801.gstart.taskexecutor.notice.NoticeTaskCaller;
import io.github.longfish801.shared.ExchangeResource;
import org.apache.commons.io.FileUtils;

/**
 * {@link NoticeTaskCaller}を継承し、タスクを非同期実行します。<br>
 * タスクとは、実行状況を確認可能なタスクです。<br>
 * 具体的には{@link NoticeTask}を継承したタスクです。<br>
 * {@link startCheckThread()}を呼ぶと、タスクの実行状況スレッドを開始します。<br>
 * タスク実行で例外が発生したならばクロージャを呼びます。
 * @version 1.0.00 2016/09/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TaskExecutor extends NoticeTaskCaller {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(TaskExecutor.class);
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(TaskExecutor.class.classLoader);
	/** タスクの実行失敗時に実行するクロージャ */
	Clinfo excClos = null;
	
	/**
	 * タスクの実行失敗時に実行するクロージャ情報を格納します。<br>
	 * クロージャの引数としてタスク{@link NoticeTask}を渡します。
	 * @param clinfo クロージャ情報
	 */
	void setExceptionHandler(Clinfo clinfo){
		ArgmentChecker.checkNotNull('クロージャ情報', clinfo);
		excClos = clinfo;
	}
	
	/**
	 * ファイルを Groovyスクリプトとみなして非同期実行します。<br>
	 * スクリプトの格納フォルダに confフォルダがあればクラスパスに追加します。<br>
	 * また、libフォルダならびにそのサブフォルダにJARファイル（拡張子が "jar"）が
	 * あればクラスパスに追加します。
	 * @param file スクリプトファイル
	 * @return スクリプトの実行タスク
	 */
	ScriptTask runAsync(File file){
		ArgmentChecker.checkNotNull('スクリプトファイル', file);
		
		// クラスパスとするフォルダ一覧を作成します
		List<File> paths = [];
		cnst.classpath.dirs.each { String path ->
			File dir = new File(file.parentFile, path);
			if (dir.isDirectory()) paths << dir;
		}
		
		// クラスパスとするフォルダ、および JARファイルが存在すればクラスパスへ追加します
		for (File path in paths){
			shell.classLoader.addURL(path.toURL());
			if (path.isDirectory()){
				List fileList = FileUtils.listFiles(file.parentFile, cnst.classpath.extensions as String[], true);
				fileList.each { shell.classLoader.addURL(it.toURL()) }
			}
		}
		
		// スクリプト実行タスクを生成し、非同期実行します
		ScriptTask task = new ScriptTask(file.absolutePath, shell.parse(file));
		task.excClos = { NoticeTask noticeTask -> excClos.call(noticeTask) };
		return runAsync(task);
	}
}
