package com.m12i.regex;

import java.util.ArrayList;
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
		private Long[] currentState;
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
			final char[] chars = input.toCharArray();
			for (int i = 0; i < chars.length; i ++) {
				currentState = dfa.transition(currentState, chars[i]);
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
		private final Long[] inner;
		private NonDisjoinSets(final Long...es) {
			inner = es;
		}
		boolean contains(final Long...other) {
			for (final Long n : inner) {
				for (final Long o : other) {
					if (n == o) return true;
				}
			}
			return false;
		}
	}
	/**
	 * 状態遷移キャッシュ.
	 * {@link DFA#transition(Long[], char)}の処理結果をキャッシュして
	 * 同じパラメータで実行される2度目以降の処理の高速化を図ります。
	 */
	private static final class Cache {
		private static final class Key {
			final Long[] froms;
			final char by;
			private final int hash;
			Key(final Long[] froms, final char by) {
				this.froms = froms;
				this.by = by;
				// 実際上のイミュータブル・オブジェクトなのでこの時点でハッシュコードも確定する
				// ＊formsはミュータブルだが実際上書き換えは行われない
				this.hash = makeHashCode();
			}
			@Override
			public int hashCode() {
				return hash;
			}
			private int makeHashCode() {
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
		private final Map<Key, Long[]> mem = new HashMap<DFA.Cache.Key, Long[]>();
		Long[] get(final Long[] froms, final char by) {
			return mem.get(new Key(froms, by));
		}
		void put(final Long[] froms, final char by, final Long[] accepts) {
			mem.put(new Key(froms, by), accepts);
		}
	}

	private final NFA nfa;
	private final Long[] froms;
	private final NonDisjoinSets accepts;
	
	// 1次キャッシュ（DFAの初期状態と入力文字をキーにして受理状態セットを管理）
	private final Cache transitionCache = new Cache();
	// 2次キャッシュ（NFAの受理状態をキーにしてイプシロン展開後の受理状態セットを管理）
	private final Map<Long,ArrayList<Long>> epsilonExpandCache = new HashMap<Long, ArrayList<Long>>();
	
	/**
	 * {@link NFA}オブジェクトをもとにDFAオブジェクトの初期化を行う.
	 * @param nfa {@link NFA}オブジェクト
	 */
	DFA(final NFA nfa) {
		this.nfa = nfa;
		this.froms = epsilonExpand();
		this.accepts = new NonDisjoinSets(nfa.accepts);
	}
	
	/**
	 * 初期状態と入力文字をもとに状態遷移を行い受理状態を返す.
	 * {@link NFA}とことなりDFAにおいては入力状態自体が集合となります。
	 * @param froms 初期状態
	 * @param by 入力文字
	 * @return 受理状態
	 */
	Long[] transition(final Long[] froms, final char by) {
		// 与えられた初期状態と入力文字をキーにしてキャッシュを検索
		final Long[] cached = transitionCache.get(froms, by);
		if (cached != null) {
			// キャッシュに登録済み受理状態があればそれを返す
			return cached;
		} else {
			// キャッシュになければNFAオブジェクトを通じて状態遷移後の受理状態を取得する
			// 受理状態セットを一時的に格納するセットを初期化
			final Set<Long> acceptList = new HashSet<Long>();
			// DFAの初期状態（NFAの初期状態の集合）を使ってループ処理
			for (final Long from : froms) {
				// 初期状態と入力文字をキーにしてNFAオブジェクトに問い合わせ
				for (final Long accept : nfa.transition(from, by)) {
					// 取得できた受理状態をリストに登録
					if (acceptList.add(accept)) {
						// 受理状態をキーにしてイプシロン展開結果のキャッシュを検索
						final ArrayList<Long> expanded = epsilonExpandCache.get(accept);
						if (expanded != null) {
							//　キャッシュに登録済み展開結果があればそれを使用
							acceptList.addAll(expanded);
						} else {
							// 存在しない場合は展開処理を実施
							final ArrayList<Long> expandedNow = epsilonExpand(accept);
							// 結果をキャッシュに登録
							epsilonExpandCache.put(accept, expandedNow);
							// 展開結果を受理状態セットに追加
							acceptList.addAll(expandedNow);
						}
					}
				}
			}
			// セットから配列に変換
			final Long[] accepts = acceptList.toArray(new Long[acceptList.size()]);
			// 最終的にできあがった受理状態セットをキャッシュに登録
			transitionCache.put(froms, by, accepts);
			// 呼び出し元に返す
			return accepts;
		}
	}
	private ArrayList<Long> epsilonExpand(final Long seed) {
		// 処理済み初期状態を記録するためのセットを初期化
		final ArrayList<Long> done = new ArrayList<Long>();
		final ArrayList<Long> todo = new ArrayList<Long>();
		
		// 再帰的手続きのための起点となる要素を追加
		todo.add(seed);
		
		// 引数として渡された受理状態セットの未処理要素がなくなるまでループ
		while (!todo.isEmpty()) {
			// 要素（受理状態）を1つ取り出す
			final Long s = todo.remove(0);
			// 処理済みセットに登録
			done.add(s);
			// この受理状態を初期状態として空文字（イプシロン）により遷移可能な受理状態セットを取得
			final Long[] nexts = nfa.transition(s);
			// 結果がnullでなければ遷移可能な受理状態があるということ
			if (nexts != null) {
				// それらの状態セットについてループ処理
				for (final Long next : nexts) {
					// もし処理済みセットに存在しないものであれば処理待ちセットに登録
					if (!done.contains(next)) {
						todo.add(next);
					}
				}
			}
		}
		
		// 展開結果を呼び出し元に返す
		return done;
	}
	/**
	 * 空文字状態遷移を行う.
	 * {@link #epsilonExpand(Long[])}とのちがいは
	 * 入力となる処理待ち受理状態がレシーバに内包されたNFAオブジェクトから供給されることだけです。
	 * @return 受理状態およびそこから空文字（イプシロン）により遷移可能な受理状態のセット
	 */
	private Long[] epsilonExpand() {
		return epsilonExpand(nfa.from).toArray(new Long[0]);
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
