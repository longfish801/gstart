/*
 * hello.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import javax.swing.JOptionPane;
import io.github.longfish801.shared.util.ScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 動作確認用スクリプト。<br>
 * 実行するとメッセージダイアログを表示します。<br>
 * メッセージダイアログには文字列"Hello, World"を表示します。<br>
 * 本スクリプトへのパスをログに出力します。
 */

JOptionPane.showMessageDialog(null, 'Hello, World.');

Logger LOG = LoggerFactory.getLogger('io.github.longfish801');
LOG.info("${ScriptHelper.thisScript(this.class)}を実行しました。");
