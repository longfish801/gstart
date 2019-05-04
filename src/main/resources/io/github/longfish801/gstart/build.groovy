/*
 * gstart.dsl
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import io.github.longfish801.clmap.Clmap;
import io.github.longfish801.clmap.ClmapServer;
import io.github.longfish801.gstart.GstartBuilder;
import io.github.longfish801.gstart.guiparts.dialog.SwingExceptionDialog;
import io.github.longfish801.shared.ExchangeResource;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

Logger LOG = LoggerFactory.getLogger('io.github.longfish801');

// 未キャッチの例外を取得するハンドラを設定します
Thread.defaultUncaughtExceptionHandler = { Thread thread, Throwable exc ->
	LOG.error('未キャッチの例外を取得しました。exc={} thread={}', exc.toString(), thread.toString(), exc);
	new SwingExceptionDialog().show(null, exc, '未キャッチの例外を取得しました。');
}

// JavaFXを初期化します
new JFXPanel();
Platform.setImplicitExit(false);

// Swing向けにルック＆フィールを設定します
UIManager.setLookAndFeel('com.sun.java.swing.plaf.windows.WindowsLookAndFeel');

// 問題発生時の解析情報として、システムプロパティを出力します
LOG.info('System Properties={}', System.getProperties().toString());

try {
	// クロージャマップを作成します
	Clmap clmap = new ClmapServer().soak(ExchangeResource.url(GstartBuilder.class, 'build.tpac')).getAt('clmap:gstart');
	// 各機能を設定します
	GstartBuilder builder = new GstartBuilder();
	builder.gstart {
		// タスク実行
		addFunction(
			key: 'taskexecutor',
			class: 'io.github.longfish801.gstart.taskexecutor.TaskExecutor',
			initial: { 
				setExceptionHandler clmap.cl('task#exception');
			}
		);
		
		// タスクトレイランチャー
		addFunction(
			key: 'traylauncher',
			class: 'io.github.longfish801.gstart.traylauncher.TrayLauncher',
			initial: {
				setActionClinfo clmap.cl('traylauncher');
				setActionDoubleClicked 'menu#showconsole';
				setPopupMenu(
					menuItem(name: '終了', combi: 'gstart#stop'),
					menu(name:'参照', items: [
						menuItem(name: 'ルートフォルダ', combi: 'menu#open'),
						menuItem(name: 'アプリデータ', combi: 'menu#appdir'),
						menuItem(name: 'ログ', combi: 'menu#log')
					]),
					menuSeparator(),
					menuItem(name: '再読込', combi: 'gstart#reload'),
					menuItem(name: 'コンソール', combi: 'menu#showconsole'),
					menuItem(name: 'ヘルプ', combi: 'menu#help'),
					menuSeparator(),
					menuScript(dir: new File('menu'), combi: 'script#run')
				);
			}
		);
		
		// スクリプト実行コンソール
		addFunction(
			key: 'scriptconsole',
			class: 'io.github.longfish801.gstart.scriptconsole.ScriptConsole',
			initial: {
				setActionClinfo clmap.cl('scriptconsole');
				setScriptDir new File('menu');
				setActionClose 'menu#stopconsole';
				setActionDoubleClicked 'script#run';
				setMenuBar(
					menu(name:'ファイル', items: [
						menuItem(name: '実行', combi: 'script#run', accelerator: 'Ctrl+Enter'),
						menuItem(name: 'エディタで開く', combi: 'script#editor'),
						menuItem(name: 'GroovyConsoleで開く', combi: 'script#console'),
						menuItem(name: '終了', combi: 'menu#stopconsole', accelerator: 'Ctrl+W')
					]),
					menu(name:'編集', items: [
						menuItem(name: '再読込', combi: 'gstart#reload', accelerator: 'Ctrl+R'),
						menuItem(name: 'ルートフォルダ', combi: 'menu#open'),
						menuItem(name: 'アプリデータ', combi: 'menu#appdir'),
						menuItem(name: 'ログ参照', combi: 'menu#log', accelerator: 'Ctrl+L')
					]),
					menu(name:'ヘルプ', items: [
						menuItem(name: 'ヘルプ', combi: 'menu#help', accelerator: 'Ctrl+H'),
						menuItem(name: 'バージョン', combi: 'menu#version')
					])
				);
				setContextMenu(
					menuItem(name: '実行', combi: 'script#run'),
					menuItem(name: 'エディタで開く', combi: 'script#editor'),
					menuItem(name: 'GroovyConsoleで開く', combi: 'script#console')
				);
			}
		);
	}
	
	clmap.properties['funcs'] = builder;
	clmap.properties['args'] = args;
	try {
		clmap.cl('gstart#start').call();
	} catch (Throwable exc){
		clmap.cl('gstart#stop').call();
		throw exc;
	}
	
} catch (Throwable exc){
	LOG.error('スタートアップ時に問題が生じました。', exc);
	new SwingExceptionDialog().show(null, exc, 'スタートアップ時に問題が生じました。');
	// JavaFXアプリケーションスレッドを停止します
	Platform.exit();
}
