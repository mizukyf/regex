package com.m12i.regex;

/**
 * IDファクトリ.
 * {@link NFA}や{@link DFA}で使用される状態のIDを発番するために利用されます。
 */
final class IDFactory {
	/**
	 * 新しいファクトリを生成する.
	 * @return ファクトリ
	 */
	static IDFactory create() {
		return new IDFactory();
	}
	
	private long i = 0;
	private IDFactory(){}
	
	/**
	 * 新しいIDを発番する.
	 * @return ID
	 */
	long product() {
		return ++i;
	}
}
