package com.m12i.regex;

import com.m12i.regex.NFA.Char;
import com.m12i.regex.NFA.Fragment;

/**
 * 正規表現の構文木をあらわすオブジェクト.
 */
final class Node {
	/**
	 * 構文木を構成するノードの種別.
	 */
	static enum Kind {
		CHAR, UNION, STAR, CONCAT, DOT;
	}
	
	private static final char nullChar = '\u0000';
	private static final String lineSep = System.lineSeparator();
	static final Node EMPTY_CHAR_NODE = charNode(nullChar);
	
	/**
	 * 構文木のノードを生成して返す.
	 * @param value 文字
	 * @return ノード
	 */
	static Node charNode(final char value) {
		return new Node(Kind.CHAR, value, null, null);
	}
	/**
	 * 構文木のノードを生成して返す.
	 * @param left 和集合演算の左側ノード
	 * @param right 和集合演算の右側ノード
	 * @return ノード
	 */
	static Node unionNode(final Node left, final Node right) {
		return new Node(Kind.UNION, nullChar, left, right);
	}
	/**
	 * 構文木のノードを生成して返す.
	 * @param factor スター演算対象ノード
	 * @return ノード
	 */
	static Node starNode(final Node factor) {
		return new Node(Kind.STAR, nullChar, factor, null);
	}
	/**
	 * 構文木のノードを生成して返す.
	 * プラス演算は内部的にはスター演算に変換されます。
	 * @param factor プラス演算対象ノード
	 * @return ノード
	 */
	static Node plusNode(final Node factor) {
		return concatNode(factor, starNode(factor));
	}
	/**
	 * 構文木のノードを生成して返す.
	 * @param left 結合される左側のノード
	 * @param right 結合される右側のノード
	 * @return ノード
	 */
	static Node concatNode(final Node left, final Node right) {
		return new Node(Kind.CONCAT, nullChar, left, right);
	}
	/**
	 * 構文木のノードを生成して返す.
	 * @return ノード
	 */
	static Node dotNode() {
		return new Node(Kind.DOT, nullChar, null, null);
	}
	private static void formatHelper(final StringBuilder buff, final int depth, final Node node) {
		if (depth > 0) {
			buff.append(lineSep);
		}
		indent(buff, depth);
		if (node.kind == Node.Kind.DOT) {
			buff.append("Dot");
		} else if (node.kind == Node.Kind.CHAR) {
			buff
			.append("Char(")
			.append(Functions.charLiteral(node.value));
		} else if (node.kind == Node.Kind.CONCAT) {
			buff.append("Concat(");
			formatHelper(buff, depth + 1, node.left);
			buff.append(',');
			formatHelper(buff, depth + 1, node.right);
		} else if (node.kind == Node.Kind.STAR) {
			buff.append("Star(");
			formatHelper(buff, depth + 1, node.left);
		} else if (node.kind == Node.Kind.UNION) {
			buff.append("Union(");
			formatHelper(buff, depth + 1, node.left);
			buff.append(',');
			formatHelper(buff, depth + 1, node.right);
		}
		buff.append(')');
	}
	private static void indent(final StringBuilder buff, final int depth) {
		if (depth == 0) return;
		for (int i = 0; i < depth; i++) {
			buff.append('\t');
		}
	}

	final Kind kind;
	final char value;
	final Node left;
	final Node right;
	
	private Node(final Node.Kind kind, final char value, final Node left, final Node right) {
		this.kind = kind;
		this.value = value;
		this.left = left;
		this.right = right;
	}
	
	/**
	 * レシーバとなるノードとその配下のノードの情報をもとに{@link Fragment}オブジェクトを構成する.
	 * @param factory 各状態にIDを初番するためのファクトリ
	 * @return {@link Fragment}オブジェクト
	 */
	Fragment assemple(IDFactory factory) {
		if (kind == Node.Kind.CHAR) {
			final long s0 = factory.product();
			final long s1 = factory.product();
			final Fragment fragN = new Fragment(s0, s1);
			fragN.connect(Char.just(value), Functions.array(s1));
			return fragN;
		} else if (kind == Node.Kind.DOT) {
			final long s0 = factory.product();
			final long s1 = factory.product();
			final Fragment fragN = new Fragment(s0, s1);
			fragN.connect(Char.DOT, Functions.array(s1));
			return fragN;
		} else if (kind == Node.Kind.UNION) {
			final Fragment frag0 = left.assemple(factory);
			final Fragment frag1 = right.assemple(factory);
			final long sN = factory.product();
			final Fragment fragN = new Fragment(sN, Functions.concat(frag0.accepts, frag1.accepts));
			fragN.include(frag0, frag1);
			fragN.connectWithEpsilon(Functions.array(frag0.from));
			fragN.connectWithEpsilon(Functions.array(frag1.from));
			return fragN;
		} else if (kind == Node.Kind.CONCAT) {
			final Fragment frag0 = left.assemple(factory);
			final Fragment frag1 = right.assemple(factory);
			final Fragment fragN = new Fragment(frag0.from, frag1.accepts);
			fragN.include(frag0, frag1);
			for (final long s : frag0.accepts) {
				fragN.connectWithEpsilon(s, Functions.array(frag1.from));
			}
			return fragN;
		} else if (kind == Node.Kind.STAR) {
			final Fragment frag0 = left.assemple(factory);
			final long sN = factory.product();
			final Fragment fragN = new Fragment(sN, Functions.concat(frag0.accepts, sN));
			fragN.include(frag0);
			for (final long s : frag0.accepts) {
				fragN.connectWithEpsilon(s, Functions.array(frag0.from));
			}
			fragN.connectWithEpsilon(Functions.array(frag0.from));
			return fragN;
		}
		throw new RuntimeException("Invalid node found.");
	}
	/**
	 * ノードの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String format() {
		final StringBuilder buff = new StringBuilder();
		formatHelper(buff, 0, this);
		return buff.toString();
	}
}
