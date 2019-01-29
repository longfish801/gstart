/*
 * FxFreeSizeDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.gstart.util.ClassConfig;

/**
 * サイズを変更可能で、再表示時に前回表示時と同じ大きさと位置で表示するダイアログです。<br>
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
 * @version 1.0.00 2017/08/09
 * @author io.github.longfish801
 */
@Slf4j('LOG')
abstract class FxFreeSizeDialog extends Dialog {
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
	 * FXMLLoaderの生成に使用するリソースを返します。<br>
	 * リソース名"[クラス名].fxml"で参照可能な FXMLファイルへの URLを返します。
	 * @return ClassConfig
	 */
	static URL getFxmlResource(){
		throw new UnsupportedOperationException('FXMLLoaderの生成に使用するリソースの返却を実装していません。');
		return null;
	}
	
	/**
	 * 初期化します。
	 * @param owner 親ウィンドウ
	 */
	void setup(Window owner){
		FXMLLoader loader = new FXMLLoader(this.fxmlResource);
		loader.controller = controller;
		this.dialogPane = loader.load() as DialogPane;
		if (owner != null) this.initOwner(owner);
		(this.dialogPane.scene.window as Stage).icons.add(AppIcon.fxIcon);
		this.onHidden = { DialogEvent event -> terminate() }
		this.resizable = true;
		this.title = config.window.title;
		this.x = config.window.locationX;
		this.y = config.window.locationY;
		this.dialogPane.prefWidth = config.window.sizeW;
		this.dialogPane.prefHeight = config.window.sizeH;
	}
	
	/**
	 * ダイアログ終了時の処理をします。
	 */
	void terminate(){
		config.window.title = this.title;
		config.window.locationX = this.x;
		config.window.locationY = this.y;
		config.window.sizeW = this.width;
		config.window.sizeH = this.height;
		config.saveConfig();
	}
}
