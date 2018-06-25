/*
 * SwingExceptionDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import io.github.longfish801.shared.util.ClassSlurper;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

/**
 * 例外を表示するためのダイアログです。
 * @version 1.0.00 2015/08/19
 * @author io.github.longfish801
 */
class SwingExceptionDialog extends SwingTextDialog {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(SwingExceptionDialog.class);
	
	/**
	 * ダイアログを表示します。<br>
	 * テキストダイアログには、例外のメッセージ内容とスタックトレースを表示します。
	 * @param owner 親コンポーネント
	 * @param exc 例外
	 * @param message メッセージ
	 * @see {SwingTextDialog#show(Component,Throwable,message)}
	 */
	void show(Component owner, Throwable exc, String message){
		Throwable origin = digOriginalCause(exc);
		List stackTraces = origin.stackTrace.findAll { stack -> constants.filterpatterns.any { stack.toString().matches(it) } };
		String stackTraceText = stackTraces.collect { "\t" + it.toString() }.join("\n");
		if (stackTraceText.empty) stackTraceText = origin.stackTrace.collect { "\t" + it.toString() }.join("\n");
		super.show(owner, message, "${origin.class.name}: ${origin.message}\n${stackTraceText}");
	}
	
	/**
	 * 再帰的に実行することで起源となった例外を返します。<br>
	 * 引数に指定された例外に getCauseメソッド（引数なし）が存在すること、
	 * その戻り値が nullではないことを確認します。<br>
	 * 存在すれば、getCauseメソッドを実行して起源となった例外を返します。<br>
	 * 存在しなければ引数に渡された例外をそのまま返します。<br>
	 * 再帰的にこれをくりかえすことで、起源となった例外を探します。
	 * @param exc 例外
	 * @return 起源となった例外
	 */
	protected Throwable digOriginalCause(Throwable exc){
		Throwable cause = (exc.class.methods.any { it.name == 'getCause' && it.parameterCount == 0 })? exc.cause : null;
		return (cause != null)? digOriginalCause(cause) : exc;
	}
}

