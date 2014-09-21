# Regexプロジェクトについて

このプロジェクトはDFAにもとづく正規表現APIを実装するものです。

実装はJavaで行っていますが、[hiratara](http://d.hatena.ne.jp/hiratara/)氏が
codezine.jpの[連載記事](http://codezine.jp/article/corner/237)において紹介されている
Pythonによる正規表現のエンジンの実装をもとにしています。

```java
import com.m12i.regex;

...

final Regex r = Regex.compile("hel+o");
final Matcher m = r.matcher("hello world.");
m.matches(); // => false
m.lookingAt(); // => true

```

PythonからJavaにポーティングするにあたりオブジェクト・グラフを若干変更していますが、現時点では基本的には前述の記事に紹介されるとおりの実装となっています。
初心の者にもたいへん理解しやすいテキストを執筆してくださった氏に感謝いたします。

初期バージョンコミット後に追加された機能は、`+`（プラス）による1回以上の繰り返し、`.`（ドット）によるワイルドカード、`[...]`（ブラケット）による文字クラスと否定文字クラスなどです。

