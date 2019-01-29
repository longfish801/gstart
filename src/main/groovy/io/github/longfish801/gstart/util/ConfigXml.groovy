/*
 * ConfigXml.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.util;

import groovy.transform.InheritConstructors;
import groovy.util.logging.Slf4j;
import groovy.util.slurpersupport.GPathResult;
import groovy.xml.MarkupBuilder;
import io.github.longfish801.shared.ExchangeResource;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * ConfigObjectをXMLから参照／保存するためのクラスです。<br>
 * ConfigSlurperによるファイル入出力では、文字列長に上限があったり、
 * 半角円記号(\)の扱いに問題があったりするため XMLで保存できるようにしました。<br>
 * ConfigObjectに格納された値について、以下のデータ型に対応しています。</p>
 *
 * <ul>
 * <li>null</li>
 * <li>プリミティブ型（byte, short, int, long, float, double, char, boolean）</li>
 * <li>java.math.BigDecimal（Groovyでは、小数点を含む数値文字列は BigDecimalとなります）</li>
 * <li>java.lang.String</li>
 * <li>List (java.util.ArrayList)</li>
 * <li>Map (java.util.LinkedHashMap)</li>
 * </ul>
 *
 * <p>XML文法についての説明は省略します。<br>
 * 本クラスを通じた入出力を想定しており、手動での XML作成は考慮していません。
 * @version 1.0.00 2017/07/09
 * @author io.github.longfish801
 */
@Category(ConfigObject)
@Slf4j('LOG')
class ConfigXml {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(ConfigXml.class);
	
	/**
	 * ConfigObjectを文字出力ストリームにXML形式で書きこみます。
	 * @param writer 文字出力ストリーム
	 */
	void outputXml(Writer writer){
		Closure outputConfig = null;
		Closure outputObject = null;
		
		// ConfigObjectの内容をMarkupBuilderへ出力するためのクロージャです
		outputConfig = { ConfigObject config, MarkupBuilder builder ->
			builder.'group' {
				config.each { Object key, Object val ->
					builder.set {
						builder.key { (key instanceof ConfigObject)? outputConfig(key, builder) : outputObject(key, builder) }
						builder.val { (val instanceof ConfigObject)? outputConfig(val, builder) : outputObject(val, builder) }
					}
				}
			}
		}
		
		// ObjectをMarkupBuilderへ出力するためのクロージャです
		outputObject = { Object value, MarkupBuilder builder ->
			switch (value){
				case List:
					builder.'list'() { value.each { outputObject(it, builder) } }
					break;
				case Map:
					builder.'map'() {
						value.each { Object key, Object val ->
							builder.set {
								builder.key { outputObject(key, builder) }
								builder.val { outputObject(val, builder) }
							}
						}
					}
					break;
				case null:
					builder.'entry'(type: 'null');
					break;
				case Byte:
					builder.'entry'(type: 'byte', value.toString());
					break;
				case Short:
					builder.'entry'(type: 'short', value.toString());
					break;
				case Integer:
					builder.'entry'(type: 'int', value.toString());
					break;
				case Long:
					builder.'entry'(type: 'long', value.toString());
					break;
				case Float:
					builder.'entry'(type: 'float', value.toString());
					break;
				case Double:
					builder.'entry'(type: 'double', value.toString());
					break;
				case Character:
					builder.'entry'(type: 'char', value.toString());
					break;
				case Boolean:
					builder.'entry'(type: 'boolean', value.toString());
					break;
				case BigDecimal:
					builder.'entry'(type: 'java.math.BigDecimal', value.toString());
					break;
				default:
					builder.'entry'(type: 'java.lang.String', value.toString());
			}
		}
		
		MarkupBuilder builder = new MarkupBuilder(writer);
		builder.doubleQuotes = true;
		builder.mkp.xmlDeclaration(cnst.xmlDec);
		builder.'config' { outputConfig(this, builder) }
	}
	
	/**
	 * 文字入力ストリームからXMLを解析し ConfigObjectに読みこみます。
	 * @param reader 文字入力ストリーム
	 * @return ConfigObject
	 * @throws ParseConfigException XMLからConfigObjectへの読込に失敗しました。
	 */
	ConfigObject loadXml(Reader reader){
		Closure parseNode = null;
		Closure parseEntry = null;
		
		// ノード内容を解析しConfigObjectに読みこむクロージャです
		parseNode = { GPathResult node, ConfigObject config ->
			Object value = null;
			try {
				switch (node.name()){
					case 'config':
						node.'*'.each { parseNode(it, config) }
						break;
					case 'group':
						ConfigObject subConfig = new ConfigObject();
						node.'*'.each { GPathResult setNode ->
							Object key = parseNode(setNode.key, subConfig);
							Object val = parseNode(setNode.val, subConfig);
							config[key] = val;
						}
						value = config;
						break;
					case 'key':
						value = parseNode(node.entry, config);
						break;
					case 'val':
						value = parseNode(node.children().first(), config);
						break;
					case 'list':
						value = [];
						node.'*'.each { value << parseNode(it, config) }
						break;
					case 'map':
						value = [:];
						node.'*'.each { GPathResult setNode ->
							Object key = parseNode(setNode.key, config);
							Object val = parseNode(setNode.val, config);
							value[key] = val;
						}
						break;
					case 'entry':
						value = parseEntry(node);
						break;
					default:
						throw new ParseException("想定外のタグが記述されています。node=${node.name}");
				}
			} catch (exc){
				throw new ParseConfigException(exc, "XMLからConfigObjectへの読込に失敗しました。node=${node.name}, text=${node.toString()}, parent=${node.parent.name}");
			}
			return value;
		}
		
		// entryノード内容を解析し、結果をObjectとして返すクロージャです
		parseEntry = { GPathResult node ->
			Object value = null;
			switch (node.@type){
				case 'null':
					break;
				case 'byte':
					value = Byte.valueOf(node.text());
					break;
				case 'short':
					value = Short.valueOf(node.text());
					break;
				case 'int':
					value = Integer.valueOf(node.text());
					break;
				case 'long':
					value = Long.valueOf(node.text());
					break;
				case 'float':
					value = Float.valueOf(node.text());
					break;
				case 'double':
					value = Double.valueOf(node.text());
					break;
				case 'char':
					value = Character.valueOf(node.text() as char);
					break;
				case 'boolean':
					value = Boolean.valueOf(node.text());
					break;
				case 'java.math.BigDecimal':
					value = new BigDecimal(node.text());
					break;
				case 'java.lang.String':
					value = node.text();
					break;
				default:
					throw new ParseException("想定外のtype属性が記述されています。node=${node.name}, type=${node.@type}");
			}
			return value;
		}
		
		GPathResult xml = new XmlSlurper().parse(reader);
		parseNode(xml, this)
		return this;
	}
	
	@InheritConstructors
	class ParseConfigException extends InvocationTargetException { }
}
