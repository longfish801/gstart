/*
 * DialogSingleSelect.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import io.github.longfish801.gstart.guiparts.dialog.FxDialog;

/**
 * エラーダイアログのサンプルです。
 */

try {
	Integer.parseInt('a');
} catch (exc){
	FxDialog.exceptionDialog('例外が発生しました。', exc);
}
