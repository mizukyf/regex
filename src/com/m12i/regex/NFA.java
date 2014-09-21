package com.m12i.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 非決定性有限オートマトン(Nondeterministic Finite Automaton).
 * 初期状態と受理状態、そして状態遷移パスを管理します。
 * このオブジェクトは{@link Fragment}オブジェクトから導出されます。
 */
final class NFA {
	/**
	 * NFAフラグメント.
	 * NFAを構成する各状態遷移の部分を表現します。
	 * このオブジェクトは{@link Node}オブジェクトから導出されます。
	 */
	static final class Fragment {
		
		final long from;
		final long[] accepts;
		private final Paths paths = new Paths();
		
		Fragment(final long from, final long... accepts) {
			this.from = from;
			this.accepts = accepts;
		}
		
		void connectWithEpsilon(final long[] to) {
			connectWithEpsilon(this.from, to);
		}
		void connectWithEpsilon(final long from, final long[] to) {
			final long[] mem = paths.get(from);
			if (mem != null) {
				paths.put(from, Functions.concat(mem, to));
			} else {
				paths.put(from, to);
			}
		}
		void connect(final Char by, final long[] to) {
			connect(this.from, by, to);
		}
		void connect(final long from, final Char by, final long[] to) {
			final long[] mem = paths.get(from, by);
			if (mem != null) {
				paths.put(from, by, Functions.concat(mem, to));
			} else {
				paths.put(from, by, to);
			}
		}
		void include(final Fragment... fragments) {
			for (final Fragment frag : fragments) {
				this.paths.include(frag.paths);
			}
		}
		NFA build() {
			return new NFA(this);
		}
	}
	static final class Char {
		static final Char EPSILON = new Char(-1, null, false, false);
		static final Char DOT = new Char(-1, null, true, false);
		private static final Map<Character,Char> justCharCache = new HashMap<Character,Char>();
		private static final Map<char[],Char> charKlassCache = new HashMap<char[],Char>();
		private static final Map<char[],Char> negaCharKlassCache = new HashMap<char[],Char>();
		
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
		static Char klass(final char[] cs) {
			final Char mem = charKlassCache.get(cs);
			if (mem != null) {
				return mem;
			} else {
				return new Char(-1, cs, false, false);
			}
		}
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
		
		boolean matches(final char ch) {
			if (isJustChar) {
				return this.c == ch;//TODO
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
				throw new IllegalStateException();
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
		public String format() {
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
					buff.append('!');
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
	
	private final Paths paths;
	final long from;
	final long[] accepts;
	
	private NFA(Fragment fragment) {
		this.paths = fragment.paths;
		this.from = fragment.from;
		this.accepts = fragment.accepts;
	}
	
	/**
	 * 初期状態と入力文字をキーにして状態遷移後の受理状態を返す.
	 * @param from 初期状態
	 * @param by 入力文字
	 * @return 受理状態セット
	 */
	long[] transition(final long from, final char by) {
		return paths.get(from, by);
	}
	/**
	 * 初期状態をキーにして状態遷移後の受理状態を返す.
	 * 入力文字は空文字（イプシロン）とみなす。
	 * @param from 初期状態
	 * @return 受理状態セット
	 */
	long[] transition(final long from) {
		return paths.get(from);
	}
	/**
	 * 空文字状態遷移を行う.
	 * 初期状態セットを受け取り、それら初期状態および初期状態から空文字（イプシロン）により
	 * 状態遷移可能な状態のすべてを内包するセットを返します.
	 * @param states 初期状態
	 * @return 初期状態およびそこから空文字（イプシロン）により遷移可能な状態のセット
	 */
	long[] epsilongExpand(final long[] states) {
		final Queue<Long> todo = Functions.queue(states);
		final Set<Long> done = new HashSet<Long>();
		
		while (!todo.isEmpty()) {
			final long s = todo.poll();
			final long[] nexts = transition(s);
			done.add(s);
			if (nexts != null) {
				for (final long next : nexts) {
					if (!done.contains(next)) {
						todo.add(next);
					}
				}
			}
		}
		
		return Functions.array(done);
	}
	/**
	 * 空文字状態遷移を行う.
	 * {@link #epsilongExpand(long[])}とのちがいは
	 * 入力となる初期状態がレシーバのNFAオブジェクトから供給されることだけです。
	 * @return 初期状態およびそこから空文字（イプシロン）により遷移可能な状態のセット
	 */
	long[] epsilongExpand() {
		return epsilongExpand(Functions.array(from));
	}
	/**
	 * NFAオブジェクトをもとにして{@link DFA}オブジェクトを導出する.
	 * @return DFAオブジェクト
	 */
	DFA transform() {
		return new DFA(this);
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String format() {
		final String lineSep = System.lineSeparator();
		final StringBuilder buff = new StringBuilder();
		buff.append("from: ").append(this.from).append(lineSep);
		buff.append("accepts: ").append(Functions.arrayList(this.accepts)).append(lineSep);
		buff.append("transitions: ").append(lineSep);
		buff.append(Paths.format(this.paths));
		return buff.toString();
	}
}
