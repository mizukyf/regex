package com.m12i.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 決定性有限オートマトン（Deterministic Finite Automaton）.
 * 初期状態と受理状態、そして状態遷移パスを管理します。
 * このオブジェクトは{@link NFA}オブジェクトから導出されます。
 */
final class DFA {
	/**
	 * {@link DFA}を使用して入力文字列の評価を行うオブジェクト.
	 * このオブジェクトは正規表現パターンマッチの都度、
	 * {@link DFA}オブジェクトから導出・初期化されて入力文字列の評価に使用されます。
	 */
	static final class Runtime {
		private final DFA dfa;
		private long[] currentState;
		private Runtime(final DFA dfa) {
			this.dfa = dfa;
			currentState = dfa.froms;
		}
		/**
		 * 入力文字を利用して状態遷移を行う.
		 * @param by 入力文字
		 */
		void doTransition(final char by) {
			currentState = dfa.transition(currentState, by);
		}
		/**
		 * 入力文字列を評価して最終的に受理状態となるか検証した結果を返す.
		 * 入力文字列を文字シーケンスに分解して、{@link DFA}を使用して順次評価。
		 * 末尾まで評価したあとで最終的に受理状態となるかどうかを検証します。
		 * @param input 入力文字列
		 * @return 検証結果
		 */
		boolean doesAccept(final String input) {
			for (final char c : input.toCharArray()) {
				currentState = dfa.transition(currentState, c);
			}
			return dfa.accepts.contains(currentState);
		}
		/**
		 * 入力文字列を前方一致的に評価して中間段階もしくは最終段階で受理状態となるか検証した結果を返す.
		 * {@link #doesAccept(String)}とちがうのは入力文字列を構成する1文字1文字を処理した都度、
		 * 受理状態にあるかどうかを検証する点です。これにより前方一致パターンマッチを実現します。
		 * @param input 入力文字列
		 * @return 検証結果
		 */
		int doesAcceptPrefix(final String input) {
			final char[] cs = input.toCharArray();
			for (int i = 0; i < cs.length; i ++) {
				currentState = dfa.transition(currentState, cs[i]);
				if (dfa.accepts.contains(currentState)) {
					return i;
				}
			}
			return -1;
		}
	}
	static final class NonDisjoinSets {
		private final long[] inner;
		NonDisjoinSets(final long...es) {
			inner = es;
		}
		boolean contains(final long...other) {
			for (final long n : inner) {
				for (final long o : other) {
					if (n == o) return true;
				}
			}
			return false;
		}
	}
	/**
	 * 状態遷移キャッシュ.
	 * {@link DFA#transition(long[], char)}の処理結果をキャッシュして
	 * 同じパラメータで実行される2度目以降の処理の高速化を図ります。
	 */
	private static final class Cache {
		private static final class Key {
			final long[] froms;
			final char by;
			Key(final long[] froms, final char by) {
				this.froms = froms;
				this.by = by;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + by;
				result = prime * result + Arrays.hashCode(froms);
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
				if (!Arrays.equals(froms, other.froms))
					return false;
				return true;
			}
		}
		private final Map<Key, long[]> mem = new HashMap<DFA.Cache.Key, long[]>();
		long[] get(final long[] froms, final char by) {
			return mem.get(new Key(froms, by));
		}
		void put(final long[] froms, final char by, final long[] accepts) {
			mem.put(new Key(froms, by), accepts);
		}
	}

	private final NFA nfa;
	private final long[] froms;
	private final NonDisjoinSets accepts;
	private final Cache cache = new Cache();
	
	/**
	 * {@link NFA}オブジェクトをもとにDFAオブジェクトの初期化を行う.
	 * @param nfa {@link NFA}オブジェクト
	 */
	DFA(final NFA nfa) {
		this.nfa = nfa;
		this.froms = nfa.epsilonExpand();
		this.accepts = new NonDisjoinSets(nfa.accepts);
	}
	
	/**
	 * 初期状態と入力文字をもとに状態遷移を行い受理状態を返す.
	 * {@link NFA}とことなりDFAにおいては入力状態自体が集合となります。
	 * @param froms 初期状態
	 * @param by 入力文字
	 * @return 受理状態
	 */
	long[] transition(final long[] froms, final char by) {
		final long[] mem = cache.get(froms, by);
		if (mem != null) {
			return mem;
		} else {
			final Set<Long> set = new HashSet<Long>();
			for (final long from : froms) {
				set.addAll(Functions.set(nfa.transition(from, by)));
			}
			final long[] accepts = nfa.epsilonExpand(Functions.array(set));
			cache.put(froms, by, accepts);
			return accepts;
		}
	}
	/**
	 * このDFAオブジェクトをもとに{@link Runtime}オブジェクトを導出・初期化します.
	 * @return 初期化済みの{@link Runtime}オブジェクト
	 */
	Runtime initializeRuntime() {
		return new Runtime(this);
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		return nfa.inspect();
	}
}
