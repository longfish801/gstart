/*
 * 空フォルダを探す.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import javax.swing.*;
import io.github.longfish801.gstart.guiparts.dialog.SwingTextDialog;

// ファイルとしてカウントしない対象のファイル名リスト
List EXCLUDE_FNAMES = [ 'Thumbs.db' ];

// 探索開始位置のフォルダを受けとります
String path = JOptionPane.showInputDialog(null, "探索開始位置となるフォルダを指定してください。");

Closure scanEmptyDirs = {};	// 再帰的に処理させるため、いったん空で定義します
scanEmptyDirs = { File curDir, List findList ->
	int cnt = 0;
	curDir.eachFile { sub ->
		if (EXCLUDE_FNAMES.every { it != sub.name }) cnt ++;
		if (sub.isDirectory()) scanEmptyDirs(sub, findList);
	}
	if (cnt == 0) findList << curDir;
}

List list = [];
String errmsg = null;
if (path == null){
	errmsg = '処理を実施しません。';
} else {
	File rootDir = new File(path);
	if (!rootDir.isDirectory()){
		errmsg = '指定されたパスはフォルダではありません。';
	} else {
		scanEmptyDirs.call(rootDir, list);
		if (list.size() == 0) errmsg = '空フォルダはありませんでした。';
	}
}

String result = (errmsg == null)? list.collect { it.absolutePath }.join("\n") : errmsg;
new SwingTextDialog().show(null, "空フォルダ検索結果", result);

/*
空フォルダをまとめて削除したい場合は、たとえば以下を実行する。
String dirs = $/ ... 空フォルダの一覧 ... /$;
dirs.split("\n").each {
	File dir = new File(it);
	dir.eachFile { it.delete() }
	println dir.delete();
}
 */
