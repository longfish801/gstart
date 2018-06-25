/*
 * main.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.1.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import groovy.util.logging.Slf4j;
import java.awt.Desktop;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;
import io.github.longfish801.gstart.guiparts.application.FreeSizeApplication;
import io.github.longfish801.gstart.guiparts.dialog.FxExceptionDialog;
import io.github.longfish801.gstart.guiparts.dialog.FxTextDialog;
import io.github.longfish801.shared.util.ClassConfig;
import io.github.longfish801.shared.util.ScriptHelper;
import io.github.longfish801.yakumo.washscr.WashScr;

/**
 * WashTxtアプリケーションです。
 * @version 1.0.00 2016/01/14
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class WashTxtApplication extends FreeSizeApplication implements Initializable {
	/** ClassConfig */
	ClassConfig config = ClassConfig.newInstance(WashTxtApplication.class);
	/** 変換ボタン押下時の処理 */
	ConvertService service = new ConvertService();
	/** ステータスラベル */
	@FXML Label status;
	/** 変換ボタン */
	@FXML Button convertButton;
	/** 処理方法 */
	@FXML ToggleGroup methodType;
	/** 処理方法：置換(固定) */
	@FXML RadioButton methodTypeReplace;
	/** 処理方法：置換(正規表現) */
	@FXML RadioButton methodTypeReprex;
	/** 処理方法：WashScr */
	@FXML RadioButton methodTypeScript;
	/** 処理内容文字列 */
	@FXML TextArea scriptText;
	/** 処理対象文字列 */
	@FXML TextArea targetText;
	
	/**
	 * メインメソッドです。
	 * @param arg コマンドライン引数
	 */
	static void main(args){
		LOG.info('WashTxtアプリケーションを起動します。');
		try {
			new WashTxtApplication().show();
		} catch (exc){
			LOG.error('WashTxtアプリケーションの起動に失敗しました。');
			throw exc;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	void close() {
		config.formVals.scriptText = scriptText.text;
		config.formVals.targetText = targetText.text;
		config.formVals.methodType = methodType.selectedToggle.id;
		super.close();
	}
	
	/**
	 * 各フォームの初期値を設定します。
	 * @param location URL
	 * @param resources ResourceBundle
	 */
	@Override
	void initialize(URL location, ResourceBundle resources) {
		methodType.toggles.each { it.selected = (it.id == config.formVals.methodType)? true : false }
		targetText.text = config.formVals.targetText;
		scriptText.text = config.formVals.scriptText;
	}
	
	/**
	 * ヘルプファイルを開きます。
	 * @param event ActionEvent
	 */
	void help(ActionEvent event){
		Desktop.desktop.open(new File(ScriptHelper.parentDir(WashTxtApplication.class), 'doc/help.html'));
	}
	
	/**
	 * ウィンドウを閉じます。
	 * @param event ActionEvent
	 */
	void close(ActionEvent event){
		this.close();
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
			return new ConvertTask<String>();
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
				targetText.editable = false;
				scriptText.editable = false;
				convertButton.disable = true;
				controller.status.text = '変換中です。';
			}
			
			// テキストを変換します
			String code = "#! washscr WashTxtApplication\n## slice\n";
			switch (methodType.selectedToggle.id){
				case methodTypeReplace.id:
					code += "# replace 固定置換\n" + scriptText.text + "\n";
					break;
				case methodTypeReprex.id:
					code += "# reprex 正規表現置換\n" + scriptText.text + "\n";
					break;
				case methodTypeScript.id:
					code = "#! washscr WashTxtApplication\n" + scriptText.text + "\n";
					break;
				default:
					throw new InternalError("処理方法が選択されていません。");
					break;
			}
			LOG.info('code={}', code);
			return new WashScr(code).wash(targetText.text);
		}
		
		/** {@inheritDoc} */
		@Override
		protected void succeeded(){
			super.succeeded();
			Platform.runLater {
				// 処理結果を表示し、編集可にします
				targetText.editable = true;
				scriptText.editable = true;
				convertButton.disable = false;
				status.text = '変換に成功しました。';
				new FxTextDialog(null, '変換結果は以下のとおりです。', getValue()).showAndWait();
			}
		}
		
		/** {@inheritDoc} */
		@Override
		protected void failed() {
			super.failed();
			LOG.error('変換時に問題が発生しました。', exception);
			Platform.runLater {
				targetText.editable = true;
				scriptText.editable = true;
				convertButton.disable = false;
				controller.status.text = '変換時に問題が発生しました。';
				new FxExceptionDialog(null, '変換時に問題が発生しました。', exception).showAndWait();
			}
		}
	}
}
