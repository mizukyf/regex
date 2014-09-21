package com.m12i.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.m12i.regex.NFA.Fragment;

/**
 * 状態遷移パスセット.
 * 初期状態と入力文字をキーにして受理状態を管理します。
 * このオブジェクトは{@link NFA}と{@link Fragment}で使用されます。
 */
final class Paths {
	/**
	 * 状態遷移パスのマップのためのキー.
	 */
	private final class Key {
		final long from;
		final Char by;
		private Key(final long from, final Char by) {
			this.from = from;
			this.by = by;
		}
		private Key(final long from) {
			this.from = from;
			this.by = Char.EPSILON;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((by == null) ? 0 : by.hashCode());
			result = prime * result + (int) (from ^ (from >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (by == null) {
				if (other.by != null)
					return false;
			} else if (!by.equals(other.by))
				return false;
			if (from != other.from)
				return false;
			return true;
		}
	}
	
	private final Map<Key, long[]> acceptsMap = new HashMap<Key, long[]>();
	private final Map<Long, Key> dotKeyMap = new HashMap<Long, Key>();
	private final Map<Long, Key[]> klassKeysMap = new HashMap<Long, Key[]>();
	
	/**
	 * 初期状態と入力文字をキーにして受理状態セットを取得する.
	 * 入力文字は{@link Char}オブジェクトのかたちで指定します。
	 * このオブジェクトは文字そのもののほか、文字クラスやドット、空文字（イプシロン）もあらわします。
	 * @param from 初期状態
	 * @param by 入力文字
	 * @return 受理状態セット
	 */
	long[] get(final long from, final Char by) {
		return acceptsMap.get(new Key(from, by));
	}
	/**
	 * 初期状態と入力文字をキーにして受理状態セットを取得する.
	 * 入力文字は内部的に保持されている文字クラスやドットの情報とも照合されます。
	 * @param from 初期状態
	 * @param by 入力文字
	 * @return 受理状態
	 */
	long[] get(final long from, final char by) {
		long[] result = acceptsMap.get(new Key(from, Char.just(by)));
		if (result == null) {
			result = new long[0];
		}
		final Key dotKey = dotKeyMap.get(from);
		if (dotKey != null) {
			result = Functions.concat(result, acceptsMap.get(dotKey));
		}
		final Key[] klassKeys = klassKeysMap.get(from);
		if (klassKeys != null) {
			for (final Key k : klassKeys) {
				if (k.by.matches(by)) {
					result = Functions.concat(result, acceptsMap.get(k));
				}
			}
		}
		return result;
	}
	/**
	 * 初期状態と空文字（イプシロン）により状態遷移パスをたどり受理状態セットを返す.
	 * @param from 初期状態
	 * @return 受理状態セット
	 */
	long[] get(final long from) {
		return acceptsMap.get(new Key(from));
	}
	/**
	 * 初期状態と入力文字をキーにして受理状態セットを登録する.
	 * @param from 初期状態
	 * @param by 入力文字
	 * @param accepts 受理状態セット
	 */
	void put(final long from, final Char by, final long[] accepts) {
		if (by.isEpsilon) {
			put(from, accepts);
		} else {
			final Key k = new Key(from, by);
			if (by.isDot) {
				dotKeyMap.put(from, k);
			}
			if (by.isCharKlass) {
				final Key[] keys = klassKeysMap.get(from);
				if (keys != null) {
					klassKeysMap.put(from, concat(keys, k));
				} else {
					klassKeysMap.put(from, new Key[]{k});
				}
			}
			acceptsMap.put(k, accepts);
		}
	}
	/**
	 * 初期状態と空文字（イプシロン）をキーにして受理状態セットを登録する.
	 * @param from 初期状態
	 * @param accepts 受理状態セット
	 */
	void put(final long from, final long[] accepts) {
		acceptsMap.put(new Key(from), accepts);
	}
	/**
	 * 状態遷移パス情報をコピーして取り込む.
	 * @param source 取り込み元
	 */
	void include(final Paths source) {
		this.acceptsMap.putAll(source.acceptsMap);
		this.klassKeysMap.putAll(source.klassKeysMap);
		this.dotKeyMap.putAll(source.dotKeyMap);
	}
	private Key[] concat(final Key[] a, final Key... b) {
		final int aLen = a.length;
		final int bLen = b.length;
		final Key[] r = Arrays.copyOf(a, aLen + bLen);
		for (int i = 0; i < bLen; i ++) {
			r[i + aLen] = b[i];
		}
		return r;
	}
	/**
	 * オブジェクトの内容を文字列表現として整形する.
	 * @return 整形結果
	 */
	String inspect() {
		final String lineSep = System.lineSeparator();
		final StringBuilder buff = new StringBuilder();
		for (final Key k : acceptsMap.keySet()) {
			if (buff.length() > 0) {
				buff.append(lineSep);
			}
			if (Char.EPSILON == k.by) {
				buff.append(String.format("(from: %s, by: (epsilon), accepts: %s)", 
						k.from, 
						Functions.arrayList(acceptsMap.get(k))));
			} else {
				buff.append(String.format("(from: %s, by: %s, accepts: %s)", 
						k.from, 
						k.by.inspect(),
						Functions.arrayList(acceptsMap.get(k))));
			}
		}
		return buff.toString();
	}
}
