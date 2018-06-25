/*
 * MyGroovyConsole.groovy
 *
 * Copyright (C) jp.coocan.cute.longfish All Rights Reserved.
 */
package io.github.longfish801.gstart.scriptconsole;

import groovy.ui.Console;
import groovy.ui.text.*;
import groovy.util.logging.Slf4j;
import java.awt.FontMetrics;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.codehaus.groovy.runtime.StackTraceUtils;

/**
 * GroovyConsoleをカスタマイズしたクラスです。<br>
 * タブを入力可能にしました。
 * @version 1.0.00 2017/07/25
 * @author jp.coocan.cute.longfish
 */
@Slf4j('LOG')
class MyGroovyConsole extends Console {
	/**
	 * コンストラクタ。
	 * @param parent ClassLoader
	 */
	MyGroovyConsole(ClassLoader parent) {
		super(parent);
	}
	
	/**
	 * GroovyConsoleを起動し、指定されたファイルを開きます。<br>
	 * {@link GroovyConsole#main(String[])}を参考に作成しました。<br>
	 * GroovyConsoleの代わりにMyGroovyConsoleのインスタンスを生成しています。<br>
	 * --helpオプションの動作は削除しました。
	 */
	static void exec(File file){
		// full stack trace should not be logged to the output window - GROOVY-4663
		java.util.logging.Logger.getLogger(StackTraceUtils.STACK_LOG_NAME).useParentHandlers = false;
		//when starting via main set the look and feel to system
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Console console = new MyGroovyConsole(Console.class.classLoader?.getRootLoader());
		console.useScriptClassLoaderForScriptExecution = true;
		console.run();
		if (file != null) console.loadScriptFile(file);
	}
	
	/**
	 * {@inheritDoc}
	 * タブをスペースに置換しないよう修正しました。
	 */
	void run() {
		super.run();
		
		// タブをスペースと解釈しないようTextEditorを設定します
		inputEditor.getTextEditor().isTabsAsSpaces(false);
		
		// GroovyFilterはタブをスペースへ置換してしまうため、代わりにMyGroovyFilterを使用します
		DefaultStyledDocument doc = inputEditor.getTextEditor().getDocument();
		doc.setDocumentFilter(new MyGroovyFilter(doc));
		inputEditor.getTextEditor().setDocument(doc);
		
		// タブの表示サイズを変更します
		setTabSize(inputEditor.getTextEditor() as JTextPane, 4);
	}
	
	/**
	 * 入力したタブをスペースに置換しないようGroovyFilterの動作を改変したクラスです。
	 */
	class MyGroovyFilter extends GroovyFilter {
		/**
		 * コンストラクタ。
		 * @param doc DefaultStyledDocument
		 */
		MyGroovyFilter(DefaultStyledDocument doc){
			super(doc);
		}
		
		/**
		 * {@inheritDoc}
		 * タブをスペースに置換しないよう修正しました。
		 */
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException {
			fb.insertString(offset, text, attrs);
			parseDocument(offset, text.length());
		}
		
		/**
		 * {@inheritDoc}
		 * タブをスペースに置換しないよう修正しました。
		 */
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			fb.replace(offset, length, text, attrs);
			parseDocument(offset, text.length());
		}
	}
	
	/**
	 * タブの表示サイズを変更します。
	 * @param jtextPane 変更対象となるJTextPane
	 * @param tabSize タブの表示サイズ
	 */
	static void setTabSize(JTextPane jtextPane, int tabSize){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					FontMetrics fontMetrics = jtextPane.getFontMetrics(jtextPane.getFont());
					int charWidth = fontMetrics.charWidth('m' as char);
					int tabLength = charWidth * tabSize;
					int tabsNum = (jtextPane.getWidth() / tabLength) - 1;
					TabStop[] tabs = new TabStop[tabsNum];
					for(int j = 0; j < tabs.length; j++) {
						tabs[j] = new TabStop((j + 1) * tabLength);
					}
					TabSet tabSet = new TabSet(tabs);
					SimpleAttributeSet attrs = new SimpleAttributeSet();
					StyleConstants.setTabSet(attrs, tabSet);
					jtextPane.getStyledDocument().setParagraphAttributes(0, jtextPane.getDocument().getLength(), attrs, false);
				} catch (exc){
					LOG.error('タブの表示サイズ変更に失敗しました。', exc);
				}
			}
		});
	}
}
