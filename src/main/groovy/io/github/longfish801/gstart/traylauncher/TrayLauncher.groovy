/*
 * TrayLauncher.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.traylauncher;

import groovy.util.logging.Slf4j;

import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import io.github.longfish801.clmap.Clinfo;
import io.github.longfish801.gstart.guiparts.icon.AppTrayIcon;
import io.github.longfish801.gstart.guiparts.menu.MenuInfo;
import io.github.longfish801.gstart.guiparts.menu.MenuInfoFile;
import io.github.longfish801.shared.ArgmentChecker;
import org.apache.commons.lang3.StringUtils;

/**
 * タスクトレイランチャーです。
 * @version 1.0.00 2017/07/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TrayLauncher {
	/** 各操作時に実行するクロージャ情報 */
	Clinfo action = null;
	/** タスクトレイアイコンをダブルクリックしたときに呼ぶクロージャ情報のコンビキー */
	String combiDoubleClicked = null;
	/** ポップアップメニュー設定 */
	List popupMenuSetting = [];
	/** スクリプトメニューで選択されたスクリプトファイル */
	File selectedScript = null;
	
	/**
	 * 各操作時に実行するクロージャ情報を格納します。<br>
	 * 引数としてコンビキーと、TrayLauncherインスタンスを渡します。
	 * @param clinfo 各操作時に実行するクロージャ情報
	 */
	void setActionClinfo(Clinfo clinfo){
		ArgmentChecker.checkNotNull('クロージャ情報', clinfo);
		this.action = clinfo;
	}
	
	/**
	 * タスクトレイアイコンをダブルクリックしたときに呼ぶクロージャ情報のコンビキーを設定します。
	 * @param combi コンビキー
	 */
	void setActionDoubleClicked(String combi){
		ArgmentChecker.checkNotBlank('コンビキー', combi);
		this.combiDoubleClicked = combi;
	}
	
	/**
	 * ポップアップメニューを格納します。<br>
	 * ポップアップメニューはメニュー、メニュー項目を格納したリストです。
	 * @param popupMenu ポップアップメニュー
	 */
	void setPopupMenu(Map... popupMenu){
		ArgmentChecker.checkNotEmptyList('ポップアップメニュー', popupMenu as List);
		this.popupMenuSetting.addAll(popupMenu as List);
	}
	
	/**
	 * メニューを格納します。<br>
	 * メニューは以下の要素を格納したマップです。</p>
	 * <dl>
	 * <dt>name</dt><dd>メニュー名。必須。</dd>
	 * <dt>items</dt><dd>メニュー項目のリスト。必須。</dd>
	 * </dl>
	 * @param menuSetting メニューの設定
	 * @return メニューの設定
	 */
	Map menu(Map menuSetting){
		ArgmentChecker.checkNotNull('メニュー設定', menuSetting);
		ArgmentChecker.checkNotBlank('メニュー名', menuSetting['name']);
		ArgmentChecker.checkNotEmptyList('メニュー項目リスト', menuSetting['items']);
		menuSetting['type'] = 'menu';
		return menuSetting;
	}
	
	/**
	 * メニュー項目を格納します。<br>
	 * メニュー項目は以下の要素を格納したマップです。</p>
	 * <dl>
	 * <dt>name</dt><dd>メニュー項目名。必須。</dd>
	 * <dt>combi</dt><dd>メニューに対し実行するクロージャ情報のコンビキー。必須。</dd>
	 * </dl>
	 * @param menuSetting メニュー項目の設定
	 * @return メニュー項目の設定
	 */
	Map menuItem(Map menuSetting){
		ArgmentChecker.checkNotNull('メニュー項目設定', menuSetting);
		ArgmentChecker.checkNotBlank('メニュー項目名', menuSetting['name']);
		ArgmentChecker.checkNotBlank('コンビキー', menuSetting['combi']);
		menuSetting['type'] = 'menuItem';
		return menuSetting;
	}
	
	/**
	 * スクリプトメニューを格納します。<br>
	 * スクリプトメニューは以下の要素を格納したマップです。</p>
	 * <dl>
	 * <dt>dir</dt><dd>スクリプトルートフォルダ。必須。</dd>
	 * <dt>combi</dt><dd>メニューに対し実行するクロージャ情報のコンビキー。必須。</dd>
	 * </dl>
	 * @param menuSetting スクリプトメニューの設定
	 * @return スクリプトメニューの設定
	 */
	Map menuScript(Map menuSetting){
		ArgmentChecker.checkNotNull('メニュー項目設定', menuSetting);
		ArgmentChecker.checkNotNull('スクリプトルートフォルダ', menuSetting['dir']);
		ArgmentChecker.checkNotBlank('コンビキー', menuSetting['combi']);
		menuSetting['type'] = 'menuScript';
		return menuSetting;
	}
	
	/**
	 * セパレータを格納します。
	 * @return セパレータの設定
	 */
	Map menuSeparator(){
		return [ 'type' : 'menuSeparator' ];
	}
	
	/**
	 * タスクトレイランチャーを開始します。
	 */
	void show(){
		if (action == null) throw new IllegalStateException('各操作時に実行するクロージャ情報 actionが設定されていません。');
		reload();
		if (!SystemTray.systemTray.trayIcons.contains(AppTrayIcon.icon)) SystemTray.systemTray.add(AppTrayIcon.icon);
	}
	
	/**
	 * タスクトレイランチャーを開始しているか否か返します。
	 */
	boolean isShow(){
		return SystemTray.systemTray.trayIcons.contains(AppTrayIcon.icon);
	}
	
	/**
	 * タスクトレイランチャーを終了します。
	 */
	void close(){
		SystemTray.getSystemTray().remove(AppTrayIcon.icon);
	}
	
	/**
	 * タスクトレイランチャーを再読込します。
	 */
	void reload(){
		// アイコンをダブルクリックしたときの処理を設定します
		AppTrayIcon.icon.addMouseListener(new MouseAdapter(){
			void mouseClicked(MouseEvent event){
				if (combiDoubleClicked != null && event.clickCount >= 2) action.call(combiDoubleClicked, TrayLauncher.this);
			}
		});
		
		// 右クリックメニューを生成します
		PopupMenu popupMenu = new PopupMenu();
		for (Map menuSetting : popupMenuSetting){
			switch (menuSetting['type']){
				case 'menu':	// メニューを追加します
					addMenu(menuSetting, popupMenu);
					break;
				case 'menuItem':	// メニュー項目を追加します
					addMenuItem(menuSetting, popupMenu);
					break;
				case 'menuScript':	// スクリプトメニューを追加します
					addMenuScript(menuSetting, popupMenu);
					break;
				case 'menuSeparator':	// メニュー区切りを挿入します
					popupMenu.addSeparator();
					break;
				default:
					throw new InternalError("type属性の値が不正です。type=${type}");
					break;
			}
		}
		AppTrayIcon.icon.popupMenu = popupMenu;
	}
	
	/**
	 * メニューを追加します。
	 * @param menuSetting メニュー設定
	 * @param parentMenu 親メニュー
	 */
	private void addMenu(Map menuSetting, Menu parentMenu){
		Menu menu = new Menu(menuSetting['name']);
		menuSetting['items'].each { addMenuItem(it, menu) }
		parentMenu.add(menu);
	}
	
	/**
	 * メニュー項目を追加します。
	 * @param menuSetting メニュー項目設定
	 * @param parentMenu 親メニュー
	 */
	private void addMenuItem(Map menuSetting, Menu parentMenu){
		MenuItem menuItem = new MenuItem(menuSetting['name']);
		menuItem.addActionListener({ ActionEvent event ->
			action.call(menuSetting['combi'], this);
		});
		parentMenu.add(menuItem);
	}
	
	/**
	 * スクリプトメニューを追加します。
	 * @param menuSetting スクリプトメニュー設定
	 * @param parentMenu 親メニュー
	 */
	private void addMenuScript(Map menuSetting, Menu parentMenu){
		MenuInfo rootMenuInfo = new MenuInfoFile(menuSetting['dir']);
		Closure createMenuTree;
		createMenuTree = { MenuInfo menuInfo, String combi ->
			List<MenuItem> items = [];
			menuInfo.subMenus.values().each { MenuInfo subMenu ->
				if (subMenu.hasSubMenu()){
					Menu menu = new Menu(subMenu.dispName);
					menuSetting['items'].each { addMenuItem(it, menu) }
					createMenuTree(subMenu, combi).each { menu.add(it) }
					items <<  menu;
				} else {
					MenuItem menuItem = new MenuItem(subMenu.dispName);
					menuItem.addActionListener({ ActionEvent event ->
						selectedScript = (subMenu as MenuInfoFile).file;
						action.call(combi, this);
					});
					items <<  menuItem;
				}
			}
			return items;
		}
		createMenuTree(rootMenuInfo, menuSetting['combi']).each { parentMenu.add(it) }
	}
}
