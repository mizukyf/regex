package com.m12i.regex;

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
	 * 状態遷移パスセット.
	 * 初期状態と入力文字をキーにして受理状態を管理します。
	 * このオブジェクトは{@link NFA}と{@link Fragment}で使用されます。
	 */
	static final class Paths {
		private static final class Key {
			final long from;
			final int by;
			private Key(final long from, final char by) {
				this.from = from;
				this.by = by;
			}
			private Key(final long from) {
				this.from = from;
				this.by = -1;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + by;
				result = prime * result + (int) (from ^ (from >>> 32));
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
				Key other = (Key) obj;
				if (by != other.by)
					return false;
				if (from != other.from)
					return false;
				return true;
			}
		}
		static String format(Paths paths) {
			final String lineSep = System.lineSeparator();
			final StringBuilder buff = new StringBuilder();
			for (final Key k : paths.mem.keySet()) {
				if (buff.length() > 0) {
					buff.append(lineSep);
				}
				if (k.by < 0) {
					buff.append(String.format("(from: %s, by: -, accepts: %s)", 
							k.from, 
							Functions.arrayList(paths.mem.get(k))));
				} else {
					buff.append(String.format("(from: %s, by: %s, accepts: %s)", 
							k.from, 
							Functions.charLiteral((char) k.by),
							Functions.arrayList(paths.mem.get(k))));
				}
			}
			return buff.toString();
		}
		
		private final Map<Key, long[]> mem = new HashMap<NFA.Paths.Key, long[]>();
		long[] get(final long from, final char by) {
			return mem.get(new Key(from, by));
		}
		long[] get(final long from) {
			return mem.get(new Key(from));
		}
		void put(final long from, final char by, final long[] accepts) {
			mem.put(new Key(from, by), accepts);
		}
		void put(final long from, final long[] accepts) {
			mem.put(new Key(from), accepts);
		}
		void include(final Paths that) {
			this.mem.putAll(that.mem);
		}
	}
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
		
		void connect(final long[] to) {
			connect(this.from, to);
		}
		void connect(final long from, final long[] to) {
			final long[] mem = paths.get(from);
			if (mem != null) {
				paths.put(from, Functions.concat(mem, to));
			} else {
				paths.put(from, to);
			}
		}
		void connect(final char by, final long[] to) {
			connect(this.from, by, to);
		}
		void connect(final long from, final char by, final long[] to) {
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
