/*
 * FxTextDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import io.github.longfish801.gstart.guiparts.ClipboardUtil;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassConfig;

/**
 * テキストダイアログです。<br>
 * 比較的長文の、複数行の文字列を表示するためのダイアログです。<br>
 * テキストエリア内に複数行の文字列を表示します。
 * @version 1.0.00 2017/07/20
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxTextDialog extends FxFreeSizeDialog implements Initializable {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(FxTextDialog.class);
	/** FXMLLoaderの生成に使用するリソース */
	static URL fxmlResource = new ExistResource(FxTextDialog.class).get('.fxml');
	/** テキスト */
	@FXML TextArea text;
	
	/**
	 * コンストラクタです。
	 */
	FxTextDialog(){
		// なにもしません
	}
	
	/**
	 * コンストラクタです。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param text テキスト（null、空文字、空白文字を許容）
	 * @see #setup(Window,String,String)
	 */
	FxTextDialog(Window owner, String headerText, String text){
		setup(owner, headerText, text);
	}
	
	/**
	 * 初期化します。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param text テキスト（null、空文字、空白文字を許容）
	 */
	void setup(Window owner, String headerText, String text){
		super.setup(owner);
		ArgmentChecker.checkNotBlank('説明', headerText);
		this.dialogPane.expanded = config.formVals.expanded;
		this.headerText = headerText;
		this.text.text = text;
	}
	
	/**
	 * ダイアログ終了時の処理をします。<br>
	 * フォームの値を設定ファイルに保存します。<br>
	 * ウィンドウを閉じます。
	 */
	void terminate() {
		config.formVals.expanded = this.dialogPane.expanded;
		config.formVals.text = text.text;
		super.terminate();
	}
	
	/** {@inheritDoc} */
	@Override
	void initialize(URL location, ResourceBundle resources){
		// なにもしません
	}
	
	/**
	 * テキストをクリップボードにコピーします。
	 * @param event ActionEvent
	 */
	void copy(ActionEvent event){
		ClipboardUtil.setText(text.text);
	}
}
