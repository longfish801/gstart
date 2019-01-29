/*
 * テキストファイルをHTMLファイルへ変換.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovyx.gpars.GParsPool;
import io.github.longfish801.gstart.guiparts.dialog.FxStatusDialog;
import io.github.longfish801.gstart.guiparts.dialog.FxDialog;
import io.github.longfish801.gstart.util.ClassConfig;
import io.github.longfish801.yakumo.YmoDocument;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指定テキストファイルあるいはフォルダ内のテキストファイルをHTMLファイルに変換します。
 * @version 1.0.00 2017/08/10
 * @author io.github.longfish801
 */

// ログ出力インスタンス
Logger LOG = LoggerFactory.getLogger('io.github.longfish801');
// 設定値
ClassConfig config = ClassConfig.newInstance(this.class, '''lastSpecified = [];''');

// 前回指定されたファイル／フォルダがある場合はダイアログで操作の選択を求めます
String operation = '変換するファイル／フォルダを指定する';
if (config.lastSpecified.size() > 0){
	List opeList = [];
	opeList << '変換するファイル／フォルダを指定する';
	opeList << "前回と同じファイル／フォルダを変換する（${config.lastSpecified.join(', ')}）";
	operation = FxDialog.singleSelect('操作を以下から選択してください。', opeList);
}

List<File> specifiedFiles = [];
switch (operation){
	case '変換するファイル／フォルダを指定する':
		// ダイアログでファイル／フォルダの指定を求めます
		specifiedFiles = FxDialog.filesSelect('処理対象のファイルをドラッグ＆ドロップで指定してください。', []);
		break;
	case { it.startsWith('前回と同じファイル／フォルダを変換する') }:
		// 前回指定されたファイル／フォルダを格納します
		specifiedFiles = config.lastSpecified.collect { new File(it) };
		break;
}

// 処理の対象が指定されていない場合は終了します
if (specifiedFiles.size() == 0){
	FxDialog.alertInformation('処理の対象が指定されていないため、終了します。');
	return;
}

// 指定されたファイル／フォルダを設定ファイルに保存します
config.lastSpecified = specifiedFiles.collect { it.absolutePath }
config.saveConfig();

// 処理状況ダイアログの表示を開始します
FxStatusDialog dialog = FxStatusDialog.start(null, '以下に処理状況を表示しています。', [ '処理を開始します。' ]);

// ダイアログで指定されたファイル／フォルダから処理対象の一覧を作成します
// 指定されたのがファイルならば、拡張子に関係なく処理対象とします
// 指定されたのがフォルダならば、その直下にあり、かつ拡張子が txtであるファイルを処理対象とします
List targetFiles = [];
specifiedFiles.each { File specifiedFile ->
	switch (specifiedFile){
		case { it.isFile() }: targetFiles << specifiedFile; break;
		case { it.isDirectory() }:
			specifiedFile.listFiles().findAll { it.isFile() && FilenameUtils.wildcardMatch(it.name, '*.txt') }.each { targetFiles << it };
			break;
		default:
			LOG.warn('ファイルでもフォルダでもないため無視します。path={}', specifiedFile.absolutePath);
	}
}

// HTMLに変換します
File outDir = null;
try {
	dialog << "ファイルの変換を開始します。";
	YmoDocument ymoDocument = new YmoDocument('_html');
	ymoDocument.script {
		target(*targetFiles);
		outDir = determineOutDir();
		LOG.info('outDir = {}', outDir.absolutePath);
		if (!outDir.isDirectory()) outDir.mkdirs();
		convertTargets(dialog);
	}
	dialog << "ファイルの変換が完了しました。";
} catch (exc){
	LOG.error('ファイルの変換に失敗しました。outDir={}', outDir, exc);
	dialog << "ファイルの変換に失敗しました。outDir=${outDir}";
	FxDialog.exceptionDialogAsync("変換に失敗しました（出力フォルダ：${outDir}）。", exc);
}

// 処理状況ダイアログの表示を終了します
dialog << 'すべての処理が完了しました。';
dialog.end();
