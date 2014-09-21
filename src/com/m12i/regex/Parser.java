package com.m12i.regex;

import com.m12i.regex.Token.Kind;

/**
 * {@link Lexer}により字句解析された結果をもとに構文木を組み立てるオブジェクト.
 */
final class Parser {
	private Token curr = null;
	private final Lexer lexer;
	
	/**
	 * {@link Lexer}オブジェクトをパラメータにとりパーサを初期化する.
	 * @param lexer {@link Lexer}オブジェクト
	 */
	Parser(final Lexer lexer) {
		this.lexer = lexer;
		next();
	}
	
	private void checkAndNext(final Kind kind) {
		if (curr.kind != kind) {
			throw new RuntimeException("Invalid token found.");
		}
		next();
	}
	
	private void next() {
		curr = lexer.scan();
	}
	
	private Node factor() {
		if (curr.kind == Kind.LPAREN) {
			next();
			final Node node = subexpr();
			checkAndNext(Kind.RPAREN);
			return node;
		} else {
			final Node node = Node.charNode(curr.value);
			next();
			return node;
		}
	}
	private Node star() {
		final Node node = factor();
		if (curr.kind == Kind.STAR) {
			next();
			return Node.starNode(node);
		} else if (curr.kind == Kind.PLUS) {
			next();
			return Node.plusNode(node);
		}
		return node;
	}
	private Node seq() {
		if (curr.kind == Kind.LPAREN || curr.kind == Kind.CHAR) {
			return subseq();
		} else {
			return Node.EMPTY_CHAR_NODE;
		}
	}
	private Node subseq() {
		final Node node0 = star();
		if (curr.kind == Kind.LPAREN || curr.kind == Kind.CHAR) {
			final Node node1 = subseq();
			return Node.concatNode(node0, node1);
		} else {
			return node0;
		}
	}
	private Node subexpr() {
		final Node node0 = seq();
		if (curr.kind == Kind.UNION) {
			next();
			final Node node1 = subexpr();
			return Node.unionNode(node0, node1);
		}
		return node0;
	}
	/**
	 * 構文木を組み立てて返す.
	 * @return 構文木
	 */
	Node parse() {
		final Node node = subexpr();
		checkAndNext(Kind.EOF);
		return node;
	}
}
