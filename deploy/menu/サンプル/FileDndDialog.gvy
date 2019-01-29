/*
 * FileDndDialog.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import io.github.longfish801.gstart.guiparts.dialog.FxDialog;

/**
 * ファイル指定ダイアログのサンプルです。
 */

List<File> files = FxDialog.filesSelect('処理対象のファイルをドラッグ＆ドロップで指定してください。', []);
FxDialog.textDialog('選択結果は以下のとおり。', files.collect { it.name }.join("\n"));

