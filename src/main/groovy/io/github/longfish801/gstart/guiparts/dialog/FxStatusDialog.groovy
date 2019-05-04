/*
 * FxStatusDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Window;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;

/**
 * 処理状況ダイアログです。
 * @version 1.0.00 2017/08/08
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxStatusDialog extends FxListViewDialog {
	/** FXMLLoaderの生成に使用するリソース */
	static URL fxmlResource = ExchangeResource.url(FxStatusDialog.class, '.fxml');
	
	/**
	 * コンストラクタです。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param list 選択リスト
	 * @see #setup(Window,String,List)
	 */
	FxStatusDialog(Window owner, String headerText, List list){
		super(owner, headerText, list);
	}
	
	/** {@inheritDoc} */
	@Override
	void initialize(URL location, ResourceBundle resources){
		dialogPane.buttonTypes.add(ButtonType.CLOSE);
		dialogPane.lookupButton(ButtonType.CLOSE).disable = true;
	}
	
	/**
	 * 処理状況ダイアログの表示を開始すると同時に、そのインスタンスを返します。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param list 選択リスト
	 */
	static FxStatusDialog start(Window owner, String headerText, List list){
		BlockingQueue<FxStatusDialog> queue = new ArrayBlockingQueue<FxStatusDialog>(1);
		FxStatusDialog dialog = null;
		Platform.runLater {
			try {
				dialog = new FxStatusDialog(owner, headerText, list);
				dialog.show();
			} catch (exc){
				FxStatusDialog.LOG.error('処理状況ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put(dialog);
			}
		}
		return queue.take();
	}
	
	/**
	 * 処理状況のメッセージを末尾に追加します。
	 * @param statusMessage 処理状況のメッセージ
	 */
	FxStatusDialog leftShift(String statusMessage){
		Platform.runLater { addItem(statusMessage) }
		return this;
	}
	
	/**
	 * 処理状況の表示を終了します。<br>
	 * ボタンを有効にします。
	 */
	void end(){
		Platform.runLater { dialogPane.lookupButton(ButtonType.CLOSE).disable = false }
	}
}
