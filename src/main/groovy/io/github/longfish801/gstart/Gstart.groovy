/*
 * Gstart.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassSlurper;

/**
 * スタートアップスクリプトを実行します。<br>
 * スタートアップスクリプトは、リソース名 "build.groovy"で参照される Groovyスクリプトです。
 * @version 1.0.00 2017/02/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Gstart {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(Gstart.class);
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(Gstart.class.classLoader);
	
	/**
	 * メインメソッド。<br>
	 * リソース名 "build.groovy"で参照される Groovyスクリプトを実行します。<br>
	 * 処理の開始時、終了時、例外発生時にINFOログを出力します。
	 * @param args コマンドライン引数
	 */
	static void main(args){
		LOG.info('スタートアップスクリプトを実行します。args={}', Arrays.toString(args));
		Object result = null;
		try {
			result = new Gstart().runDSL(constants.resourceName, args);
		} catch (Throwable exc){
			LOG.error('スタートアップスクリプトの実行に失敗しました。resourceName={}', constants.resourceName, exc);
		} finally {
			LOG.info('スタートアップスクリプトを実行しました。args={}, result={}', Arrays.toString(args), result);
		}
	}
	
	/**
	 * 指定された名前のリソースを参照し、Groovyスクリプトとして実行します。
	 * @param name リソース名
	 * @param args コマンドライン引数
	 * @return Groovyスクリプトの実行結果
	 */
	Object runDSL(String name, String[] args){
		URL url = new ExistResource(Gstart.class).get(name);
		return shell.run(url.toURI(), args);
	}
}
