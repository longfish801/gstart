/*
 * SystemEditor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.text.SimpleTemplateEngine;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ExchangeResource;
import java.awt.Desktop;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ファイルをテキストエディタで開くためのユーティリティです。
 * @version 1.0.00 2017/07/26
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SystemEditor {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(SystemEditor.class);
	
	/**
	 * 指定されたファイルをエディタで開きます。<br>
	 * 以下を試みます。
	 * (1) 環境変数"EDITOR"にエディタ起動用のコマンドが指定されていれば、それで開きます。
	 * (2) {@link Desktop#edit(File)}が対応していれば、それで開きます。
	 * (3) {@link Desktop#open(File)}が対応していれば、それで開きます。
	 * (4) 例外IOExceptionを投げます。
	 * @param file 対象ファイル
	 */
	static void exec(File file) {
		// 環境変数"EDITOR"にエディタ起動用のコマンドが指定されていれば、それで開きます。
		String command = SystemEditor.getCommand(file.absolutePath, 1);
		if (command != null){
			command.execute();
			return;
		}
		
		// Desktop#editが対応していれば、それで開きます。
		if (Desktop.isSupported(Desktop.Action.EDIT)){
			Desktop.desktop.edit(file);
			return;
		}
		
		// Desktop#openが対応していれば、それで開きます。
		if (Desktop.isSupported(Desktop.Action.OPEN)){
			Desktop.desktop.open(file);
			return;
		}
		
		// 上記の方法で開けなければ、例外を投げます
		throw new IOException("エディタの起動に失敗しました。file=${file.path}");
	}
	
	/**
	 * 行番号指定でエディタを起動するためのコマンドを返します。<br>
	 * 環境変数"EDITOR"に、エディタ起動用のコマンドを指定しておく必要があります。<br>
	 * 環境変数の値には以下の埋め込み変数が使用できます。</p>
	 * <ul>
	 * <li>${path} - 起動対象ファイルへのパス</li>
	 * <li>${position} - 行番号</li>
	 * </ul>
	 * <p>環境変数が未設定の場合はnullを返します。
	 * @param path 対象ファイルへのパス
	 * @param position 表示したい行
	 * @return エディタを起動するためのコマンド
	 */
	static String getCommand(String path, int position){
		String template = System.getenv(cnst.envName);
		if (template == null) return null;
		Map binding = [ 'path': "${path}", 'position': "${position}" ];
		template = template.replaceAll(Pattern.quote('\\'), Matcher.quoteReplacement('\\\\'));	// 円記号(\)をエスケープします
		String command = new SimpleTemplateEngine().createTemplate(template).make(binding).toString();
		LOG.debug('command={}', command);
		return command;
	}
}
