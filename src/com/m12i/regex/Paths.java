package com.m12i.regex;

import java.util.HashMap;
import java.util.Map;

import com.m12i.regex.NFA.Char;
import com.m12i.regex.NFA.Fragment;

/**
 * 状態遷移パスセット.
 * 初期状態と入力文字をキーにして受理状態を管理します。
 * このオブジェクトは{@link NFA}と{@link Fragment}で使用されます。
 */
final class Paths {
	static final class Key {
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
	static String format(Paths paths) {
		final String lineSep = System.lineSeparator();
		final StringBuilder buff = new StringBuilder();
		for (final Key k : paths.acceptsMap.keySet()) {
			if (buff.length() > 0) {
				buff.append(lineSep);
			}
			if (Char.EPSILON == k.by) {
				buff.append(String.format("(from: %s, by: (epsilon), accepts: %s)", 
						k.from, 
						Functions.arrayList(paths.acceptsMap.get(k))));
			} else {
				buff.append(String.format("(from: %s, by: %s, accepts: %s)", 
						k.from, 
						k.by.format(),
						Functions.arrayList(paths.acceptsMap.get(k))));
			}
		}
		return buff.toString();
	}
	
	private final Map<Key, long[]> acceptsMap = new HashMap<Key, long[]>();
	private final Map<Long, Key> dotKeyMap = new HashMap<Long, Key>();
	private final Map<Long, Key[]> klassKeysMap = new HashMap<Long, Key[]>();
	
	long[] get(final long from, final Char by) {
		return acceptsMap.get(new Key(from, by));
	}
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
	long[] get(final long from) {
		return acceptsMap.get(new Key(from));
	}
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
					klassKeysMap.put(from, Functions.concat(keys, k));
				} else {
					klassKeysMap.put(from, new Key[]{k});
				}
			}
			acceptsMap.put(k, accepts);
		}
	}
	void put(final long from, final long[] accepts) {
		acceptsMap.put(new Key(from), accepts);
	}
	void include(final Paths that) {
		this.acceptsMap.putAll(that.acceptsMap);
		this.klassKeysMap.putAll(that.klassKeysMap);
		this.dotKeyMap.putAll(that.dotKeyMap);
	}
}
