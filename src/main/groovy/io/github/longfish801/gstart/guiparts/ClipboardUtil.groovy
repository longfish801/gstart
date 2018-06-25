/*
 * ClipboardUtil.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts;

import groovy.util.logging.Slf4j;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * クリップボード関連のユーティリティです。
 * 
 * @version 1.0.00 2015/08/19
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClipboardUtil {
	/** クリップボード */
	private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * クリップボードに文字列が格納されているとみなして参照します。
	 * @return クリップボードに格納された文字列（取得失敗時はnull）
	 */
	static String getText() {
		return (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
	}

	/**
	 * クリップボードに文字列を格納します。
	 * @param str クリップボードに格納する文字列
	 */
	static void setText(String str) {
		clipboard.setContents(new StringSelection(str), null);
	}
}
