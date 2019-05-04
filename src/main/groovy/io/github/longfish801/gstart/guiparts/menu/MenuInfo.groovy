/*
 * MenuInfo.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.menu;

import io.github.longfish801.shared.ArgmentChecker;

/**
 * メニューに関する情報を保持するクラスです。
 * @version 1.0.00 2015/08/19
 * @author io.github.longfish801
 */
class MenuInfo {
	/** 識別キー */
	String key = null;
	/** メニュー表示名 */
	String dispName = null;
	/** サブメニュー */
	Map<String, MenuInfo> subMenus = [:];
	/** 親メニュー */
	MenuInfo upper = null;
	
	/**
	 * 他のメニューと重複しない、唯一のIDを返します。
	 * @return メニュー情報ID
	 */
	String getId(){
		return (upper == null)? key : "${upper.id}/${key}";
	}
	
	/**
	 * サブメニューが存在するか否か判定を返します。
	 * @return サブメニューが存在するか否か
	 */
	boolean hasSubMenu(){
		return (subMenus.size() > 0)? true : false;
	}
	
	/**
	 * サブメニューを追加します。
	 * @param menuInfo メニュー情報
	 */
	MenuInfo leftShift(MenuInfo menuInfo){
		ArgmentChecker.checkUniqueKey('サブメニュー', menuInfo.key, subMenus);
		subMenus[menuInfo.key] = menuInfo;
		menuInfo.upper = this;
		return this;
	}
	
	/**
	 * 文字列表現を返します。<br>
	 * メニュー表示名を返します。
	 * @return 文字列表現
	 */
	String toString(){
		return dispName;
	}
}
