package com.m12i.regex;

import java.util.regex.MatchResult;

/**
 * 正規表現オブジェクト.
 */
public final class Regex {
	/**
	 * パターンマッチの実行と結果の取得に利用されるオブジェクト.
	 * グルーピングには非対応です。
	 * {@link Regex#matcher(String)}により初期化されます。
	 */
	public static final class Matcher implements MatchResult {
		private int lastIndex = -1;
		private final Regex re;
		private final String input;
		private Matcher(final Regex re, final String input) {
			this.input = input;
			this.re = re;
		}
		@Override
		public int start(int group) {
			return group == 0 ? start() : -1;
		}
		@Override
		public int start() {
			return 0;
		}
		@Override
		public int groupCount() {
			return 0;
		}
		@Override
		public String group(int group) {
			return group == 0 ? group() : null;
		}
		@Override
		public String group() {
			if (lastIndex < 0) throw new IllegalStateException();
			return input.substring(0, lastIndex);
		}
		@Override
		public int end(int group) {
			return lastIndex;
		}
		@Override
		public int end() {
			return lastIndex;
		}
		/**
		 * 完全一致型のパターンマッチを試みる.
		 * @return パターンマッチの結果
		 */
		public boolean matches() {
			if (re.dfa.initializeRuntime().doesAccept(input)) {
				lastIndex = input.length();
				return true;
			} else {
				lastIndex = -1;
				return false;
			}
		}
		/**
		 * 前方一致型のパターンマッチを試みる.
		 * @return パターンマッチの結果
		 */
		public boolean lookingAt() {
			lastIndex = re.dfa.initializeRuntime().doesAcceptPrefix(input);
			return lastIndex > -1;
		}
	}
	/**
	 * 正規表現パターンをもとに正規表現オブジェクトを初期化して返す.
	 * @param pattern 正規表現パターン
	 * @return 正規表現オブジェクト
	 */
	public static Regex compile(final String pattern) {
		return new Regex(pattern);
	}
	
	private final DFA dfa;
	/**
	 * このオブジェクトのもととなった正規表現パターン.
	 */
	public final String pattern;
	
	private Regex(final String pattern){
		this.pattern = pattern;
		final Node node = new Parser(new Lexer(pattern)).parse();
		Functions.dump(node);
		final NFA.Fragment frag = node.assemble(IDFactory.create());
		final NFA nfa = frag.build();
		Functions.dump(nfa);
		this.dfa = nfa.transform();
	}
	
	/**
	 * パターンマッチの実行と結果の取得に使用するマッチャーを生成する.
	 * @param input 入力文字列（パターンマッチ対象）
	 * @return マッチャー
	 */
	public Matcher matcher(final String input) {
		return new Matcher(this, input);
	}
	/**
	 * 完全一致型のパターンマッチを試みる.
	 * @param input 入力文字列（パターンマッチ対象）
	 * @return 検証結果
	 */
	public boolean matches(final String input) {
		return matcher(input).matches();
	}
	/**
	 * 前方一致型のパターンマッチを試みる.
	 * @param input 入力文字列（パターンマッチ対象）
	 * @return 検証結果
	 */
	public boolean lookingAt(final String input) {
		return matcher(input).lookingAt();
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		final StringBuilder buff = new StringBuilder();
		buff.append("pattern: ")
		.append(Functions.stringLiteral(pattern))
		.append(System.lineSeparator())
		.append(this.dfa.inspect());
		return buff.toString();
	}
}
