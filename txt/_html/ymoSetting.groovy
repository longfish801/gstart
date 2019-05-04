/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import org.apache.commons.io.FilenameUtils;

yakumo.setting {
	// デフォルトのテンプレートファイルを上書きします
	fileFinder.find("${convDir.name}/template", ['*.html'], []).each {
		engine.templateHandler.load(FilenameUtils.getBaseName(it.key), it.value)
	}
}
