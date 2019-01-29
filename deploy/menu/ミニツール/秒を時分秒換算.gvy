/*
 * 秒を時分秒換算
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovy.util.logging.Slf4j;
import javax.swing.JOptionPane;

@Slf4j('LOG')
class SecToTime {
	static void main(String[] args){
		String input = JOptionPane.showInputDialog(null, "秒数を指定してください。", "");
		if (input == null){
			return;
		}
		
		// 時分秒の値を求める
		long totalSec = Long.parseLong(input);
		long hh = totalSec / 3600;
		long mm = (totalSec % 3600) / 60;
		long ss = totalSec % 60;
		
		long days = 0;
		if (hh >= 24){
			days = hh / 24;
			hh = hh % 24;
		}
		long years = 0;
		if (days >= 365){
			years = days / 365;
			days = days - (years * 365);
		}
		
		// 表示用の文字列を作成する
		StringBuilder builder = new StringBuilder();
		if (years > 0) builder.append("${years}years ");
		if (days > 0) builder.append("${days}days ");
		builder.append("${String.format('%02d', hh)}:${String.format('%02d', mm)}:${String.format('%02d', ss)}");
		if (years > 0) builder.append("（１年＝365日換算）");
		
		// 結果を表示します
		JOptionPane.showMessageDialog(null, "${input} sec = ${builder.toString()}");
	}
}
