package com.m12i.regex;

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
		/**
		 * 初期状態.
		 */
		final Long from;
		/**
		 * 受理状態セット.
		 */
		final Long[] accepts;
		private final Paths paths = new Paths();
		
		/**
		 * 初期状態と受理状態セットをもとにオブジェクトを初期化する.
		 * @param from 初期状態
		 * @param accepts 受理状態セット
		 */
		Fragment(final Long from, final Long... accepts) {
			this.from = from;
			this.accepts = accepts;
		}
		
		/**
		 * 空文字（イプシロン）による状態遷移パスを追加する.
		 * @param to 受理状態セット
		 */
		void connectWithEpsilon(final Long[] to) {
			connectWithEpsilon(this.from, to);
		}
		/**
		 * 空文字（イプシロン）による状態遷移パスを追加する.
		 * @param from 初期状態
		 * @param to 受理状態セット
		 */
		void connectWithEpsilon(final Long from, final Long[] to) {
			final Long[] mem = paths.get(from);
			if (mem != null) {
				paths.put(from, Functions.concat(mem, to));
			} else {
				paths.put(from, to);
			}
		}
		/**
		 * 状態遷移パスを追加する.
		 * 初期状態はレシーバ・オブジェクトのそれが利用される。
		 * @param by 入力文字
		 * @param to 受理状態セット
		 */
		void connect(final Char by, final Long[] to) {
			connect(this.from, by, to);
		}
		/**
		 * 状態遷移パスを追加する.
		 * @param from 初期状態
		 * @param by 入力文字
		 * @param to 受理状態セット
		 */
		void connect(final Long from, final Char by, final Long[] to) {
			final Long[] mem = paths.get(from, by);
			if (mem != null) {
				paths.put(from, by, Functions.concat(mem, to));
			} else {
				paths.put(from, by, to);
			}
		}
		/**
		 * 状態遷移パス情報をコピーして取り込む.
		 * @param sources 取り込み元
		 */
		void include(final Fragment... sources) {
			for (final Fragment frag : sources) {
				this.paths.include(frag.paths);
			}
		}
		/**
		 * {@link NFA}オブジェクトを構築する.
		 * @return {@link NFA}オブジェクト
		 */
		NFA build() {
			return new NFA(this);
		}
	}
	
	private final Paths paths;
	final Long from;
	final Long[] accepts;
	
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
	Long[] transition(final Long from, final char by) {
		return paths.get(from, by);
	}
	/**
	 * 初期状態をキーにして状態遷移後の受理状態を返す.
	 * 入力文字は空文字（イプシロン）とみなす。
	 * @param from 初期状態
	 * @return 受理状態セット
	 */
	Long[] transition(final Long from) {
		return paths.get(from);
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
	String inspect() {
		final String lineSep = System.lineSeparator();
		final StringBuilder buff = new StringBuilder();
		buff.append("from: ").append(this.from).append(lineSep);
		buff.append("accepts: ").append(Functions.arrayList(this.accepts)).append(lineSep);
		buff.append("transitions: ").append(lineSep);
		buff.append(this.paths.inspect());
		return buff.toString();
	}
}
