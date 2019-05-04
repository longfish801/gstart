package io.github.longfish801.user;

import io.github.longfish801.gstart.util.ScriptHelper;

class SomeClazz {
	String getScriptDirName(){
		File scriptDir = ScriptHelper.parentDir(SomeClazz.class);
		return scriptDir.getName();
	}
}

return new SomeClazz().getScriptDirName();

