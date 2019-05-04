package io.github.longfish801.user;

import io.github.longfish801.gstart.util.ScriptHelper;

class SomeClazz {
	String getScriptFileName(){
		File scriptFile = ScriptHelper.thisScript(SomeClazz.class);
		return scriptFile.getName();
	}
}

return new SomeClazz().getScriptFileName();

