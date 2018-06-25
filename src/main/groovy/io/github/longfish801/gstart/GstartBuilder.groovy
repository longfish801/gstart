/*
 * GstartBuilder.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * 各機能のインスタンスを保持します。
 * @version 1.0.00 2017/07/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class GstartBuilder extends LinkedHashMap {
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(GstartBuilder.class.classLoader);
	
	/**
	 * Gstartに各機能を設定します。<br>
	 * 引数となるクロージャには、コマンドライン引数を引数として渡します。
	 * @param closure Gstartに各機能を設定するためのクロージャ
	 */
	void gstart(Closure closure){
		ArgmentChecker.checkNotNull('クロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		closure();
	}
	
	/**
	 * 機能のインスタンスを生成します。<br>
	 * 機能設定として以下のキー、値を格納したマップを渡してください。</p>
	 * <dl>
	 * <dt>key</dt><dd>機能を特定するキー。必須。</dd>
	 * <dt>class</dt><dd>機能の実装クラス。必須。</dd>
	 * <dt>initial</dt><dd>機能を初期化するためのクロージャ。省略可。詳細は各機能の実装クラスを参照してください。</dd>
	 * </dl>
	 * @param setting 機能設定
	 */
	void addFunction(Map setting){
		ArgmentChecker.checkNotNull('機能設定', setting);
		String key = setting['key'];
		ArgmentChecker.checkNotBlank('key', key);
		ArgmentChecker.checkUniqueKey('key', key, this);
		String className = setting['class'];
		ArgmentChecker.checkNotBlank('class', className);
		put(key, shell.classLoader.loadClass(className).newInstance());
		if (setting['initial'] != null){
			ArgmentChecker.checkClass('initial', setting['initial'], Closure.class);
			Closure closure = setting['initial'];
			closure.delegate = get(key);
			closure.resolveStrategy = Closure.DELEGATE_FIRST;
			closure();
		}
	}
}
