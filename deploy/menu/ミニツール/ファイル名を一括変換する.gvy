/*
 * ファイル名を一括変換する.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

import groovy.swing.SwingBuilder;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import io.github.longfish801.gstart.guiparts.icon.AppIcon;
import io.github.longfish801.gstart.guiparts.dialog.SwingTextDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指定された正規表現に名前が一致するファイルをサブフォルダまで再帰的に走査して抽出します。
 * @param dir 探索対象のフォルダ
 * @param pattern ファイル名の正規表現
 * @param list 抽出結果を格納するリスト
 */
void scanDir(File dir, String pattern, List list) {
	Logger LOG = LoggerFactory.getLogger('io.github.longfish801');
	dir.eachFile { sub ->
		if (sub.isDirectory()){
			scanDir(sub, pattern, list);
		} else if (sub.isFile()){
			if (sub.name ==~ pattern) list.add(sub);
		} else {
			LOG.info("フォルダでもファイルでもないため無視します:${sub.absolutePath}");
		}
	}
}

/**
 * ファイル名の変換を実施するか事前確認、ならびに変換実行の処理をします。
 * @param swing SwingBuilder
 */
void check(SwingBuilder swing){
	Logger LOG = LoggerFactory.getLogger('io.github.longfish801');

	String root = swing.root.text;
	String pattern = swing.pattern.text
	String replacement = swing.replacement.text
	File rootDir = new File(root);
	if (!rootDir.isDirectory()){
		JOptionPane.showMessageDialog(swing.mainFrame, "指定されたルートフォルダは存在しないかフォルダではありません。\nroot=${root}");
		return;
	}

	List targetFiles = [];
	scanDir(rootDir, pattern, targetFiles);
	if (targetFiles.size() == 0){
		JOptionPane.showMessageDialog(swing.mainFrame, '変換の対象となるファイルがみつからないため、終了します。');
		return;
	}
	
	// 変換後のファイル名のマップを作成します
	Map replaceFnames = [:];
	targetFiles.each {
		replaceFnames[it] = it.name.replaceAll(pattern, replacement);
	}
	
	// 変換内容をダイアログに表示します
	StringBuilder buffer = new StringBuilder('');
	String curDir = '';
	for (File file : replaceFnames.keySet()){
		if (!curDir.equals(file.parentFile.absolutePath)){
			buffer.append("${file.parentFile.absolutePath}\n");
			curDir = file.parentFile.absolutePath;
		}
		buffer.append("\t${file.name} -> ${replaceFnames[file]}\n")
	}
	SwingTextDialog mappingDialog = new SwingTextDialog();
	mappingDialog.show(swing.mainFrame, "以下の通りファイル名を変換します。", buffer.toString());
	
	// 変換を実行するか確認し、OKであれば変換します
	int input = JOptionPane.showConfirmDialog(swing.mainFrame, "ファイル名を一括変換しますか？", "実行確認", JOptionPane.YES_NO_OPTION);
	mappingDialog.closeWindow();
	switch (input){
		case JOptionPane.YES_OPTION:
			List failList = [];
			for (File file : replaceFnames.keySet()){
				File newFile = new File(file.parentFile, replaceFnames[file]);
				if (!file.renameTo(newFile)) failList << file.absolutePath;
			}
			if (failList.size() > 0) LOG.info("以下のファイルについて、ファイル名の変換に失敗しました。\n\t${failList.join('\n\t')}\n");
			String errMsg = (failList.size() == 0)? '' : "\n一部のファイルについて変換に失敗しました。詳細はログを確認してください。";
			JOptionPane.showMessageDialog(swing.mainFrame, "ファイル名を一括変換しました。${errMsg}");
			break;
		case JOptionPane.NO_OPTION:
		default:
			JOptionPane.showMessageDialog(swing.mainFrame, "処理を終了します。");
			break;
	}
}

SwingBuilder swing = new SwingBuilder();
swing.frame(
		id: 'mainFrame', title: 'ファイル名を一括変換する',
		show: true, iconImage: AppIcon.icon,
		defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
		location: [50, 50], size: [550, 200]
	){
		borderLayout();
		
		panel(constraints: NORTH){
			label("<html>たとえば、拡張を logから txtに変更するなら、<br>変換対象のファイル名には「(.+)\\.log」を、変換後のファイル名には「\$1.txt」を指定してください。");
		}
		
		panel(
				constraints: CENTER,
				border: compoundBorder([ emptyBorder(5), titledBorder('変換条件')])
			){
			tableLayout {
				tr {
					td { label('ルートフォルダ') }
					td { textField('', id: 'root', columns: 30) }
				} 
				tr {
					td { label('変換対象のファイル名（正規表現で指定）') }
					td { textField('', id: 'pattern', columns: 20) }
				} 
				tr {
					td { label('変換後のファイル名') }
					td { textField('', id: 'replacement', columns: 20) }
				}
			}
		}
		
		panel(constraints: SOUTH){
			boxLayout(axis:BoxLayout.Y_AXIS);
			button(
					text: '変換内容を確認する',
					actionPerformed:{ check(swing); },
					defaultButton: true,
					alignmentX:0.5f
				);
		}
}
