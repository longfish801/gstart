/*
 * ymoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

// 出力先フォルダ
File outDir = new File(targetDir, '../docs');

yakumo.script {
	// 変換資材を読みこみます
	['_bltxt', '_html', convDir].each { configure(it) };
	// 出力先フォルダなどを設定します
	setup(outDir, '.html', '_bltxt', '_html');
	// HTML変換の固定ファイルはコピー不要なため設定から削除します
	assetHandler.remove('_html');
}
