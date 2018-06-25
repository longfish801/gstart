/*
 * GstartSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * Gstartのテスト。
 * @version 1.0.00 2015/02/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class GstartSpec extends Specification {
	def '指定された名前のリソースを参照し、Groovyスクリプトとして実行します'(){
		given:
		Object result = null;
		
		when:
		result = new Gstart().runDSL('test.groovy', ['World'] as String[]);
		then:
		result == 'Hello, World!!';
	}
}
