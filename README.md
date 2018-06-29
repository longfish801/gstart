# gstart

　Groovyスクリプトのランチャです。

　詳細は[ドキュメント](https://longfish801.github.io/gitdoc/gstart/)を参照してください。

## 実行環境の作成

　以下のコマンドで releaseフォルダ配下に実行環境が作成されます。

~~~
gradle release
~~~

## Grapeのログ出力を強化

　Grape関連の動作で問題が生じた場合は、ログ出力を強化して調査します。  
　以下を参考にしました。

~~~
groovy grape verbose - Stack Overflow
https://stackoverflow.com/questions/3722280/groovy-grape-verbose
~~~

　gstart.l4j.iniに以下を追記します。

~~~
"-Divy.message.logger.level=4"
"-Dgroovy.grape.report.downloads=true"
~~~

　build.gradleについて headerTypeの指定を有効にします。

~~~
createExe {
	...
	headerType = 'console';
}
~~~

　gradle releaseを実行します。  
　生成された gstart.exeを介してスクリプトを実行します。  
　たとえば WashTxtを実行する場合、以下のコマンドを実行します。  
　stdout.logに Grapeのログが出力されます。

~~~
gstart.exe menu\WashTxt\main.gvy > stdout.log
~~~

## Groovyコマンドで実行

　menuフォルダ配下のスクリプトを groovyコマンドで実行する例を示します。  
　以下の例では Hello.gvyを実行しています。  
　事前に gradle releaseの実行が必要となります。

~~~
set CLASSPATH='.;lib/*;conf'
set JAVA_OPTS=-Dfile.encoding=UTF-8
groovy -c UTF-8 menu\サンプル\Hello.gvy
~~~

