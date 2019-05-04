/*
 * main.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.2.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import groovy.util.logging.Slf4j;
import java.awt.Desktop;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import io.github.longfish801.gstart.guiparts.ClipboardUtil;
import io.github.longfish801.gstart.guiparts.application.FreeSizeApplication;
import io.github.longfish801.gstart.guiparts.dialog.FxDialog;
import io.github.longfish801.gstart.guiparts.dialog.FxExceptionDialog;
import io.github.longfish801.gstart.util.ClassConfig;
import io.github.longfish801.gstart.util.ScriptHelper;
import io.github.longfish801.yakumo.YmoScript;
import io.github.longfish801.yakumo.YmoDocument;
import io.github.longfish801.bltxt.BLtxt;
import org.apache.commons.io.FilenameUtils;

/**
 * yakumoアプリケーションです。
 * @version 1.0.00 2016/01/21
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YakumoApplication extends FreeSizeApplication implements Initializable {
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(YakumoApplication.class.classLoader);
	/** スクリプトフォルダ */
	static final File scriptDir = new File(ScriptHelper.parentDir(YakumoApplication.class), 'script');
	/** ClassConfig */
	ClassConfig config = ClassConfig.newInstance(YakumoApplication.class);
	/** 変換ボタン押下時の処理 */
	ConvertService serviceConvert = new ConvertService();
	/** 実行ボタン押下時の処理 */
	ExecService serviceExec = new ExecService();
	/** タブペイン */
	@FXML TabPane tabPane;
	/** ステータスラベル */
	@FXML Label status;
	/** 変換ボタン */
	@FXML Button convertButton;
	/** 処理方法 */
	@FXML ToggleGroup methodType;
	/** 処理方法：HTML化 */
	@FXML RadioButton methodTypeHtmlize;
	/** 処理方法：XML化 */
	@FXML RadioButton methodTypeXmlize;
	/** 処理方法：事前整形 */
	@FXML RadioButton methodTypeWash;
	/** 処理対象文字列 */
	@FXML TextArea targetText;
	/** 処理結果文字列 */
	@FXML TextArea resultText;
	/** 変換対象リスト */
	@FXML ListView targetList;
	/** 実行ボタン */
	@FXML Button execButton;
	
	/**
	 * メインメソッドです。
	 * @param arg コマンドライン引数
	 */
	static void main(args){
		LOG.info('BLtxtアプリケーションを起動します。');
		try {
			new YakumoApplication().show();
		} catch (exc){
			LOG.error('BLtxtアプリケーションの起動に失敗しました。');
			throw exc;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	void close() {
		config.formVals.tabIdx = tabPane.selectionModel.selectedIndex;
		config.formVals.methodType = methodType.selectedToggle.id;
		config.formVals.targetText = targetText.text;
		config.formVals.resultText = resultText.text;
		config.formVals.targetList = [];
		targetList.items.each { String path -> config.formVals.targetList << path }
		super.close();
	}
	
	/**
	 * 各フォームの初期値を設定します。
	 * @param location URL
	 * @param resources ResourceBundle
	 */
	@Override
	void initialize(URL location, ResourceBundle resources) {
		tabPane.selectionModel.select(config.formVals.tabIdx);
		methodType.toggles.each { it.selected = (it.id == config.formVals.methodType)? true : false }
		targetText.text = config.formVals.targetText;
		resultText.text = config.formVals.resultText;
		
		// 変換対象リストを初期化します
		targetList.items = FXCollections.observableArrayList(config.formVals.targetList);
		// ダブルクリックしたフォルダを変換対象リストから削除します
		targetList.onMouseClicked =  { MouseEvent event ->
			if (event.clickCount >= 2) targetList.items.remove(targetList.selectionModel.selectedIndex);
		}
	}
	
	/**
	 * ヘルプファイルを開きます。
	 * @param event ActionEvent
	 */
	void help(ActionEvent event){
		Desktop.desktop.open(new File(ScriptHelper.parentDir(YakumoApplication.class), 'doc/help.html'));
	}
	
	/**
	 * ウィンドウを閉じます。
	 * @param event ActionEvent
	 */
	void close(ActionEvent event){
		this.close();
	}
	
	/**
	 * 処理結果文字列をクリップボードにコピーします。
	 * @param event ActionEvent
	 */
	void copy(ActionEvent event){
		ClipboardUtil.setText(resultText.text);
	}
	
	/**
	 * 変換処理をします。
	 * @param event ActionEvent
	 */
	void convert(ActionEvent event){
		serviceConvert.restart();
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
			if (file.isDirectory() && !targetList.items.contains(file.absolutePath)){
				targetList.items.add(file.absolutePath);
			}
		}
		event.setDropCompleted(event.dragboard.hasFiles());
		event.consume();
	}
	
	/**
	 * スクリプトを実行します。
	 * @param event ActionEvent
	 */
	void exec(ActionEvent event){
		serviceExec.restart();
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
				resultText.editable = false;
				convertButton.disable = true;
				controller.status.text = '変換中です。';
			}
			
			// 事前整形スクリプトによる変換結果を返すクロージャです
			Closure washText = { String targetText ->
				YmoScript ymoScript = new YmoScript();
				ymoScript.configure('_bltxt');
				return ymoScript.engine.washServer.getAt('washsh:_bltxt').wash(targetText);
			}
			
			// テキストを変換します
			String result = '';
			switch (methodType.selectedToggle.id){
				case methodTypeWash.id:
					result = washText.call(targetText.text);
					break;
				case methodTypeXmlize.id:
					result = new BLtxt(washText.call(targetText.text)).toXml();
					break;
				case methodTypeHtmlize.id:
					result = YmoScript.convert('_bltxt', '_html', targetText.text);
					break;
				default:
					throw new InternalError("処理方法が選択されていません。");
					break;
			}
			return result;
		}
		
		/** {@inheritDoc} */
		@Override
		protected void succeeded(){
			super.succeeded();
			Platform.runLater {
				// 処理結果を表示し、編集可にします
				resultText.editable = true;
				resultText.text = getValue();
				convertButton.disable = false;
				status.text = '変換に成功しました。';
			}
		}
		
		/** {@inheritDoc} */
		@Override
		protected void failed() {
			super.failed();
			LOG.error('変換時に問題が発生しました。', exception);
			Platform.runLater {
				resultText.editable = true;
				resultText.text = '';
				convertButton.disable = false;
				controller.status.text = '変換時に問題が発生しました。';
				new FxExceptionDialog(null, '変換時に問題が発生しました。', exception).showAndWait();
			}
		}
	}
	
	/**
	 * 実行ボタン押下時のサービスです。
	 */
	class ExecService extends Service<Boolean>{
		/** {@inheritDoc} */
		@Override
		protected Task<Boolean> createTask() {
			return new ExecTask();
		}
	}
	
	/**
	 * 実行ボタン押下時のタスクです。<br>
	 * テキストフィールドに指定されたスクリプトを実行します。
	 */
	class ExecTask extends Task<Boolean>{
		/** {@inheritDoc} */
		@Override
		protected Boolean call(){
			// 処理結果欄を空欄にし、編集不可にします
			Platform.runLater {
				execButton.disable = true;
				controller.status.text = 'スクリプトを実行します。';
			}
			
			// 変換します
			if (targetList.selectionModel.selectedItems.size() == 0) throw new Exception("対象フォルダが選択されていません。");
			targetList.selectionModel.selectedItems.each { String targetPath ->
				File targetDir = new File(targetPath);
				if (!targetDir.isDirectory()) throw new Exception("指定されたフォルダが存在しません。 path=${targetPath}");
				List convertNames = targetDir.listFiles().findAll { it.isDirectory() && FilenameUtils.wildcardMatch(it.name, '_*') }.collect { it.name };
				if (convertNames.isEmpty()) throw new Exception("設定フォルダが存在しません。 path=${targetPath}");
				try {
					String convertName = null;
					if (convertNames.size() == 1){
						convertName = convertNames[0];
					} else {
						convertName = FxDialog.singleSelect('変換名を選択してください。', convertNames);
					}
					new YmoDocument(targetDir.canonicalFile).run(new File(targetDir, convertName));
				} catch (Error error){
					// Errorが発生した場合は例外に変換します
					LOG.error('実行時にエラーが発生しました。');
					throw new Exception(error.message, error);
				}
			}
			return true;
		}
		
		/** {@inheritDoc} */
		@Override
		protected void succeeded(){
			super.succeeded();
			Platform.runLater {
				// 処理に成功した旨を表示します
				execButton.disable = false;
				controller.status.text = '実行に成功しました。';
			}
		}
		
		/** {@inheritDoc} */
		@Override
		protected void failed() {
			super.failed();
			LOG.error('実行時に問題が発生しました。', exception);
			Platform.runLater {
				execButton.disable = false;
				controller.status.text = '実行時に問題が発生しました。';
			}
			new FxExceptionDialog(null, '実行時に問題が発生しました。', exception).showAndWait();
		}
	}
}
