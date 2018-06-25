
window {
	title = 'エラーダイアログ';	// ウィンドウタイトル
	locationX = 50.0;	// ウィンドウ表示位置X座標
	locationY = 50.0;	// ウィンドウ表示位置Y座標
	sizeW = 500.0;	// ウィンドウ横幅
	sizeH = 400.0;	// ウィンドウ縦幅
}

formVals {
	headerText = '';	// 説明文字列
	expanded = false;	// 詳細を表示するか
}

// 詳細を表示しないときの高さ
notExpanded.sizeH = 200;

// スタックトレースをフィルタリングするための検索文字列リスト
filterpatterns = [ /.*io\.github\.longfish801.*/, /.*cl_.*#.*\.groovy.*/ ];

