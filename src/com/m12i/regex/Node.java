package com.m12i.regex;

import com.m12i.regex.NFA.Fragment;

/**
 * 正規表現の構文木をあらわすオブジェクト.
 */
final class Node {
	/**
	 * 構文木を構成するノードの種別.
	 */
	static enum Kind {
		CHAR, UNION, STAR, CONCAT;
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
	private static void formatHelper(final StringBuilder buff, final int depth, final Node node) {
		if (depth > 0) {
			buff.append(lineSep);
		}
		indent(buff, depth);
		if (node.isChar) {
			buff
			.append("Char(")
			.append(Functions.charLiteral(node.value));
		} else if (node.isConcat) {
			buff.append("Concat(");
			formatHelper(buff, depth + 1, node.left);
			buff.append(',');
			formatHelper(buff, depth + 1, node.right);
		} else if (node.isStar) {
			buff.append("Star(");
			formatHelper(buff, depth + 1, node.left);
		} else if (node.isUnion) {
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
	final boolean isChar;
	final boolean isUnion;
	final boolean isStar;
	final boolean isConcat;
	
	private Node(final Kind kind, final char value, final Node left, final Node right) {
		this.kind = kind;
		this.value = value;
		this.left = left;
		this.right = right;
		this.isChar = kind == Kind.CHAR;
		this.isUnion = kind == Kind.UNION;
		this.isStar = kind == Kind.STAR;
		this.isConcat = kind == Kind.CONCAT;
	}
	
	/**
	 * レシーバとなるノードとその配下のノードの情報をもとに{@link Fragment}オブジェクトを構成する.
	 * @param factory 各状態にIDを初番するためのファクトリ
	 * @return {@link Fragment}オブジェクト
	 */
	Fragment assemple(IDFactory factory) {
		if (isChar) {
			final long s0 = factory.product();
			final long s1 = factory.product();
			final Fragment fragN = new Fragment(s0, s1);
			fragN.connect(value, Functions.array(s1));
			return fragN;
		} else if (isUnion) {
			final Fragment frag0 = left.assemple(factory);
			final Fragment frag1 = right.assemple(factory);
			final long sN = factory.product();
			final Fragment fragN = new Fragment(sN, Functions.concat(frag0.accepts, frag1.accepts));
			fragN.include(frag0, frag1);
			fragN.connect(Functions.array(frag0.from));
			fragN.connect(Functions.array(frag1.from));
			return fragN;
		} else if (isConcat) {
			final Fragment frag0 = left.assemple(factory);
			final Fragment frag1 = right.assemple(factory);
			final Fragment fragN = new Fragment(frag0.from, frag1.accepts);
			fragN.include(frag0, frag1);
			for (final long s : frag0.accepts) {
				fragN.connect(s, Functions.array(frag1.from));
			}
			return fragN;
		} else if (isStar) {
			final Fragment frag0 = left.assemple(factory);
			final long sN = factory.product();
			final Fragment fragN = new Fragment(sN, Functions.concat(frag0.accepts, sN));
			fragN.include(frag0);
			for (final long s : frag0.accepts) {
				fragN.connect(s, Functions.array(frag0.from));
			}
			fragN.connect(Functions.array(frag0.from));
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
