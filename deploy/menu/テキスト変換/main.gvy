/*
 * main.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovy.util.logging.Slf4j;
import java.awt.Desktop;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import io.github.longfish801.clmap.Clmap;
import io.github.longfish801.clmap.ClmapServer;
import io.github.longfish801.gstart.guiparts.application.FreeSizeApplication;
import io.github.longfish801.gstart.guiparts.dialog.FxExceptionDialog;
import io.github.longfish801.gstart.guiparts.ClipboardUtil;
import io.github.longfish801.gstart.guiparts.dialog.FxDialog;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.gstart.util.ClassConfig;
import io.github.longfish801.gstart.util.ScriptHelper;

/**
 * テキスト変換アプリケーションです。<br>
 * 変換処理はクロージャマップで定義します。
 * @version 1.0.00 2017/07/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TextConvertApplication extends FreeSizeApplication implements Initializable {
	/** 処理内容定義ファイル */
	static final File fileClmap = new File(ScriptHelper.parentDir(TextConvertApplication.class), 'conf/clmap.tpac');
	/** ヘルプファイル */
	static final File fileHelp = new File(ScriptHelper.parentDir(TextConvertApplication.class), 'doc/help.html');
	/** 初期設定値 */
	static final String defConfig = '''\
		window {
			title = 'テキスト変換';	// ウィンドウタイトル
			locationX = 50.0;	// ウィンドウ表示位置X座標
			locationY = 50.0;	// ウィンドウ表示位置Y座標
			sizeW = 500.0;	// ウィンドウ横幅
			sizeH = 400.0;	// ウィンドウ縦幅
		}
		formVals {
			procIdx = 0;		// 処理内容プルダウンの選択位置
			targetText = '';	// 処理対象テキストエリアの値
			resultText = '';	// 処理結果テキストエリアの値
		}'''.stripIndent();
	/** ClassConfig */
	static ClassConfig config = ClassConfig.newInstance(TextConvertApplication.class, defConfig);
	/** Clmap */
	Clmap clmap = null;
	/** 変換ボタン押下時の処理 */
	ConvertService service = new ConvertService();
	/** ステータスラベル */
	@FXML Label status;
	/** 処理内容選択リスト */
	@FXML ComboBox procList;
	/** 処理対象文字列 */
	@FXML TextArea targetText;
	/** 処理結果文字列 */
	@FXML TextArea resultText;
	
	/**
	 * メインメソッドです。
	 * @param arg コマンドライン引数
	 */
	static void main(args){
		LOG.info('テキスト変換アプリケーションを起動します。');
		try {
			new TextConvertApplication().show();
		} catch (exc){
			LOG.error('テキスト変換アプリケーションの起動に失敗しました。');
			throw exc;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	void close() {
		config.formVals.targetText = controller.targetText.text;
		config.formVals.resultText = controller.resultText.text;
		config.formVals.procIdx = controller.procList.selectionModel.selectedIndex;
		super.close();
	}
	
	/**
	 * 各フォームの初期値を設定します。
	 * @param location URL
	 * @param resources ResourceBundle
	 */
	@Override
	void initialize(URL location, ResourceBundle resources) {
		clmap = new ClmapServer().soak(fileClmap).getAt('clmap:テキスト変換');
		List clNames = clmap.getClosureNames('default');
		procList.items.addAll(*clNames);
		procList.setStyle('-fx-font: 10pt "Yu Gothic UI Regular";');
		procList.selectionModel.select(clNames[config.formVals.procIdx]);
		targetText.text = config.formVals.targetText;
		resultText.text = config.formVals.resultText;
	}
	
	/**
	 * 処理結果文字列をクリップボードにコピーします。
	 * @param event ActionEvent
	 */
	void copy(ActionEvent event){
		ClipboardUtil.setText(resultText.text);
	}
	
	/**
	 * 処理内容定義ファイルを開きます。
	 * @param event ActionEvent
	 */
	void edit(ActionEvent event){
		Desktop.getDesktop().open(fileClmap);
	}
	
	/**
	 * ヘルプファイルを開きます。
	 * @param event ActionEvent
	 */
	void help(ActionEvent event){
		Desktop.getDesktop().open(fileHelp);
	}
	
	/**
	 * ウィンドウを閉じます。
	 * @param event ActionEvent
	 */
	void close(ActionEvent event){
		this.close();
	}
	
	/**
	 * 処理内容を再読込します。
	 * @param event ActionEvent
	 */
	void reload(ActionEvent event){
		try {
			clmap = new ClmapServer().soak(fileClmap).getAt('clmap:テキスト変換');
			List clNames = clmap.getClosureNames('default');
			int selectedIdx = (procList.selectionModel.selectedIndex >= clNames.size())? 0 : procList.selectionModel.selectedIndex;;
			procList.items.clear();
			procList.items.addAll(*clNames);
			procList.selectionModel.select(clNames[selectedIdx]);
			controller.status.text = '処理内容の編集を反映しました。';
		} catch (exc){
			LOG.error('処理内容の読込時に問題が発生しました。', exc);
			FxDialog.exceptionDialog('処理内容の読込時に問題が発生しました。', exc);
		}
	}
	
	/**
	 * 変換処理をします。
	 * @param event ActionEvent
	 */
	void convert(ActionEvent event){
		service.restart();
	}
	
	/**
	 * 変換ボタン押下時のサービスです。
	 */
	class ConvertService extends Service<String>{
		/** {@inheritDoc} */
		@Override
		protected Task<String> createTask() {
			return new ConvertTask();
		}
	}
	
	/**
	 * 変換ボタン押下時のタスクです。<br>
	 * テキストエリアに指定された文字列を変換します。<br>
	 * 処理に成功した場合、結果をテキストエリアに表示します。
	 */
	class ConvertTask extends Task<String>{
		/** {@inheritDoc} */
		@Override
		protected String call(){
			// 処理結果欄を空欄にし、編集不可にします
			Platform.runLater {
				controller.resultText.text = '';
				controller.resultText.editable = false;
				controller.status.text = '変換中です。';
			}
			
			// 変換します
			List clNames = clmap.getClosureNames('default');
			return clmap.cl("default#${clNames[controller.procList.selectionModel.selectedIndex]}").call(controller.targetText.text);
		}
		
		/** {@inheritDoc} */
		@Override
		protected void succeeded(){
			super.succeeded();
			Platform.runLater {
				// 処理結果をテキストエリアに表示し、編集可にします
				controller.resultText.text = getValue();
				controller.resultText.editable = true;
				controller.status.text = '変換に成功しました。';
			}
		}
		
		/** {@inheritDoc} */
		@Override
		protected void failed() {
			super.failed();
			LOG.error('変換時に問題が発生しました。', exception);
			Platform.runLater {
				controller.resultText.editable = true;
				controller.status.text = '変換時に問題が発生しました。';
				new FxExceptionDialog(null, '変換時に問題が発生しました。', exception).showAndWait();
			}
		}
	}
}
