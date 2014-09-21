package com.m12i.regex;

final class Token {
	static enum Kind {
		CHAR, UNION, STAR, PLUS, DOT, LPAREN, RPAREN, EOF;
	}
	private final static char nullChar = '\u0000';
	static final Token UNION = new Token(nullChar, Kind.UNION);
	static final Token STAR = new Token(nullChar, Kind.STAR);
	static final Token PLUS = new Token(nullChar, Kind.PLUS);
	static final Token DOT = new Token(nullChar, Kind.DOT);
	static final Token LPAREN = new Token(nullChar, Kind.LPAREN);
	static final Token RPAREN = new Token(nullChar, Kind.RPAREN);
	static final Token EOF = new Token(nullChar, Kind.EOF);
	
	static Token charToken(final char value) {
		return new Token(value, Kind.CHAR);
	}
	
	final char value;
	final Kind kind;
	
	private Token(final char value, final Kind kind) {
		this.value = value;
		this.kind = kind;
	}
	
	/**
	 * オブジェクトの内容を文字列表現として整形して返す.
	 * @return 整形結果
	 */
	String format() {
		if (this.kind == Kind.CHAR) {
			return this.kind.toString() + '(' + this.value + ')';
		} else {
			return this.kind.toString();
		}
	}
}
