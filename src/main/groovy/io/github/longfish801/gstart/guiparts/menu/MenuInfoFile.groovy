/*
 * MenuInfoFile.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.menu;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import org.apache.commons.io.FilenameUtils;

/**
 * ファイルやフォルダ構成に基づくメニュー情報です。<br>
 * フォルダとファイルの階層関係からメニューを生成します。<br>
 * 拡張子「.gvy」をメニューとみなします。拡張子を除いたファイル名をメニュー名にします。<br>
 * フォルダ名はそのままメニューフォルダ名に使用します。<br>
 * ただしデフォルトスクリプト"main.gvy"が存在した場合は、その親フォルダ名をメニュー名にします。<br>
 * デフォルトスクリプトと同じフォルダに拡張子".gvy"のファイルが他に存在しても無視します。
 * @version 1.0.00 2015/08/19
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MenuInfoFile extends MenuInfo {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(MenuInfoFile.class);
	/** メニューに関連付けするファイル */
	File file = null;
	
	/**
	 * 最上位のメニュー情報のためのコンストラクタ。
	 * @param key 識別子
	 * @param dir メニュー作成対象のルートとなるフォルダ
	 * @throws IOException フォルダを参照できません。
	 */
	MenuInfoFile(File dir){
		LOG.debug("最上位のメニュー作成：dir={}", dir);
		this.key = dir.name;
		File[] files = dir.listFiles();
		if (files == null) throw new IOException("フォルダを参照できません。dir=${dir.absolutePath}");
		files.each { setupMenu(it, this); }
	}
	
	/**
	 * メニューフォルダ作成用のコンストラクタ。
	 * @param dispName メニュー表示名
	 */
	private MenuInfoFile(String dispName){
		ArgmentChecker.checkNotBlank('dispName', dispName);
		LOG.debug('メニューフォルダ作成：dispName={}', dispName);
		this.key = dispName;
		this.dispName = dispName;
	}
	
	/**
	 * メニュー項目作成用のコンストラクタ。
	 * @param dispName メニュー表示名
	 * @param file メニューに関連付けするファイル
	 */
	private MenuInfoFile(String dispName, File file){
		ArgmentChecker.checkNotBlank('dispName', dispName);
		ArgmentChecker.checkNotNull('file', file);
		LOG.debug('メニュー項目作成：dispName={}, file={}', dispName, file.absolutePath);
		this.key = dispName;
		this.dispName = dispName;
		this.file = file;
	}
	
	/**
	 * メニュー情報を再帰的に生成します。
	 * @param elem メニュー作成対象フォルダ配下のファイル/フォルダ
	 * @param parentMenu 親のメニュー情報
	 * @return 下位でメニュー情報を作成したか
	 */
	private boolean setupMenu(File elem, MenuInfo parentMenu) {
		boolean isThereMenu = false;	// 下位でメニュー作成をしたか
		switch (elem){
			case { it.isDirectory() }:
				// 対象がフォルダの場合、フォルダ内にデフォルトスクリプトがあるか確認します
				File mainFile = elem.listFiles().find { it.isFile() && it.name == cnst.script.defaultName };
				if (mainFile != null){
					// デフォルトスクリプトがあれば、フォルダ名をメニュー名としたメニューフォルダを作成します
					parentMenu << new MenuInfoFile(elem.name, mainFile);
					isThereMenu = true;
				} else {
					// デフォルトスクリプトが無ければ、フォルダ内の要素について再帰的に呼びだします
					MenuInfo menuInfo = new MenuInfoFile(elem.name);
					if (elem.listFiles().findAll { setupMenu(it, menuInfo) }.size() > 0){
						// 一度でも下位でメニューを作成したならば、メニューフォルダを追加します
						parentMenu << menuInfo;
					}
				}
				break;
			case { it.isFile() }:
				// 対象がファイルの場合、ファイル名がパターンを満たすなら、メニュー項目を作成します
				if (FilenameUtils.wildcardMatch(elem.name, cnst.script.pattern)){
					parentMenu << new MenuInfoFile(FilenameUtils.removeExtension(elem.name), elem);
					isThereMenu = true;
				}
				break;
			default:
				LOG.debug('ファイルでもフォルダでもありません。elem={}', elem.absolutePath);
		}
		return isThereMenu;
	}
}
