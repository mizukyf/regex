package com.m12i.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 状態遷移のための入力文字をあらわすオブジェクト.
 * 空文字（イプシロン）やドットや文字クラスもこのオブジェクトで表現されます。
 */
final class Char {
	static final Char EPSILON = new Char(-1, null, false, false);
	static final Char DOT = new Char(-1, null, true, false);
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
			final Char newChar = new Char(c, null, false, false);
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
			return new Char(-1, cs, false, false);
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
			return new Char(-1, cs, false, true);
		}
	}
	
	private final int c;
	private final char[] cs;
	final boolean isEpsilon;
	final boolean isJustChar;
	final boolean isCharKlass;
	final boolean isDot;
	final boolean isNegative;
	
	private Char(final int c, final char[] cs, final boolean dot, final boolean nega) {
		this.c = c;
		this.cs = cs;
		this.isEpsilon = c < 0 && cs == null && !dot;
		this.isJustChar = c >= 0;
		this.isCharKlass = cs != null;
		this.isDot = dot;
		this.isNegative = nega;
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
		if (isJustChar) {
			return this.c == ch;
		} else if (isCharKlass && !isNegative) {
			for (final char c: cs) {
				if (c == ch) {
					return true;
				}
			}
			return false;
		} else if (isCharKlass && isNegative) {
			for (final char c: cs) {
				if (c == ch) {
					return false;
				}
			}
			return true;
		} else if (isDot) {
			return true;
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + Arrays.hashCode(cs);
		result = prime * result + (isCharKlass ? 1231 : 1237);
		result = prime * result + (isDot ? 1231 : 1237);
		result = prime * result + (isEpsilon ? 1231 : 1237);
		result = prime * result + (isJustChar ? 1231 : 1237);
		result = prime * result + (isNegative ? 1231 : 1237);
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
		if (c != other.c)
			return false;
		if (!Arrays.equals(cs, other.cs))
			return false;
		if (isCharKlass != other.isCharKlass)
			return false;
		if (isDot != other.isDot)
			return false;
		if (isEpsilon != other.isEpsilon)
			return false;
		if (isJustChar != other.isJustChar)
			return false;
		if (isNegative != other.isNegative)
			return false;
		return true;
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		if (isJustChar) {
			return Functions.charLiteral((char)c);
		} else if (isEpsilon) {
			return "(epsilon)";
		} else if (isDot) {
			return "(dot)";
		} else if (isCharKlass) {
			final StringBuilder buff = new StringBuilder();
			buff.append('[');
			if (isNegative) {
				buff.append('^');
			}
			for (final char c : cs) {
				buff.append(Functions.escapedChar(c));
			}
			buff.append(']');
			return buff.toString();
		} else {
			return "?";
		}
	}
}