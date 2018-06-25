/*
 * MenuInfoFileSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.menu;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassDirectory;
import spock.lang.Specification;

/**
 * MenuInfoFileクラスのテスト。
 * @version 1.0.00 2015/08/17
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MenuInfoFileSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(MenuInfoFileSpec.class);
	
	def '最上位のメニュー情報のためのコンストラクタ'(){
		given:
		MenuInfoFile menuInfo;
		
		when:
		menuInfo = new MenuInfoFile(new File(testDir, 'menu'));
		then:
		menuInfo.id == 'menu';
		
		when:
		menuInfo = new MenuInfoFile(new File(testDir, 'noSuchDir'));
		then:
		thrown(IOException);
	}
	
	def 'メニュー情報を再帰的に生成します'(){
		given:
		MenuInfoFile menuInfo;
		
		when:
		menuInfo = new MenuInfoFile(new File(testDir, 'menu'));
		then:
		menuInfo.subMenus.keySet() as List == [ 'submenu', 'テスト１' ];
		menuInfo.subMenus['submenu'].dispName == 'submenu';
		menuInfo.subMenus['テスト１'].dispName == 'テスト１';
		menuInfo.subMenus['テスト１'].file.name == 'テスト１.gvy';
		
		when:
		menuInfo = new MenuInfoFile(new File(testDir, 'menu'));
		then:
		menuInfo.subMenus['submenu'].subMenus.keySet() as List == [ 'テスト２', 'テスト３' ];
		menuInfo.subMenus['submenu'].subMenus['テスト３'].dispName == 'テスト３';
		menuInfo.subMenus['submenu'].subMenus['テスト３'].file.name == 'main.gvy';
		menuInfo.subMenus['submenu'].subMenus['テスト３'].id == 'menu/submenu/テスト３';
		menuInfo.subMenus['submenu'].subMenus['テスト２'].dispName == 'テスト２';
		menuInfo.subMenus['submenu'].subMenus['テスト２'].file.name == 'テスト２.gvy';
		menuInfo.subMenus['submenu'].subMenus['テスト２'].id == 'menu/submenu/テスト２';
	}
}
