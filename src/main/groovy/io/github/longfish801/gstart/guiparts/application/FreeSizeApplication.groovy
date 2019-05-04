/*
 * FreeSizeApplication.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.application;

import groovy.util.logging.Slf4j;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.gstart.util.ClassConfig;

/**
 * サイズを変更可能で、再表示時に前回表示時と同じ大きさと位置で表示するアプリケーションです。<br>
 * Controllerの FXMLファイルがリソース名"[クラス名].fxml"で参照可能である必要があります。<br>
 * ウィンドウのタイトル、表示位置、大きさについて、ClassConfigで以下のように指定される必要があります。</p>
 * <pre>
 * window {
 *     title = 'テキスト変換';	// ウィンドウタイトル
 *     locationX = 50.0;	// ウィンドウ表示位置X座標
 *     locationY = 50.0;	// ウィンドウ表示位置Y座標
 *     sizeW = 500.0;	// ウィンドウ横幅
 *     sizeH = 400.0;	// ウィンドウ縦幅
 * }
 * </pre>
 * <p>ウィンドウのアイコンには {@link AppIcon#fxIcon()}を使用します。
 * @version 1.0.00 2017/07/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
abstract class FreeSizeApplication extends Application {
	/** Stage */
	Stage stage = null;
	/** アプリケーションを表示しているか否か */
	boolean started = false;
	
	/**
	 * Controllerを返します。<br>
	 * 自インスタンス（this）を返します。
	 * @return Controller
	 */
	Initializable getController(){
		return this;
	}
	
	/**
	 * ClassConfigを返します。
	 * @return ClassConfig
	 */
	static ClassConfig getConfig(){
		throw new UnsupportedOperationException('ClassConfigの返却を実装していません。');
		return null;
	}
	
	/**
	 * アプリケーションを表示します。<br>
	 * 事前に{@link Platform#setImplicitExit(boolean)}で falseが設定されている必要があります。
	 * @param arg コマンドライン引数
	 * @throws IllegalStateException implicitExit属性がfalseに設定されていません。
	 */
	void show(){
		if (started) return;
		started = true;
		if (Platform.implicitExit) throw new IllegalStateException('implicitExit属性がfalseに設定されていません。');
		if (Platform.isFxApplicationThread()){
			start(new Stage());
		} else {
			Platform.runLater { start(new Stage()) }
		}
	}
	
	/**
	 * アプリケーションを初期化ならびに表示します。<br>
	 * 本メソッドは直接呼ばず、{@link #show()}を利用してください。<br>
	 * FXMLを読みこんで、ウィンドウのタイトル、表示位置、大きさ、アイコン、
	 * ウィンドウを閉じるときの処理を設定します。
	 * @param stage Stage
	 */
	@Override
	void start(Stage stage) {
		if (config == null) throw new IllegalStateException('configが指定されていません。');
		if (controller == null) throw new IllegalStateException('controllerが指定されていません。');
		FXMLLoader loader = new FXMLLoader(ExchangeResource.url(this.getClass(), '.fxml'));
		loader.controller = controller;
		stage.scene = new Scene(loader.load() as Pane, config.window.sizeW, config.window.sizeH);
		stage.icons.add(AppIcon.fxIcon);
		stage.title = config.window.title;
		stage.scene.window.x = config.window.locationX;
		stage.scene.window.y = config.window.locationY;
		stage.onCloseRequest = { WindowEvent event ->
			close();
			event.consume();
		}
		stage.show();
		this.stage = stage;
	}
	
	/**
	 * アプリケーション終了時の処理をします。<br>
	 * ウィンドウのタイトル、表示位置、大きさを保存します。
	 */
	void close() {
		if (!started) return;
		started = false;
		config.window.title = stage.title;
		config.window.locationX = stage.scene.window.x;
		config.window.locationY = stage.scene.window.y;
		config.window.sizeW = stage.scene.width;
		config.window.sizeH = stage.scene.height;
		config.saveConfig();
		if (Platform.isFxApplicationThread()){
			stop();
		} else {
			Platform.runLater { stop() }
		}
	}
	
	/**
	 * ウィンドウを閉じます。<br>
	 * 本メソッドは直接呼ばず、{@link #close()}を利用してください。
	 */
	@Override
	void stop() {
		stage.scene.window.hide();
	}
}
