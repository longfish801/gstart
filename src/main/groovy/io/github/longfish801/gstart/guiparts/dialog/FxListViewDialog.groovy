/*
 * FxListViewDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListView;
import javafx.stage.Window;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.gstart.util.ClassConfig;

/**
 * 選択ダイアログです。<br>
 * ChoiceDialogはプルダウンで選択しますが、こちらはListViewから選択します。
 * @version 1.0.00 2017/08/08
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxListViewDialog extends FxFreeSizeDialog implements Initializable {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(FxListViewDialog.class);
	/** FXMLLoaderの生成に使用するリソース */
	static URL fxmlResource = ExchangeResource.url(FxListViewDialog.class, '.fxml');
	/** 選択リストビュー */
	@FXML ListView selectList;
	
	/**
	 * コンストラクタです。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param list 選択リスト
	 * @see #setup(Window,String,List)
	 */
	FxListViewDialog(Window owner, String headerText, List list){
		setup(owner, headerText, list);
	}
	
	/**
	 * 初期化します。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param list 選択リスト
	 */
	void setup(Window owner, String headerText, List list){
		setup(owner);
		ArgmentChecker.checkNotBlank('説明', headerText);
		ArgmentChecker.checkNotNull('選択リスト', list);
		this.headerText = headerText;
		this.list = list;
	}
	
	/**
	 * ダイアログ終了時の処理をします。<br>
	 * 選択された項目を結果として格納します。<br>
	 * 選択された項目を設定ファイルに保存します。
	 */
	@Override
	void terminate() {
		this.result = controller.selectList.selectionModel.selectedItems[0];
		config.formVals.headerText = this.headerText;
		config.formVals.selectedItems = controller.selectList.selectionModel.selectedItems as List;
		super.terminate();
	}
	
	/** {@inheritDoc} */
	@Override
	void initialize(URL location, ResourceBundle resources){
		selectList.onMouseClicked =  { MouseEvent event -> if (event.clickCount >= 2) terminate() }
	}
	
	/**
	 * 選択リストを設定します。
	 * @param list 選択リスト
	 */
	void setList(List list){
		selectList.items = FXCollections.observableArrayList(*list);
		config.formVals.selectedItems.each { selectList.selectionModel.select(it) }
	}
	
	/**
	 * 選択項目を末尾に追加し、最下端にスクロールします。
	 * @param item 選択項目
	 */
	void addItem(String item){
		selectList.items.add(item);
		selectList.scrollTo(selectList.items.size() - 1);
	}
}
