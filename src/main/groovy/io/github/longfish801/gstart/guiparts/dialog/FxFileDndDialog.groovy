/*
 * FxFileDndDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Window;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExistResource;

/**
 * ファイル指定ダイアログです。<br>
 * ドラッグ＆ドロップによりファイルを指定できます。
 * @version 1.0.00 2017/08/08
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxFileDndDialog extends FxListViewDialog {
	/** FXMLLoaderの生成に使用するリソース */
	static URL fxmlResource = new ExistResource(FxFileDndDialog.class).get('.fxml');
	
	/**
	 * コンストラクタです。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param list 選択リスト
	 * @see #setup(Window,String,List)
	 */
	FxFileDndDialog(Window owner, String headerText, List list){
		super(owner, headerText, list);
	}
	
	/** {@inheritDoc} */
	@Override
	void terminate() {
		super.terminate();
		this.result = selectList.items.collect { new File(it) };
	}
	
	/** {@inheritDoc} */
	@Override
	void initialize(URL location, ResourceBundle resources){
		selectList.onDragOver = { DragEvent event -> handleDragOver(event) }
		selectList.onDragDropped = { DragEvent event -> handleDropped(event) }
	}
	
	/**
	 * 選択リストへのドラッグオーバー時の処理です。
	 * @param event DragEvent
	 */
	void handleDragOver(DragEvent event){
		if (event.dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY);
		event.consume();
	}
	
	/**
	 * 選択リストへのドラッグドロップ時の処理です。
	 * @param event DragEvent
	 */
	void handleDropped(DragEvent event){
		if (event.dragboard.hasFiles()) event.dragboard.files.each { File file ->
			if (!selectList.items.contains(file.absolutePath)) this.addItem(file.absolutePath);
		}
		event.setDropCompleted(event.dragboard.hasFiles());
		event.consume();
	}
	
	/**
	 * 選択リストを空にします。
	 * @param event ActionEvent
	 */
	void clear(ActionEvent event){
		selectList.items.clear();
	}
}
