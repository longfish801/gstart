/*
 * TextSearcherApp.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:zenbun:0.1.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import groovy.swing.SwingBuilder;
import groovy.util.logging.Slf4j;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXB;

import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.shared.util.ClassConfig;
import io.github.longfish801.shared.util.TextUtil;
import io.github.longfish801.shared.util.SystemEditor;
import io.github.longfish801.zenbun.Searcher;
import io.github.longfish801.zenbun.searchinfo.*;

/**
 * テキスト検索アプリケーションです。
 */
@Slf4j('LOG')
class TextSearcherApp {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(TextSearcherApp.class);
	/** SwingBuilder */
	SwingBuilder swing = new SwingBuilder();
	/** フォーム入力値 */
	private FormVals formVals;

	/**
	 * メインメソッドです。
	 */
	static void main(String[] args) {
		new TextSearcherApp().show();
	}

	/**
	 * TextSearcherを終了します。
	 */
	void shutdown() {
		LOG.debug("TextSearcherを終了します。");
		
		// 設定値をファイルへ保存します
		config.window.locationX = swing.mainFrame.getX();
		config.window.locationY = swing.mainFrame.getY();
		config.window.sizeW = swing.mainFrame.getWidth();
		config.window.sizeH = swing.mainFrame.getHeight();
		config.formVals.searchWord = formVals.searchWord;
		config.formVals.dirMask    = formVals.dirMask;
		config.formVals.fileMask   = formVals.fileMask;
		config.saveConfig();
		
		// ウィンドウを終了します
		swing.dispose();
	}

	/**
	 * 検索結果を格納するデータモデルです。
	 */
	class SearchResult {
		/** 検索結果の行番号 */
		int rowIdx;
		/** ファイルパス */
		String path;
		/** 行番号 */
		int position;
	}

	/**
	 * フォーム入力値のデータモデルです。
	 */
	class FormVals {
		/** 検索語 */
		String searchWord = '';
		/** フォルダマスク */
		String dirMask = '';
		/** ファイルマスク */
		String fileMask = '*';
	}

	/**
	 * ウィンドウを表示します。
	 */
	void show(){
		// 設定値をフォームのデフォルト値として参照します
		formVals = new FormVals();
		formVals.searchWord = config.formVals.searchWord;
		formVals.dirMask = config.formVals.dirMask;
		formVals.fileMask = config.formVals.fileMask;
		
		// ウィンドウを表示します
		swing.frame(
			id: 'mainFrame', title: '全文検索', show: true, iconImage: AppIcon.icon,
			defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
			location: [ config.window.locationX, config.window.locationY ],
			size: [ config.window.sizeW, config.window.sizeH ],
			windowClosing: { shutdown(); }
		){
			boxLayout(axis:BoxLayout.Y_AXIS);
			
			// メニュー
			menuBar(){
				menu(text:'ヘルプ(H)', mnemonic: 'H'){
					menuItem(text:'ヘルプ(H)', mnemonic: 'H', actionPerformed:{ this.help() });
				}
			}
			
			// 検索条件
			panel(border: compoundBorder([ emptyBorder(5), titledBorder('検索条件')])){
				tableLayout {
					tr {
						td { label('検索語') }
						td { textField(formVals.searchWord, id: 'searchWord', columns: 20) }
					} 
					tr {
						td { label('フォルダ') }
						td { textField(formVals.dirMask, id: 'dirMask', columns: 30) }
					} 
					tr {
						td { label('ファイル') }
						td { textField(formVals.fileMask, id: 'fileMask', columns: 20) }
					}
				}
			}
			
			// 検索ボタン
			button(text: '検索', actionPerformed:{ search(); }, defaultButton: true );
			
			// 検索結果
			scrollPane(){
				def table = table(id: 'table') {
					def formVals = new DefaultTableModel(['ファイル', '行番号'] as Vector, 0) {
						boolean isCellEditable(int row, int column) {
							// すべてのセルを編集不可とします
							return false;
						}
					}
					tableModel(formVals, id: 'tableModel');
				}
				table.columnModel.getColumn(0).setPreferredWidth(400);
				table.addMouseListener([
						mouseClicked: { clickResult(it); },
						mouseEntered: {}, mouseExited: {}, mousePressed: {},
						mouseDragged : {}, mouseMoved : {}, mouseReleased: {}
					] as MouseListener);
			}
			
			// ステータス
			label('', id:'status');
			
			bean(
					formVals,
					searchWord: bind { searchWord.text },
					dirMask: bind { dirMask.text },
					fileMask: bind { fileMask.text }
				);
		}
	}
	
	/**
	 * ヘルプを表示します。
	 */
	void help(){
		Desktop.getDesktop().open(new File('doc/zenbun/index.html'));
	}

	/**
	 * 検索結果のクリックに対応する処理を実行します。
	 * @param ref スクリプト参照子
	 */
	void clickResult(MouseEvent evt){
		// ダブルクリックのとき以外はなにもしません
		if (evt.getClickCount() != 2){
			return;
		}
		
		// 検索結果をエディタで表示します
		SearchResult searchResult = getResult(evt);
		if (searchResult != null){
			String cmndEditor = SystemEditor.getCommand(searchResult.path, searchResult.position);
			if (cmndEditor != null){
				cmndEditor.execute();
			} else {
				Desktop.getDesktop().open(new File(searchResult.path));
			}
		}
	}
	
	/**
	 * マウスイベントが発生した位置の検索結果を取得します。
	 * @param evt MouseEvent
	 * @return 検索結果（該当行が無い場合はnullを返します）
	 */
	private SearchResult getResult(MouseEvent evt){
		int rowNum = swing.table.rowAtPoint(evt.getPoint());
		SearchResult searchResult = null;
		if (rowNum >= 0){
			searchResult = new SearchResult();
			searchResult.rowIdx = swing.table.convertRowIndexToModel(rowNum);
			searchResult.path = swing.tableModel.getValueAt(searchResult.rowIdx, 0);
			searchResult.position = swing.tableModel.getValueAt(searchResult.rowIdx, 1);
		}
		return searchResult;
	}
	
	/**
	 * 検索ボタン押下に対応する処理を実行します。
	 */
	void search(){
		SwingBuilder.build {
			// 現在の検索結果表示をクリアします
			swing.tableModel.setRowCount(0);
			swing.status.text = "";
			
			doOutside {
				// 検索を実行します
				edt { swing.status.text = "検索中..."; }
				List searchResults = searchActually(formVals);
				edt { swing.status.text = "検索結果件数 ${searchResults.size()}件"; }
				
				// 検索結果を表示します
				edt {
					for (SearchResult searchResult in searchResults){
						swing.tableModel.addRow([searchResult.path, searchResult.position] as Vector);
					}
				}
			}
		}
	}
	
	/**
	 * 検索ボタン押下に対応する処理を実行します。
	 * @param formVals フォーム入力値
	 * @return 検索結果
	 */
	private List searchActually(FormVals formVals){
		// 指定されたフォルダパスを、ベースディレクトリとインクルード指定に分割します
		String[] elems = TextUtil.split(formVals.dirMask, Pattern.quote(File.separator));
		String baseDir = '';
		String dirInclude = '';
		boolean flg = true;
		for (String elem : elems){
			if (flg){
				if (elem.indexOf('*') < 0 && elem.indexOf('?') < 0){
					baseDir += elem;
				} else {
					flg = false;
					dirInclude = elem;
				}
			} else {
				dirInclude += elem;
			}
		}
		LOG.debug("baseDir:${baseDir}");
		LOG.debug("dirInclude:${dirInclude}");
		
		// 検索条件を設定します
		Searcher searcher = new Searcher();
		searcher.getSearchInfo().getDirCond().setBaseDir(baseDir);
		if (!dirInclude.isEmpty()){
			IncludesType includesDir = new IncludesType();
			includesDir.getInclude().add(dirInclude);
			searcher.getSearchInfo().getDirCond().setIncludes(includesDir);
		}
		IncludesType includes = new IncludesType();
		includes.getInclude().add(formVals.fileMask);
		searcher.getSearchInfo().getFileCond().setIncludes(includes);
		KeywordsType keywords = new KeywordsType();
		keywords.getKeyword().add(formVals.searchWord);
		searcher.getSearchInfo().getTextCond().setKeywords(keywords);
		
		// 検索を実行します
		SearchInfo searchInfo = searcher.searchText();
		
		// 検索結果のオブジェクトを作成します
		List searchResults = [];
		int idx = 0;
		List dirs = searchInfo.getResults().getDir();
		for (DirType dir : dirs){
			List files =  dir.getFile();
			for (FileType file : files){
				List results =  file.getResult();
				for (ResultType result : results){
					SearchResult searchResult = new SearchResult();
					searchResult.rowIdx = idx ++;
					searchResult.path = file.getPath();
					searchResult.position = result.getLineNum();
					searchResults << searchResult;
				}
			}
		}
		
		// 検索結果をファイルに保存します
		File outFile = new File(config.getDataDir(), 'SearchInfo.xml');
		JAXB.marshal(searchInfo, outFile);
		
		return searchResults;
	}
}
