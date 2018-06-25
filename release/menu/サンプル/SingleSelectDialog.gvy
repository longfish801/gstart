/*
 * DialogSingleSelect.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import io.github.longfish801.gstart.guiparts.dialog.FxDialog;

/**
 * 選択ダイアログのサンプルです。
 */
String selected = FxDialog.singleSelect('以下から選択してください。', [ 'カレー', '焼肉', 'コロッケ' ]);
FxDialog.alertInformation("今夜は${selected}！");
