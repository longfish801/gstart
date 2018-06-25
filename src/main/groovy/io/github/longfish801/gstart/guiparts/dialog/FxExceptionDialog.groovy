/*
 * FxExceptionDialog.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.dialog;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.util.ClassSlurper;
import javafx.stage.Window;

/**
 * エラーダイアログです。<br>
 * 例外発生時に、そのメッセージやスタックトレースを表示するためのダイアログです。
 * @version 1.0.00 2017/07/201
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FxExceptionDialog extends FxTextDialog {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(FxExceptionDialog.class);
	
	/**
	 * コンストラクタです。
	 * @param owner このダイアログの所有Window（省略時は null）
	 * @param headerText 説明
	 * @param exception 例外
	 * @see #setup(Window,String,Throwable)
	 */
	FxExceptionDialog(Window owner, String headerText, Throwable exception){
		super();
		setup(owner, headerText, exception);
	}
	
	/**
	 * 初期化します。<br>
	 * テキストエリアに、例外のスタックトレースを表示します。</p>
	 * 
	 * <p>もし起源となった例外があれば、以下の手順で再帰的に確認し、その例外について表示します。<br>
	 * 引数に指定された例外に getCauseメソッド（引数なし）が存在すること、
	 * その戻り値が nullではないことを確認します。<br>
	 * 存在すれば、getCauseメソッドを実行して起源となった例外を取得します。<br>
	 * 再帰的にこれをくりかえすことで、起源となった例外を探します。
	 * @param owner 親ウィンドウ
	 * @param headerText 説明
	 * @param exception 例外
	 */
	void setup(Window owner, String headerText, Throwable exception){
		ArgmentChecker.checkNotNull('例外', exception);
		
		// 起源となる例外を探します
		Closure digOriginalCause;
		digOriginalCause = { Throwable curExc ->
			Throwable cause = (curExc.class.methods.any { it.name == 'getCause' && it.parameterCount == 0 })? curExc.cause : null;
			return (cause != null)? digOriginalCause(cause) : curExc;
		}
		Throwable origin = digOriginalCause(exception);
		
		// スタックトレース文字列を生成します
		List stackTraces = origin.stackTrace.findAll { stack -> constants.filterpatterns.any { stack.toString().matches(it) } };
		String stackTraceText = stackTraces.collect { "\t" + it.toString() }.join("\n");
		if (stackTraceText.empty) stackTraceText = origin.stackTrace.collect { "\t" + it.toString() }.join("\n");
		
		super.setup(owner, headerText, "${origin.class.name}: ${origin.message}\n${stackTraceText}");
	}
}
