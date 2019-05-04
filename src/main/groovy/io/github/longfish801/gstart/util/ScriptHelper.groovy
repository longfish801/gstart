/*
 * ScriptHelper.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;

/**
 * スクリプトファイルに関するユーティリティです。
 * @version 1.0.00 2015/08/19
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ScriptHelper {
	/**
	 * スクリプトファイルのFileインスタンスを返します。
	 * @param clazz スクリプトファイルに定義されたClass
	 * @return スクリプトファイルのFileインスタンス
	 */
	static File thisScript(Class clazz) {
		ArgmentChecker.checkNotNull('clazz', clazz);
		return new File(clazz.protectionDomain.codeSource.location.file);
	}
	
	/**
	 * スクリプトファイルが格納されているフォルダのFileインスタンスを返します。
	 * @param clazz スクリプトファイルに定義されたClass
	 * @return スクリプトファイルが格納されているフォルダのFileインスタンス
	 */
	static File parentDir(Class clazz) {
		return thisScript(clazz).parentFile;
	}
}
