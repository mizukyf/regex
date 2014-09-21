package com.m12i.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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
				s = ((NFA) o).format();
			} else if (o instanceof Node) {
				l = String.format(dumpTmpl, "Node", o);
				s = ((Node) o).format();
			} else if (o instanceof Token) {
				l = String.format(dumpTmpl, "Token", o);
				s = ((Token) o).format();
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
		final StringBuilder buff = new StringBuilder();
		buff.append('\'');
		if (c == '"') {
			buff.append(c);
		} else {
			final int index = searchEscaped(c);
			if (index < 0) {
				buff.append(c);
			} else {
				buff.append(dict[index].escaped);
			}
		}
		buff.append('\'');
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
	static long[] concat(final long[] a, final long... b) {
		final int aLen = a.length;
		final int bLen = b.length;
		final long[] r = Arrays.copyOf(a, aLen + bLen);
		for (int i = 0; i < bLen; i ++) {
			r[i + aLen] = b[i];
		}
		return r;
	}
	static Set<Long> set(final long...array) {
		final Set<Long> r = new HashSet<Long>();
		if (array != null) {
			for (final long n : array) {
				r.add(n);
			}
		}
		return r;
	}
	static Queue<Long> queue(final long...array) {
		final Queue<Long> r = new LinkedList<Long>();
		for (final long n: array) {
			r.add(n);
		}
		return r;
	}
	static long[] array(final Set<Long> set) {
		final long[] array = new long[set.size()];
		int i = 0;
		for (final long n : set) {
			array[i++] = n;
		}
		return array;
	}
	static long[] array(final long n) {
		return new long[]{n};
	}
	static ArrayList<Long> arrayList(final long...array) {
		final ArrayList<Long> r = new ArrayList<Long>();
		for (final long e : array) {
			r.add(e);
		}
		return r;
	}
}
