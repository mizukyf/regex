package com.m12i.regex;

final class Token {
	static enum Kind {
		CHAR, UNION, STAR, PLUS, DOT, LPAREN, RPAREN, EOF, LBRACKET, RBRACKET, CARET, HYPHEN;
	}
	private final static char nullChar = '\u0000';
	static final Token UNION = new Token(nullChar, Kind.UNION);
	static final Token STAR = new Token(nullChar, Kind.STAR);
	static final Token PLUS = new Token(nullChar, Kind.PLUS);
	static final Token DOT = new Token(nullChar, Kind.DOT);
	static final Token LPAREN = new Token(nullChar, Kind.LPAREN);
	static final Token RPAREN = new Token(nullChar, Kind.RPAREN);
	static final Token EOF = new Token(nullChar, Kind.EOF);
	static final Token LBRACKET = new Token(nullChar, Kind.LBRACKET);
	static final Token RBRACKET = new Token(nullChar, Kind.RBRACKET);
	static final Token CARET = new Token(nullChar, Kind.CARET);
	static final Token HYPHEN = new Token(nullChar, Kind.HYPHEN);
	
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
