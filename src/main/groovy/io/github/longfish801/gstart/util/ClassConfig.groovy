/*
 * ClassConfig.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.shared.PackageDirectory;
import org.apache.commons.io.FileUtils;

/**
 * 値を変更可能なクラス単位の設定値です。<br>
 * 初期値は設定値文字列として指定するか、リソースから参照します。<br>
 * 設定値文字列は{@link ConfigSlurper}で解析可能かつ{@link ConfigXml}で扱えるデータ型を定義してください。<br>
 * リソースとして定義する場合、設定値文字列を初期設定ファイルとして保存してください。<br>
 * 初期設定ファイルはクラスの単純名＋拡張子「.groovy」をリソース名として{@link ExchangeResource#config(Class)}で参照します。<br>
 * 初期設定ファイルが存在しない場合は参照しません。</p>
 * 
 * <p>設定値は設定ファイルとしてデータフォルダに保存します。<br>
 * 設定ファイルは、データフォルダに XML形式のプロパティファイルとして保存します。<br>
 * ルートフォルダとして、デフォルトではシステムプロパティ"java.io.tmpdir"で参照されるフォルダを利用します。
 * ルートフォルダは作成済であることを前提とします。<br>
 * ルートフォルダの直下にルートデータフォルダ gstartフォルダを作成します。</p>
 * ルートデータフォルダ直下に、クラスの正規名でフォルダを作成します。これを、そのクラスに固有のデータフォルダとします。<br>
 * データフォルダには設定ファイル config.xmlを作成します。{@link ConfigXml}を介してXML形式で保存します。
 * @version 1.0.00 2016/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassConfig extends ConfigObject {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(ClassConfig.class);
	/** ルートフォルダ */
	static File rootDir = new File(System.getProperty(cnst.syspropTmpdir));
	/** データフォルダ */
	File dataDir = null;
	/** 設定ファイル */
	File confFile = null;
	/** 設定対象のクラス */
	Class clazz = null;
	
	/**
	 * 設定値文字列を初期設定値としたClassConfigインスタンスを返します。<br>
	 * 設定値文字列から初期設定値を読みこみます。<br>
	 * 加えて、クラス設定ファイルが存在すれば初期設定値に上書きします。<br>
	 * 参照に失敗した場合は ERRORログを出力します。
	 * @param clazz 設定対象のクラス
	 * @param defConfig 設定値文字列
	 * @return ClassConfigインスタンス（読込失敗時は null）
	 */
	static ClassConfig newInstance(Class clazz, String defConfig){
		ClassConfig config = null;
		try {
			config = new ClassConfig(clazz);
			config.loadStringConfig(defConfig);
			config.loadCurrentConfig();
		} catch (exc){
			LOG.error('設定値の読込に失敗しました。clazz={} defConfig={}', clazz, defConfig, exc);
		}
		return config;
	}
	
	/**
	 * リソース上の初期設定ファイルを初期設定値としたClassConfigインスタンスを返します。<br>
	 * 初期設定ファイルが存在すれば初期設定値を読みこみます。<br>
	 * 加えて、クラス設定ファイルが存在すれば初期設定値に上書きします。<br>
	 * 参照に失敗した場合は ERRORログを出力します。
	 * @param clazz 設定対象のクラス
	 * @return ClassConfigインスタンス（読込失敗時はnull）
	 */
	static ClassConfig newInstance(Class clazz){
		ClassConfig config = null;
		try {
			config = new ClassConfig(clazz);
			config.loadResourceConfig();
			config.loadCurrentConfig();
		} catch (exc){
			LOG.error('設定値の読込に失敗しました。clazz={}', clazz, exc);
		}
		return config;
	}
	
	/**
	 * コンストラクタ。
	 * @param clazz 設定対象のクラス
	 */
	ClassConfig(Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		this.clazz = clazz;
		File rootDataDir = new File(rootDir, cnst.rootDataDirname);
		dataDir = PackageDirectory.flatDir(rootDataDir, clazz);
		confFile = new File(dataDir, cnst.confFileName);
	}
	
	/**
	 * データフォルダを削除します。<br>
	 * データフォルダ内のサブフォルダやファイルもすべて削除します。<br>
	 * データフォルダが存在しない場合はなにもしません。
	 */
	void deleteDataDir() {
		if (!dataDir.isDirectory()) return;
		FileUtils.deleteDirectory(dataDir);
	}
	
	/**
	 * 設定値文字列を読みこみます。
	 * @param defConfig 設定値文字列
	 */
	void loadStringConfig(String defConfig){
		ArgmentChecker.checkNotBlank('defConfig', defConfig);
		merge(new ConfigSlurper().parse(defConfig));
	}
	
	/**
	 * リソース上に初期設定ファイルが存在すれば読みこみます。<br>
	 * 初期設定ファイルが存在しない場合は読みこみません。<br>
	 * 初期設定ファイルが存在しなかった旨を WARNログとして出力します。
	 */
	void loadResourceConfig(){
		ConfigObject config = ExchangeResource.config(clazz);
		if (config == null){
			LOG.warn('リソース上に初期設定ファイルが存在しません。clazz={}', clazz);
			return;
		}
		merge(config);
	}
	
	/**
	 * データフォルダに設定ファイルが存在すれば読みこみます。
	 */
	void loadCurrentConfig(){
		if (!confFile.isFile()) return;
		new FileReader(confFile).withCloseable { reader ->
			use(ConfigXml){ loadXml(reader) }
		}
	}
	
	/**
	 * 設定値をデータフォルダ内の設定ファイルへ保存します。<br>
	 * もし未作成であればデータフォルダを作成します。
	 */
	void saveConfig(){
		setupDataDir();
		new FileWriter(confFile).withCloseable { writer ->
			use(ConfigXml){ outputXml(writer) }
		}
	}
	
	/**
	 * もし未作成であればデータフォルダを作成します。<br>
	 * データフォルダ作成時はその旨を INFOログとして出力します。
	 * @throws IOException データフォルダの作成に失敗しました。
	 */
	protected void setupDataDir(){
		if (dataDir.isDirectory()) return;
		if (!dataDir.mkdirs()) throw new IOException("データフォルダの作成に失敗しました。clazz=${clazz}, dataDir=${dataDir}");
		LOG.info('データフォルダを作成しました。dataDir={}', dataDir);
	}
}
