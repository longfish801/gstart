/*
 * バイトコード文字列変換.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovy.util.logging.Slf4j;

import javax.swing.JOptionPane;

@Slf4j('LOG')
class ConvertByteCode {
	static void main(args){
		String inputStr = JOptionPane.showInputDialog(null, 'バイトコード(\\u9999)', '');
		if (inputStr != null){
			LOG.info("inputStr=${inputStr}");
			String result = parseByteCodeBulk(inputStr).join(" ");
			LOG.info("result=${result}");
			JOptionPane.showMessageDialog(null, result);
		}
	}
	
	static List parseByteCodeBulk(String str){
		List list = str.split("\\\\u");
		List result = [];
		for (String elem : list){
			if (!elem.isEmpty()){
				LOG.info("elem=${elem}");
				result << parseByteCode(elem);
			}
		}
		return result;
	}
	
	static String parseByteCode(String codeStr){
		int code = Integer.parseInt(codeStr, 16);
		char[] chars = Character.toChars(code);
		return chars.toString();
	}
}
