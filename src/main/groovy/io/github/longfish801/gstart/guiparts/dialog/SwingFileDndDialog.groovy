/*
 * SwingFileDndDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.swing.SwingBuilder;
import groovy.util.logging.Slf4j;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.util.ClassConfig;

/**
 * ドラッグ＆ドロップでファイルを選択するためのダイアログです。<br>
 * ドラッグ＆ドロップされたファイル（複数可、フォルダも可）の絶対パスをテキストエリアに表示します。<br>
 * OKボタン押下で、選択されたファイルの Fileインスタンスのリストを返します。
 * 
 * @version 1.0.00 2016/03/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
public class SwingFileDndDialog {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(SwingFileDndDialog.class);
	/** SwingBuilder */
	private SwingBuilder swing = new SwingBuilder();
	/** 選択されたファイルリスト */
	List files = [];
	
	/**
	 * ダイアログを表示します。<br>
	 * メッセージとして、選択すべきファイルについての説明を渡してください。<br>
	 * ダイアログのタイトルにはメッセージ文字列を表示します。<br>
	 * OKボタン押下で、選択されたファイルの Fileインスタンスのリストを返します。
	 *
	 * @param owner 親コンポーネント
	 * @param message メッセージ
	 * @return 選択されたファイルのリスト
	 */
	List showDialog(Component owner, String message){
		show(owner, message);
		// ドラッグ＆ドロップされなかったが、絶対パスのリストが初期表示されていた場合は Fileインスタンスのリストに変換します
		if (files.size() == 0 && !swing.files.text.isEmpty()){
			swing.files.text.split("\n").each { files << new File(it) }
		}
		return files;
	}
	
	/**
	 * ダイアログを表示します。
	 *
	 * @param owner 親コンポーネント
	 * @param message メッセージ
	 */
	private void show(Component owner, String message){
		swing.dialog(
			id: 'fileDndDialog', title: message, owner: owner, iconImage: AppIcon.icon, modal: true,
			location: [ config.window.locationX, config.window.locationY ],
			size: [ config.window.sizeW, config.window.sizeH ],
			defaultCloseOperation: JFrame.DO_NOTHING_ON_CLOSE, windowClosing: { closeWindow() }
		){
			boxLayout(axis: BoxLayout.Y_AXIS);
			
			// メッセージ
			label("<html>${message}</html>", alignmentX: 0.5f);
			
			// 選択されたファイル
			scrollPane() {
				textArea(text: config.files, id: 'files', rows: 20, lineWrap: false);
			}
			
			// ＯＫボタン
			panel(){
				button(text: 'ＯＫ', actionPerformed: { closeWindow() }, defaultButton: true);
			}
		}
		
		// ドラッグ＆ドロップを受け付けます
		DropTarget targetJTextField = new DropTarget(swing.files, new TextAreaDropTargetAdapter());
		
		// ダイアログを表示します
		swing.fileDndDialog.show();
	}
	
	/**
	 * テキストエリアにファイルがドラッグ＆ドロップされたときの処理をするクラスです。
	 */
	private class TextAreaDropTargetAdapter extends DropTargetAdapter {
		/** {@inheritDoc} */
		public void drop(DropTargetDropEvent evt) {
			try {
				Transferable transfer = evt.getTransferable();
				// ファイルリストの転送を受け付けます
				if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					// ドラッグ＆ドロップされたファイルのリストを取得し、表示に反映します
					evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					files = (List) transfer.getTransferData(DataFlavor.javaFileListFlavor);
					swing.files.text = files.collect({ it.absolutePath }).join("\n");
				}
			} catch (UnsupportedFlavorException exc) {
				LOG.warn("未サポートのデータがドロップされました。", exc);
			} catch (exc) {
				LOG.error("ドロップ時に想定外の例外が発生しました。", exc);
				throw exc;
			}
		}
	}
	
	/**
	 * ダイアログ終了時やＯＫボタン押下時、設定値を更新して保存し、ダイアログを閉じます。
	 */
	void closeWindow(){
		// 設定値をファイルへ保存します
		config.window.locationX = swing.fileDndDialog.getX();
		config.window.locationY = swing.fileDndDialog.getY();
		config.window.sizeW = swing.fileDndDialog.getWidth();
		config.window.sizeH = swing.fileDndDialog.getHeight();
		config.files = swing.files.text;
		config.saveConfig();
		
		// ダイアログを閉じます
		swing.dispose();
	}
}
