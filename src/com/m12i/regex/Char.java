package com.m12i.regex;

import java.util.HashMap;
import java.util.Map;

/**
 * 状態遷移のための入力文字をあらわすオブジェクト.
 * 空文字（イプシロン）やドットや文字クラスもこのオブジェクトで表現されます。
 */
final class Char {
	static enum Kind {
		JUST_CHAR, KLASS, NEGATIVE_KLASS, EPSILON, DOT;
	}
	
//	static final Char EPSILON = new Char(-1, null, false, false);
//	static final Char DOT = new Char(-1, null, true, false);
	static final Char EPSILON = new Char(-1, null, Kind.EPSILON);
	static final Char DOT = new Char(-1, null, Kind.DOT);
	private static final Map<Character,Char> justCharCache = new HashMap<Character,Char>();
	private static final Map<char[],Char> charKlassCache = new HashMap<char[],Char>();
	private static final Map<char[],Char> negaCharKlassCache = new HashMap<char[],Char>();
	
	/**
	 * 通常の文字をあらわす{@link Char}オブジェクトを返す.
	 * @param c 文字
	 * @return {@link Char}オブジェクト
	 */
	static Char just(final char c) {
		final Char mem = justCharCache.get(c);
		if (mem != null) {
			return mem;
		} else {
//			final Char newChar = new Char(c, null, false, false);
			final Char newChar = new Char(c, null, Kind.JUST_CHAR);
			justCharCache.put(c, newChar);
			return newChar;
		}
	}
	/**
	 * 文字クラスをあらわす{@link Char}オブジェクトを返す.
	 * 引数の{@code char[]}の要素は文字クラスに属する文字をあらわします。
	 * @param cs 文字クラスに属する文字配列
	 * @return {@link Char}オブジェクト
	 */
	static Char klass(final char[] cs) {
		final Char mem = charKlassCache.get(cs);
		if (mem != null) {
			return mem;
		} else {
//			return new Char(-1, String.valueOf(cs), false, false);
			return new Char(-1, String.valueOf(cs), Kind.KLASS);
		}
	}
	/**
	 * 否定文字クラスをあらわす{@link Char}オブジェクトを返す.
	 * 引数の{@code char[]}の要素は文字クラスに属さない文字をあらわします。
	 * @param cs 文字クラスに属さない文字配列
	 * @return {@link Char}オブジェクト
	 */
	static Char negativeKlass(final char[] cs) {
		final Char mem = negaCharKlassCache.get(cs);
		if (mem != null) {
			return mem;
		} else {
//			return new Char(-1, String.valueOf(cs), false, true);
			return new Char(-1, String.valueOf(cs), Kind.NEGATIVE_KLASS);
		}
	}
	
	final Kind kind;
	private final int c;
	private final String cs;
//	final boolean isEpsilon;
//	final boolean isJustChar;
//	final boolean isCharKlass;
//	final boolean isDot;
//	final boolean isNegative;
	
	private Char(final int c, final String cs, final Kind kind) {
		this.c = c;
		this.cs = cs;
//		this.isEpsilon = c < 0 && cs == null && !dot;
//		this.isJustChar = c >= 0;
//		this.isCharKlass = cs != null;
//		this.isDot = dot;
//		this.isNegative = nega;
		this.kind = kind;
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
		if (kind == Kind.JUST_CHAR) {
			return this.c == ch;
		} else if (kind == Kind.KLASS) {
			return -1 < cs.indexOf(ch);
		} else if (kind == Kind.NEGATIVE_KLASS) {
			return -1 == cs.indexOf(ch);
		} else if (kind == Kind.DOT) {
			return true;
		} else {
			return false;
		}
//		if (isJustChar) {
//			return this.c == ch;
//		} else if (isCharKlass && !isNegative) {
////			for (final char c: cs) {
////				if (c == ch) {
////					return true;
////				}
////			}
////			return false;
//			return -1 < cs.indexOf(ch);
//		} else if (isCharKlass && isNegative) {
////			for (final char c: cs) {
////				if (c == ch) {
////					return false;
////				}
////			}
////			return true;
//			return -1 == cs.indexOf(ch);
//		} else if (isDot) {
//			return true;
//		} else {
//			return false;
//		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + ((cs == null) ? 0 : cs.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Char other = (Char) obj;
		if (kind != other.kind)
			return false;
		if (c != other.c)
			return false;
		if (cs == null) {
			if (other.cs != null)
				return false;
		} else if (!cs.equals(other.cs))
			return false;
		return true;
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		if (kind == Kind.JUST_CHAR) {
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