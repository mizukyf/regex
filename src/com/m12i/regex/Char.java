package com.m12i.regex;

import java.util.HashMap;
import java.util.Map;

/**
 * 状態遷移のための入力文字をあらわすオブジェクト.
 * 空文字（イプシロン）やドットや文字クラスもこのオブジェクトで表現されます。
 */
final class Char {
	static enum Kind {
		CHAR, KLASS, NEGATIVE_KLASS, EPSILON, DOT;
	}
	
	static final Char EPSILON = new Char(-1, null, Kind.EPSILON);
	static final Char DOT = new Char(-1, null, Kind.DOT);
	private static final Map<Character,Char> kharCache = new HashMap<Character,Char>();
	private static final Map<String,Char> klassCache = new HashMap<String,Char>();
	private static final Map<String,Char> negativeKlassCache = new HashMap<String,Char>();
	
	/**
	 * 通常の文字をあらわす{@link Char}オブジェクトを返す.
	 * @param c 文字
	 * @return {@link Char}オブジェクト
	 */
	static Char khar(final char c) {
		// もし同じ意味のクラスがすでに定義済みならそれを返す
		// ＊これにより同じ意味のオブジェクトが重複して生成されるのを防止する
		// ＊ただしこのロジックはスレッド・アンセーフである
		final Char cached = kharCache.get(c);
		if (cached != null) {
			// 定義済みインスタンスがあればそれを返す
			return cached;
		} else {
			// 未定義であれば新たにインスタンスを生成
			final Char result = new Char(c, null, Kind.CHAR);
			// キャッシュに登録してから呼び出し元に返す
			kharCache.put(c, result);
			return result;
		}
	}
	/**
	 * 文字クラスをあらわす{@link Char}オブジェクトを返す.
	 * 引数の{@code char[]}の要素は文字クラスに属する文字をあらわします。
	 * @param cs 文字クラスに属する文字配列
	 * @return {@link Char}オブジェクト
	 */
	static Char klass(final String klass) {
		// もし同じ意味のクラスがすでに定義済みならそれを返す
		// ＊これにより同じ意味のオブジェクトが重複して生成されるのを防止する
		// ＊ただしこのロジックはスレッド・アンセーフである
		final Char cached = klassCache.get(klass);
		if (cached != null) {
			// 定義済みインスタンスがあればそれを返す
			return cached;
		} else {
			// 未定義であれば新たにインスタンスを生成
			final Char result = new Char(-1, klass, Kind.KLASS);
			// キャッシュに登録してから呼び出し元に返す
			klassCache.put(klass, result);
			return result;
		}
	}
	/**
	 * 否定文字クラスをあらわす{@link Char}オブジェクトを返す.
	 * 引数の{@code char[]}の要素は文字クラスに属さない文字をあらわします。
	 * @param cs 文字クラスに属さない文字配列
	 * @return {@link Char}オブジェクト
	 */
	static Char negativeKlass(final String klass) {
		// もし同じ意味のクラスがすでに定義済みならそれを返す
		// ＊これにより同じ意味のオブジェクトが重複して生成されるのを防止する
		// ＊ただしこのロジックはスレッド・アンセーフである
		final Char cached = negativeKlassCache.get(klass);
		if (cached != null) {
			// 定義済みインスタンスがあればそれを返す
			return cached;
		} else {
			// 未定義であれば新たにインスタンスを生成
			final Char result = new Char(-1, klass, Kind.NEGATIVE_KLASS);
			// キャッシュに登録してから呼び出し元に返す
			negativeKlassCache.put(klass, result);
			return result;
		}
	}
	
	/**
	 * このオブジェクトの種別.
	 */
	final Kind kind;
	private final int c;
	private final String cs;
	private final int hash;
	
	private Char(final int c, final String cs, final Kind kind) {
		this.c = c;
		this.cs = cs;
		this.kind = kind;
		// ハッシュコードを計算する
		// ＊このオブジェクトはイミュータブルなのでハッシュコードはこの段階で確定する
		this.hash = makeHashCode();
	}
	
	/**
	 * 入力文字が{@link Char}オブジェクトがあらわす文字と適合するかどうか検証する.
	 * {@link Char}オブジェクトがあらわすものがなにかによって検証の仕方が変化します：
	 * <ul>
	 * <li>文字そのものをあらわす場合は単純な値比較が行われます。</li>
	 * <li>文字クラスをあらわす場合はクラスに属する文字との値比較が行われます。</li>
	 * <li>ドットをあわらす場合はいかなる入力文字も適合するものと見なされます。</li>
	 * <li>空文字（イプシロン）をあわらす場合はいかなる入力文字も適合しないものと見なされます。</li>
	 * </ul>
	 * @param ch 入力文字
	 * @return 検証結果
	 */
	boolean matches(final char ch) {
		if (kind == Kind.CHAR) {
			// 文字そのものの場合、入力文字との単純比較でOK
			return this.c == ch;
		} else if (kind == Kind.KLASS) {
			// 文字クラスの場合、文字集合（cs）のなかに入力文字が含まれればOK
			return -1 < cs.indexOf(ch);
		} else if (kind == Kind.NEGATIVE_KLASS) {
			// 否定文字クラスの場合、文字集合（cs）のなかに入力文字が含まれなければOK
			return -1 == cs.indexOf(ch);
		} else if (kind == Kind.DOT) {
			// ドットの場合、入力文字がいかなるものでもOK
			return true;
		} else {
			// それ以外の場合（空文字を想定）、入力文字がいかなるものでもNG
			return false;
		}
	}
	@Override
	public int hashCode() {
		return hash;
	}
	private int makeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + ((cs == null) ? 0 : cs.hashCode());
		result = prime * result + kind.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		// 等価性判定は参照そのものの比較でOK
		// ＊このオブジェクトは重複なし制御されているため参照比較のみでよい
		if (this == obj)
			return true;
		return true;
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		if (kind == Kind.CHAR) {
			return Functions.charLiteral((char)c);
		} else if (kind == Kind.EPSILON) {
			return "(epsilon)";
		} else if (kind == Kind.DOT) {
			return "(dot)";
		} else if (kind == Kind.KLASS) {
			final StringBuilder buff = new StringBuilder();
			buff.append('[');
			buff.append(cs);
			buff.append(']');
			return buff.toString();
		} else if (kind == Kind.NEGATIVE_KLASS) {
			final StringBuilder buff = new StringBuilder();
			buff.append('[');
			buff.append('^');
			buff.append(cs);
			buff.append(']');
			return buff.toString();
		} else {
			return "?";
		}
	}
}