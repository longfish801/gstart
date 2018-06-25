/*
 * ScriptConsole.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.scriptconsole;

import groovy.util.logging.Slf4j;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import io.github.longfish801.yakumo.clmap.Clinfo;
import io.github.longfish801.gstart.guiparts.application.FreeSizeApplication;
import io.github.longfish801.gstart.guiparts.dialog.FxDialog;
import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.gstart.guiparts.menu.MenuInfo;
import io.github.longfish801.gstart.guiparts.menu.MenuInfoFile;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.util.ClassConfig;

/**
 * スクリプト実行コンソールです。
 * @version 1.0.00 2017/07/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ScriptConsole extends FreeSizeApplication implements Initializable {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(ScriptConsole.class);
	/** 各操作時に実行するクロージャ情報 */
	Clinfo action = null;
	/** スクリプトルートフォルダ */
	File scriptRootDir = new File('.');
	/** 終了時に呼ぶクロージャ情報のコンビキー */
	String combiClose = null;
	/** スクリプトツリーでスクリプトをダブルクリックしたときに呼ぶクロージャ情報のコンビキー */
	String combiDoubleClicked = null;
	/** メニューリスト */
	List menuList = [];
	/** スクリプトツリーのコンテキストメニュー */
	ContextMenu contextMenu = new ContextMenu();
	/** ステータスラベル */
	@FXML Label status;
	/** スクリプトツリー */
	@FXML TreeView scriptTree;
	/** メニューバー */
	@FXML MenuBar menuBar;
	
	/**
	 * 各操作時に実行するクロージャ情報を格納します。<br>
	 * 引数としてコンビキーと、ScriptConsoleインスタンスを渡します。
	 * @param clinfo 各操作時に実行するクロージャ情報
	 */
	void setActionClinfo(Clinfo clinfo){
		ArgmentChecker.checkNotNull('クロージャ情報', clinfo);
		this.action = clinfo;
	}
	
	/**
	 * スクリプトルートフォルダを設定します。<br>
	 * スクリプトルートフォルダは、スクリプトツリーのルートとなるフォルダです。
	 * @param dir スクリプトルートフォルダ
	 */
	void setScriptDir(File dir){
		ArgmentChecker.checkNotNull('スクリプトルートフォルダ', dir);
		this.scriptRootDir = dir;
	}
	
	/**
	 * スクリプトコンソールの終了時に呼ぶクロージャ情報のコンビキーを設定します。
	 * @param combi コンビキー
	 */
	void setActionClose(String combi){
		ArgmentChecker.checkNotBlank('コンビキー', combi);
		this.combiClose = combi;
	}
	
	/**
	 * スクリプトツリーでスクリプトをダブルクリックしたときに呼ぶクロージャ情報のコンビキーを設定します。
	 * @param combi コンビキー
	 */
	void setActionDoubleClicked(String combi){
		ArgmentChecker.checkNotBlank('コンビキー', combi);
		this.combiDoubleClicked = combi;
	}
	
	/**
	 * メニューバーを格納します。<br>
	 * メニューバーはメニューを格納したリストです。
	 * @param menubar メニューバー
	 */
	void setMenuBar(Menu... menubar){
		ArgmentChecker.checkNotEmptyList('メニューバー', menubar as List);
		this.menuList.addAll(menubar);
	}
	
	/**
	 * コンテキストメニューを格納します。<br>
	 * メニューバーはメニューを格納したリストです。
	 * @param contextMenu コンテキストメニュー
	 */
	void setContextMenu(MenuItem... contextMenu){
		ArgmentChecker.checkNotEmptyList('コンテキストメニュー', contextMenu as List);
		this.contextMenu.items.addAll(contextMenu);
	}
	
	/**
	 * メニューを格納します。<br>
	 * メニューは以下の要素を格納したマップです。</p>
	 * <dl>
	 * <dt>name</dt><dd>メニュー名。必須。</dd>
	 * <dt>items</dt><dd>メニュー項目のリスト。必須。</dd>
	 * </dl>
	 * @param menuSetting メニューの設定
	 */
	Menu menu(Map menuSetting){
		ArgmentChecker.checkNotNull('メニュー設定', menuSetting);
		ArgmentChecker.checkNotBlank('メニュー名', menuSetting['name']);
		ArgmentChecker.checkNotEmptyList('メニュー項目リスト', menuSetting['items']);
		Menu menu = new Menu(menuSetting['name']);
		menuSetting['items'].each { menu.items.add(it) }
		return menu;
	}
	
	/**
	 * メニュー項目を格納します。<br>
	 * メニュー項目は以下の要素を格納したマップです。</p>
	 * <dl>
	 * <dt>name</dt><dd>メニュー項目名。必須。</dd>
	 * <dt>combi</dt><dd>メニューに対し実行するクロージャ情報のコンビキー。必須。</dd>
	 * <dt>accelerator</dt><dd>ショートカットキー設定。省略可。</dd>
	 * </dl>
	 * @param menuSetting メニュー項目の設定
	 */
	MenuItem menuItem(Map menuSetting){
		ArgmentChecker.checkNotNull('メニュー項目設定', menuSetting);
		ArgmentChecker.checkNotBlank('メニュー項目名', menuSetting['name']);
		ArgmentChecker.checkNotBlank('コンビキー', menuSetting['combi']);
		if (menuSetting['accelerator'] != null) ArgmentChecker.checkNotBlank('ショートカットキー設定', menuSetting['accelerator']);
		MenuItem item = new MenuItem(menuSetting['name']);
		item.onAction = { action.call(menuSetting['combi'], this) };
		if (menuSetting['accelerator'] != null) item.accelerator = KeyCombination.valueOf(menuSetting['accelerator']);
		return item;
	}
	
	/**
	 * スクリプト実行コンソールを表示します。<br>
	 * @throws IllegalStateException 各操作時に実行するクロージャ情報 actionが設定されていません。
	 */
	@Override
	void show(){
		if (action == null) throw new IllegalStateException('各操作時に実行するクロージャ情報 actionが設定されていません。');
		super.show();
	}
	
	/**
	 * スクリプト実行コンソールを終了します。
	 */
	@Override
	void close() {
		if (!started) return;
		if (!scriptTree.selectionModel.empty){
			config.formVals.selectedIndex = scriptTree.selectionModel.selectedIndex;
			config.formVals.lastSelected = scriptTree.selectionModel.selectedItem.value.id;
		}
		super.close();
		if (combiClose != null) action.call(combiClose, this);
	}
	
	/**
	 * 各フォームの初期値を設定します。
	 * @param location URL
	 * @param resources ResourceBundle
	 * @throws IllegalStateException 各操作時に実行するクロージャ情報 actionが設定されていません。
	 */
	@Override
	void initialize(URL location, ResourceBundle resources) {
		if (menuList.size() > 0) menuBar.menus.addAll(menuList);
		loadScriptTree();
		if (config.formVals.lastSelected != null) scriptTree.selectionModel.select(config.formVals.selectedIndex);
		scriptTree.setStyle('-fx-font: 12pt "Yu Gothic UI Regular";');
		scriptTree.onMouseClicked = { MouseEvent event ->
			if (!scriptTree.selectionModel.empty){
				MenuInfo menuInfo = scriptTree.selectionModel.selectedItem.value as MenuInfo;
				status.text = '選択：' + (menuInfo?.file?.absolutePath ?: menuInfo.dispName);
			}
			if (combiDoubleClicked != null && event.clickCount >= 2) action.call(combiDoubleClicked, this);
		}
		if (contextMenu.items.size() > 0) scriptTree.contextMenu = contextMenu;
	}
	
	/**
	 * スクリプト実行コンソールのスクリプトツリーを再読込します。
	 */
	void reload() {
		try {
			loadScriptTree();
			status.text = 'スクリプトツリーを再読込しました。';
		} catch (exc){
			LOG.error('スクリプトツリーの再読込時に問題が発生しました。', exc);
			FxDialog.exceptionDialog('スクリプトツリーの再読込時に問題が発生しました。', exc);
		}
	}
	
	/**
	 * スクリプトルートフォルダの状態からスクリプトツリーを作成します。
	 */
	private void loadScriptTree(){
		if (scriptRootDir == null) return;
		MenuInfo rootMenuInfo = new MenuInfoFile(scriptRootDir);
		Closure createMenuTree;
		createMenuTree = { MenuInfo menuInfo ->
			List<TreeItem<MenuInfo>> treeItems = [];
			menuInfo.subMenus.values().each { MenuInfo subMenu ->
				if (subMenu.hasSubMenu()){
					TreeItem itemFolder = new TreeItem<MenuInfo>(subMenu);
					itemFolder.children.addAll(createMenuTree(subMenu));
					if (config.formVals.lastSelected != null && config.formVals.lastSelected.startsWith(subMenu.id)) itemFolder.expanded = true;
					treeItems << itemFolder;
				} else {
					treeItems <<  new TreeItem<MenuInfo>(subMenu);
				}
			}
			return treeItems;
		}
		TreeItem rootItem = new TreeItem<MenuInfo>(rootMenuInfo);
		rootItem.children.addAll(createMenuTree(rootMenuInfo));
		scriptTree.root = rootItem;
		scriptTree.showRoot = false;
	}
	
	/**
	 * ウィンドウを閉じます。
	 * @param event ActionEvent
	 */
	void close(ActionEvent event){
		this.close();
	}
	
	/**
	 * スクリプトツリーを再読込します。
	 * @param event ActionEvent
	 */
	void reload(ActionEvent event){
		this.reload();
	}
}
