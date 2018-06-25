/*
 * FxDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.util.ClassSlurper;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFXのダイアログに関するユーティリティクラスです。
 * このクラスのメソッドは JavaFXアプリケーションスレッドからは呼ばないでください。<br>
 * JavaFXアプリケーションスレッド以外のスレッドから各ダイアログを表示させるために使用します。
 * @version 1.0.00 2017/07/20
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxDialog {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(FxDialog.class);
	
	/**
	 * 情報ダイアログを表示します。
	 * @param message メッセージ
	 */
	static String alertInformation(String message){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			try {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.title = constants.dialogTitle.information;
				alert.headerText = message;
				alert.contentText = null;
				(alert.dialogPane.scene.window as Stage).icons.add(AppIcon.fxIcon);
				alert.showAndWait();
			} catch (exc){
				FxDialog.LOG.error('情報ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put('');
			}
		}
		queue.take();
	}
	
	/**
	 * 注意ダイアログを表示します。
	 * @param message メッセージ
	 */
	static String alertWarning(String message){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			try {
				Alert alert = new Alert(AlertType.WARNING);
				alert.title = constants.dialogTitle.warning;
				alert.headerText = message;
				alert.contentText = null;
				(alert.dialogPane.scene.window as Stage).icons.add(AppIcon.fxIcon);
				alert.showAndWait();
			} catch (exc){
				FxDialog.LOG.error('注意ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put('');
			}
		}
		queue.take();
	}
	
	/**
	 * テキスト入力ダイアログを表示します。<br>
	 * 取消ボタン押下時やダイアログを閉じた場合は空文字を返します。
	 * @param caption 説明（ヘッダーテキスト）
	 * @param defaultValue デフォルト値
	 * @return テキスト入力ダイアログへの入力値（未指定時は空文字）
	 */
	static String textInputDialog(String caption, String defaultValue){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			String inputed = '';
			try {
				TextInputDialog dialog = new TextInputDialog(defaultValue);
				dialog.title = constants.dialogTitle.textInput;
				dialog.headerText = caption;
				dialog.contentText = null;
				dialog.editor.font = Font.font('Yu Gothic UI Regular', 12);
				(dialog.dialogPane.scene.window as Stage).icons.add(AppIcon.fxIcon);
				inputed = dialog.showAndWait().orElse('');
			} catch (exc){
				FxDialog.LOG.error('テキスト入力ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put(inputed);
			}
		}
		return queue.take();
	}
	
	/**
	 * 選択ダイアログを表示し、選択結果を返します。<br>
	 * 選択せずにダイアログを閉じた場合は空文字を返します。
	 * @param headerText 説明
	 * @param list 選択リスト
	 * @return 選択結果文字列（未指定時は空文字）
	 */
	static String singleSelect(String headerText, List list){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			String selected = '';
			try {
				selected = new FxListViewDialog(null, headerText, list).showAndWait().orElse('');
			} catch (exc){
				FxDialog.LOG.error('選択ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put(selected);
			}
		}
		return queue.take();
	}
	
	/**
	 * ファイル指定ダイアログを表示し、選択結果を返します。<br>
	 * 選択せずにダイアログを閉じた場合は空リストを返します。
	 * @param headerText 説明
	 * @param list 選択リスト
	 * @return 選択結果文字列（未指定時は空文字）
	 */
	static List<File> filesSelect(String headerText, List list){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			List<File> files = [];
			try {
				files = new FxFileDndDialog(null, headerText, list).showAndWait().orElse([]);
			} catch (exc){
				FxDialog.LOG.error('選択ダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put(files);
			}
		}
		return queue.take();
	}
	
	/**
	 * テキストダイアログを表示します。
	 * @param headerText 説明
	 * @param text テキスト（null、空文字、空白文字を許容）
	 */
	static void textDialog(String headerText, String text){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			try {
				new FxTextDialog(null, headerText, text).showAndWait();
			} catch (exc){
				FxDialog.LOG.error('テキストダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put('');
			}
		}
		queue.take();
	}
	
	/**
	 * テキストダイアログを非同期で表示します。
	 * @param headerText 説明
	 * @param text テキスト（null、空文字、空白文字を許容）
	 */
	static void textDialogAsync(String headerText, String text){
		Platform.runLater { new FxTextDialog(null, headerText, text).show() }
	}
	
	/**
	 * エラーダイアログを表示します。
	 * @param headerText 説明
	 * @param exception 例外
	 */
	static void exceptionDialog(String headerText, Throwable exception){
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
		Platform.runLater {
			try {
				new FxExceptionDialog(null, headerText, exception).showAndWait();
			} catch (exc){
				FxDialog.LOG.error('テキストダイアログ表示に失敗しました。', exc);
			} finally {
				queue.put('');
			}
		}
		queue.take();
	}
	
	/**
	 * エラーダイアログを非同期で表示します。
	 * @param headerText 説明
	 * @param exception 例外
	 */
	static void exceptionDialogAsync(String headerText, Throwable exception){
		Platform.runLater { new FxExceptionDialog(null, headerText, exception).show() }
	}
}
