/*
 * AppIcon.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gstart.guiparts.icon;

import groovy.util.logging.Slf4j;
import java.awt.Image;
import javafx.scene.image.Image as ImageFx;
import javax.imageio.ImageIO;
import io.github.longfish801.shared.ExchangeResource;

/**
 * アイコン画像を取得します。<br>
 * アイコン画像はリソース名"AppIcon.png"で参照します。
 * @version 1.0.00 2015/08/13
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AppIcon {
	/** ConfigObject */
	static final ConfigObject cnst = ExchangeResource.config(AppIcon.class);
	/** アイコン画像 */
	static Image icon = null;
	/** JavaFX向けのアイコン画像 */
	static ImageFx fxIcon = null;
	
	/**
	 * アイコン画像を取得します。
	 * @return アイコン画像
	 */
	static Image getIcon(){
		if (icon == null){
			URL url = ExchangeResource.url(AppIcon.class, cnst.extension);
			icon = ImageIO.read(url);
		}
		return icon;
	}
	
	/**
	 * JavaFX向けのアイコン画像を取得します。
	 * @return アイコン画像
	 */
	static ImageFx getFxIcon(){
		if (fxIcon == null){
			URL url = ExchangeResource.url(AppIcon.class, cnst.extension);
			fxIcon = new ImageFx(url.toString());
		}
		return fxIcon;
	}
}
