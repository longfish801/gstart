/*
 * SwingTextDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.swing.SwingBuilder;
import groovy.util.logging.Slf4j;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import io.github.longfish801.gstart.guiparts.ClipboardUtil;
import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.util.ClassConfig;

/**
 * 複数行の文字列を表示するためのダイアログです。<br>
 * テキストエリア内に複数行の文字列を表示します。
 * @version 1.0.00 2015/08/14
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwingTextDialog {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(SwingTextDialog.class);
	/** SwingBuilder */
	private SwingBuilder swing = new SwingBuilder();
	
	/**
	 * ダイアログを表示します。<br>
	 * メッセージとして、テキストエリア内に表示する文字列についての説明を渡してください。<br>
	 * ダイアログのタイトルにはメッセージ文字列を表示します。
	 *
	 * @param owner 親コンポーネント
	 * @param message メッセージ
	 * @param text テキストエリアに表示するテキスト
	 */
	void show(Component owner, String message, String text){
		swing.dialog(
			id: 'mainDialog', title: message, owner: owner,
			show: true, iconImage: AppIcon.icon,
			defaultCloseOperation: JFrame.DO_NOTHING_ON_CLOSE,
			location: [ config.window.locationX, config.window.locationY ],
			size: [ config.window.sizeW, config.window.sizeH ],
			windowClosing: { this.closeWindow() }
		){
			borderLayout();
			
			// メッセージ
			label(message, id: 'message', constraints: NORTH, alignmentX: 0.0f);
			
			// テキストエリア
			scrollPane(constraints: CENTER) {
				textArea(
						id: 'textArea',
						text: text,
						lineWrap: true,
						rows: 10
					);
			}
			
			panel(constraints: SOUTH){
				boxLayout(axis: BoxLayout.Y_AXIS);
				panel(alignmentX: 0.5f){
					boxLayout(axis: BoxLayout.X_AXIS);
					
					// ＯＫボタン
					button(text: 'ＯＫ', actionPerformed: { this.closeWindow() }, defaultButton: true);
					
					// コピーボタン
					button(text: 'コピー', actionPerformed: { this.copyText() });
				}
			}
		}
	}
	
	/**
	 * コピーボタン押下時、テキストエリア内容をコピーします。
	 */
	void copyText(){
		ClipboardUtil.setText(swing.textArea.text);
	}
	
	/**
	 * ダイアログ終了時やＯＫボタン押下時、設定値を更新して保存し、ダイアログを閉じます。
	 */
	void closeWindow(){
		// 設定値をファイルへ保存します
		config.window.locationX = swing.mainDialog.getX();
		config.window.locationY = swing.mainDialog.getY();
		config.window.sizeW = swing.mainDialog.getWidth();
		config.window.sizeH = swing.mainDialog.getHeight();
		config.text = swing.textArea.text;
		config.saveConfig();
		
		// ダイアログを閉じます
		swing.dispose();
	}
}
