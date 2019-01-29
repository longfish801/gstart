/*
 * ClassConfigSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.util.logging.Slf4j;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import spock.lang.Specification;

/**
 * ClassConfigのテスト。
 * 
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassConfigSpec extends Specification {
	def 'newInstance：設定値文字列'(){
		given:
		String defConfig = '''\
			some.test = 'test'
			some.num = 123
			some.bool = true'''.stripIndent();
		
		when: 'データディレクトリが削除されること'
		ClassConfig config1 = ClassConfig.newInstance(ClassConfigSpec.class);
		config1.deleteDataDir();
		then:
		config1.getDataDir().isDirectory() == false;
		
		when:
		ClassConfig config2 = ClassConfig.newInstance(ClassConfigSpec.class, defConfig);
		then:
		config2.some.test == 'test';
		config2.some.num == 123;
		config2.some.bool == true;
		
		when: '設定値を、新しい値に上書きしたうえで保存しても例外が生じないこと'
		config2.some.key = 'This is New Value';
		config2.some.bool = false;
		config2.some.addKey = 0.12;
		config2.saveConfig();
		then:
		noExceptionThrown();
		
		when: '設定ファイルから新しい設定値を参照できること'
		ClassConfig config3 = ClassConfig.newInstance(ClassConfigSpec.class, defConfig);
		then:
		config3.some.key == 'This is New Value';
		config3.some.num == 123;
		config3.some.bool == false;
		config3.some.addKey == 0.12;
	}
	
	def 'newInstance：初期設定ファイル'(){
		when: 'データディレクトリが削除されること'
		ClassConfig config1 = ClassConfig.newInstance(ClassConfigSpec.class);
		config1.deleteDataDir();
		then:
		config1.getDataDir().isDirectory() == false;
		
		when: '初期設定値が参照されること'
		ClassConfig config2 = ClassConfig.newInstance(ClassConfigSpec.class);
		then:
		config2.key == 'This is TEST';
		config2.num == 123;
		config2.bool == true;
		
		when: '設定値を、新しい値に上書きしたうえで保存しても例外が生じないこと'
		config2.key = 'This is New Value';
		config2.bool = false;
		config2.addKey = 0.12;
		config2.saveConfig();
		then:
		noExceptionThrown();
		
		when: '設定ファイルから新しい設定値を参照できること'
		ClassConfig config3 = ClassConfig.newInstance(ClassConfigSpec.class);
		then:
		config3.key == 'This is New Value';
		config3.num == 123;
		config3.bool == false;
		config3.addKey == 0.12;
	}
	
	def 'コンストラクタ'(){
		when:
		new ClassConfig(ClassConfigSpec.class);
		then:
		noExceptionThrown();
		
		when:
		new ClassConfig(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'deleteDataDir'(){
		given:
		ClassConfig config = new ClassConfig(ClassConfigSpec.class);
		
		when:
		config.some = 'value';
		config.saveConfig();
		then:
		config.getDataDir().isDirectory() == true;
		
		when:
		config.some = 'value';
		config.saveConfig();
		config.deleteDataDir();
		then:
		config.getDataDir().isDirectory() == false;
	}
	
	def 'loadStringConfig'(){
		given:
		String defConfig = '''\
			some.test = 'test'
			some.num = 123
			some.bool = true'''.stripIndent();
		ClassConfig config = new ClassConfig(ClassConfigSpec.class);
		
		when:
		config.loadStringConfig(defConfig);
		then:
		config.some.test == 'test';
		config.some.num == 123;
		config.some.bool == true;
		
		when:
		config.loadStringConfig('');
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'loadResourceConfig'(){
		given:
		ClassConfig config = null;
		
		when:
		config = new ClassConfig(ClassConfigSpec.class);
		config.loadResourceConfig();
		then:
		config.key == 'This is TEST';
		config.num == 123;
		config.bool == true;
		
		when:
		config = new ClassConfig(InternalClass.class);
		config.loadResourceConfig();
		then:
		thrown(MultipleCompilationErrorsException);
	}
	
	def 'loadCurrentConfig'(){
		given:
		ClassConfig config = new ClassConfig(ClassConfigSpec.class);
		
		when:
		config.deleteDataDir();
		config.loadCurrentConfig();
		then:
		config.size() == 0;
		
		when:
		config.some = 'value';
		config.saveConfig();
		ClassConfig config2 = new ClassConfig(ClassConfigSpec.class);
		config2.loadCurrentConfig();
		then:
		config2.some == 'value';
	}
	
	def 'saveConfig'(){
		given:
		ClassConfig config = new ClassConfig(ClassConfigSpec.class);
		
		when:
		config.some = 'value';
		config.saveConfig();
		
		then:
		config.getConfFile().isFile() == true;
	}
	
	/**
	 * リソースにプロパティファイルがない場合のテスト用の内部クラス。
	 */
	class InternalClass { }
}
