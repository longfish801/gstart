/*
 * GstartBuilderSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * GstartBuilderのテスト。
 * 
 * @version 1.0.00 2017/07/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class GstartBuilderSpec extends Specification {
	def 'Gstartに各機能を設定します'(){
		given:
		GstartBuilder builder = new GstartBuilder();
		
		when:
		builder.gstart({ -> });
		then:
		noExceptionThrown();
		
		when:
		builder.gstart(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '機能のインスタンスを生成します'(){
		given:
		GstartBuilder builder = null;
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'some', 'class': 'io.github.longfish801.gstart.GstartBuilderSpec$Some' ]);
		then:
		builder.some.thing == 'def';
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'some', 'class': 'io.github.longfish801.gstart.GstartBuilderSpec$Some', initial: { setThing 'abc' } ]);
		then:
		builder.some.thing == 'abc';
		
		when:
		builder = new GstartBuilder();
		builder.addFunction(null);
		then:
		thrown(IllegalArgumentException);
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: null, 'class': 'java.lang.String' ]);
		then:
		thrown(IllegalArgumentException);
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'string', 'class': 'java.lang.String' ]);
		builder.addFunction([ key: 'string', 'class': 'java.lang.String' ]);
		then:
		thrown(IllegalArgumentException);
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'string', 'class': 'java.lang.String' ]);
		builder.addFunction([ key: 'string', 'class': 'java.lang.String' ]);
		then:
		thrown(IllegalArgumentException);
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'string', 'class': ' ' ]);
		then:
		thrown(IllegalArgumentException);
		
		when:
		builder = new GstartBuilder();
		builder.addFunction([ key: 'some', 'class': 'io.github.longfish801.gstart.GstartBuilderSpec$Some', initial: 'some' ]);
		then:
		thrown(IllegalArgumentException);
	}
	
	/**
	 * テスト用のクラスです。
	 */
	class Some {
		String thing = 'def';
	}
}
