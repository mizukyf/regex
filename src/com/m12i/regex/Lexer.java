package com.m12i.regex;

/**
 * 正規表現パターンの字句解析器.
 */
final class Lexer {
	private final int len;
	private final char[] chars;
	private int pos = 0;
	
	/**
	 * パターン文字列により字句解析器を初期化する.
	 * @param input パターン文字列
	 */
	Lexer(final String input) {
		chars = input.toCharArray();
		len = chars.length;
	}
	
	/**
	 * 次の文字を読み取って返す.
	 * {@code '\\'}（バックスラッシュ）をエスケープ文字とみなして解析を行います。
	 * パターン文字列の末端まで読み取り終えたあとは{@link Token#EOF}を返します。
	 * @return 次の文字
	 */
	Token scan() {
		if (pos == len) {
			return Token.EOF;
		}
		final char ch = chars[pos ++];
		switch (ch) {
		case '\\':
			return Token.charToken(chars[pos ++]);
		case '.':
			return Token.DOT;
		case '|':
			return Token.UNION;
		case '*':
			return Token.STAR;
		case '+':
			return Token.PLUS;
		case '(':
			return Token.LPAREN;
		case ')':
			return Token.RPAREN;
		default:
			return Token.charToken(ch);
		}
	}
}
