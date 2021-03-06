package com.m12i.regex;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * パターンマッチおよびその準備手続きのなかで使用するヘルパー関数.
 */
final class Functions {
	private Functions() {}
	private static final class Pair {
		final char ch;
		final String escaped;
		Pair(final char ch, final String escaped) {
			this.ch = ch;
			this.escaped = escaped;
		}
	}
	private static final boolean debug = false;
	private static final String dumpTmpl = "[[%s]](%s)";
	private static final Pair[] dict = {
			new Pair('\b', "\\b"),
			new Pair('\n', "\\n"),
			new Pair('\t', "\\t"),
			new Pair('\f', "\\f"),
			new Pair('\r', "\\r"),
			new Pair('"', "\\\""),
			new Pair('\\', "\\\\")
	};
	static void dump(final Object o) {
		if (debug) {
			final String l;
			final String s;
			if (o instanceof NFA) {
				l = String.format(dumpTmpl, "NFA", o);
				s = ((NFA) o).inspect();
			} else if (o instanceof Node) {
				l = String.format(dumpTmpl, "Node", o);
				s = ((Node) o).inspect();
			} else if (o instanceof Token) {
				l = String.format(dumpTmpl, "Token", o);
				s = ((Token) o).inspect();
			} else {
				l = String.format(dumpTmpl, "Object", o);
				s = o.toString();
			}
			final String hyphen = hyphen(l.length());
			System.out.println(l);
			System.out.println(hyphen);
			System.out.println(s);
			System.out.println(hyphen);
		}
	}
	private static String hyphen(final int len) {
		final StringBuilder buff = new StringBuilder();
		for (int i = 0; i < len; i ++) {
			buff.append('-');
		}
		return buff.toString();
	}
	static String stringLiteral(final String s) {
		final StringBuilder buff = new StringBuilder();
		buff.append('"');
		outer:
		for (final char c : s.toCharArray()) {
			for (final Pair p : dict) {
				if (c == p.ch) {
					buff.append(p.escaped);
					continue outer;
				}
			}
			buff.append(c);
		}
		buff.append('"');
		return buff.toString();
	}
	static String charLiteral(final char c) {
		return "'" + escapedChar(c) + "'";
	}
	static String escapedChar(final char c) {
		final StringBuilder buff = new StringBuilder();
		if (c == '"') {
			buff.append(c);
		} else if (c == '\'') {
			buff.append('\\').append(c);
		} else {
			final int index = searchEscaped(c);
			if (index < 0) {
				buff.append(c);
			} else {
				buff.append(dict[index].escaped);
			}
		}
		return buff.toString();
	}
	private static int searchEscaped(final char ch) {
		for (int i = 0; i < dict.length; i ++) {
			if (ch == dict[i].ch) {
				return i;
			}
		}
		return -1;
	}
	static Long[] concat(final Long[] a, final Long... b) {
		final int aLen = a.length;
		final int bLen = b.length;
		final Long[] r = Arrays.copyOf(a, aLen + bLen);
		for (int i = 0; i < bLen; i ++) {
			r[i + aLen] = b[i];
		}
		return r;
	}
	static Long[] array(final Long n) {
		return new Long[]{n};
	}
	static ArrayList<Long> arrayList(final Long...array) {
		final ArrayList<Long> r = new ArrayList<Long>();
		for (final Long e : array) {
			r.add(e);
		}
		return r;
	}
}
