/*
 * DialogSingleSelect.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import io.github.longfish801.gstart.guiparts.dialog.FxDialog;

/**
 * テキストダイアログのサンプルです。
 */

String text = '''\
	昔々、ある村でお爺さんとお婆さんが暮らしていました。
	ある日、お爺さんは山へ柴刈りにでかけました。
	お婆さんは川へ洗濯に行きました。'''.stripIndent();

FxDialog.textDialog('以下に例文を示します。', text);

