/*
 * 文字コード別バイトコード確認
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovy.util.logging.Slf4j;
import javax.swing.JOptionPane;

@Slf4j('LOG')
class ByteCodeChecker {
	static void main(String[] args){
		String str = JOptionPane.showInputDialog(null, '対象文字列', '');
		List encList = ['UTF-16', 'UTF-8', 'EUC-JP', 'Windows-31J'];
		List results = [];
		for (String enc in encList){
			results << str2hex(str, enc);
		}
		JOptionPane.showMessageDialog(null, results.join("\n"));
	}
	
	private static String str2hex(String str, String enc){
		byte[] arr =str.getBytes(enc);
		List list = [];
		for (byte elem in arr){
			list << Integer.toHexString(elem & 0xff);
		}
		return "文字コード: ${enc} byteコード: ${list.join('')}";
	}
}
