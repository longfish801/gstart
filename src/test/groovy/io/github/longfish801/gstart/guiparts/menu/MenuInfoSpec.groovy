/*
 * MenuInfoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.menu;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * MenuInfoクラスのテスト。
 * @version 1.0.00 2017/07/25
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MenuInfoSpec extends Specification {
	def '他のメニューと重複しない、唯一のIDを返します'(){
		MenuInfo menuInfo;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		then:
		menuInfo.id == 'menu';
		
		when:
		menuInfo = new MenuInfo(key: 'menu', upper: new MenuInfo(key: 'parent'));
		then:
		menuInfo.id == 'parent/menu';
	}
	
	def 'サブメニューが存在するか否か判定を返します'(){
		MenuInfo menuInfo;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		then:
		menuInfo.hasSubMenu() == false;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		menuInfo << new MenuInfo(key: 'subMenu');
		then:
		menuInfo.hasSubMenu() == true;
	}
	
	def 'サブメニューを追加します'(){
		MenuInfo menuInfo;
		MenuInfo subMenuInfo;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		subMenuInfo = new MenuInfo(key: 'subMenu');
		menuInfo << subMenuInfo;
		then:
		subMenuInfo.upper == menuInfo;
		menuInfo.subMenus['subMenu'] == subMenuInfo;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		subMenuInfo = new MenuInfo(key: 'subMenu');
		menuInfo << subMenuInfo;
		menuInfo << subMenuInfo;
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'サブメニューを追加します'(){
		MenuInfo menuInfo;
		
		when:
		menuInfo = new MenuInfo(key: 'menu');
		then:
		menuInfo.toString() == null;
		
		when:
		menuInfo = new MenuInfo(key: 'menu', dispName: 'メニュー');
		then:
		menuInfo.toString() == 'メニュー';
	}
}
