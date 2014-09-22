package com.m12i.regex;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class RegexTest {

	@Test
	public void matchesTest00() {
		final Regex re0 = Regex.compile("");
		assertThat(re0.pattern, is(""));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest01() {
		final Regex re0 = Regex.compile("a");
		assertThat(re0.pattern, is("a"));
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest02() {
		final Regex re0 = Regex.compile("ab");
		assertThat(re0.pattern, is("ab"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest03() {
		final Regex re0 = Regex.compile("ab*");
		assertThat(re0.pattern, is("ab*"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("abb"), is(true));
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest04() {
		final Regex re0 = Regex.compile("ab+");
		assertThat(re0.pattern, is("ab+"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("abb"), is(true));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest05() {
		final Regex re0 = Regex.compile("a[b-z]");
		assertThat(re0.pattern, is("a[b-z]"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("abb"), is(false));
		assertThat(re0.matches("az"), is(true));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest06() {
		final Regex re0 = Regex.compile("a[b-z]*");
		assertThat(re0.pattern, is("a[b-z]*"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("abb"), is(true));
		assertThat(re0.matches("az"), is(true));
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest07() {
		final Regex re0 = Regex.compile("a[b-z]+");
		assertThat(re0.pattern, is("a[b-z]+"));
		assertThat(re0.matches("aa"), is(false));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("abb"), is(true));
		assertThat(re0.matches("az"), is(true));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest08() {
		final Regex re0 = Regex.compile("a[^b-z]");
		assertThat(re0.pattern, is("a[^b-z]"));
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("aA"), is(true));
		assertThat(re0.matches("ab"), is(false));
		assertThat(re0.matches("abb"), is(false));
		assertThat(re0.matches("az"), is(false));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest09() {
		final Regex re0 = Regex.compile("a[^b-z]*");
		assertThat(re0.pattern, is("a[^b-z]*"));
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("aA"), is(true));
		assertThat(re0.matches("ab"), is(false));
		assertThat(re0.matches("abb"), is(false));
		assertThat(re0.matches("az"), is(false));
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest10() {
		final Regex re0 = Regex.compile("a[^b-z]+");
		assertThat(re0.pattern, is("a[^b-z]+"));
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("aA"), is(true));
		assertThat(re0.matches("aZA"), is(true));
		assertThat(re0.matches("ab"), is(false));
		assertThat(re0.matches("abb"), is(false));
		assertThat(re0.matches("az"), is(false));
		assertThat(re0.matches("a"), is(false));
		assertThat(re0.matches("b"), is(false));
		assertThat(re0.matches(""), is(false));
	}

	@Test
	public void matchesTest20() {
		final Regex re0 = Regex.compile("(a|b)");
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("b"), is(true));
		assertThat(re0.matches("c"), is(false));
	}

	@Test
	public void matchesTest21() {
		final Regex re0 = Regex.compile("(a|b|c)");
		assertThat(re0.matches("a"), is(true));
		assertThat(re0.matches("b"), is(true));
		assertThat(re0.matches("c"), is(true));
		assertThat(re0.matches("d"), is(false));
	}

	@Test
	public void matchesTest22() {
		final Regex re0 = Regex.compile("a(a|b)");
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("ac"), is(false));
	}

	@Test
	public void matchesTest23() {
		final Regex re0 = Regex.compile("(a|b)a");
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("ba"), is(true));
		assertThat(re0.matches("ca"), is(false));
	}

	@Test
	public void matchesTest24() {
		final Regex re0 = Regex.compile("(aa|ab)");
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("ac"), is(false));
	}

	@Test
	public void matchesTest25() {
		final Regex re0 = Regex.compile("(aa|ab|ac)");
		assertThat(re0.matches("aa"), is(true));
		assertThat(re0.matches("ab"), is(true));
		assertThat(re0.matches("ac"), is(true));
		assertThat(re0.matches("ad"), is(false));
	}

	@Test
	public void matchesTest26() {
		final Regex re0 = Regex.compile("a(aa|ab)");
		assertThat(re0.matches("aaa"), is(true));
		assertThat(re0.matches("aab"), is(true));
		assertThat(re0.matches("aac"), is(false));
	}

	@Test
	public void matchesTest27() {
		final Regex re0 = Regex.compile("(aa|ab)a");
		assertThat(re0.matches("aaa"), is(true));
		assertThat(re0.matches("aba"), is(true));
		assertThat(re0.matches("aca"), is(false));
	}

	@Test
	public void matchesTest30() {
		final Regex re0 = Regex.compile("(a|b)*c");
		assertThat(re0.matches("ac"), is(true));
		assertThat(re0.matches("bc"), is(true));
		assertThat(re0.matches("c"), is(true));
		assertThat(re0.matches("cc"), is(false));
	}

	@Test
	public void matchesTest31() {
		final Regex re0 = Regex.compile("(a|b)+c");
		assertThat(re0.matches("ac"), is(true));
		assertThat(re0.matches("bc"), is(true));
		assertThat(re0.matches("c"), is(false));
		assertThat(re0.matches("cc"), is(false));
		assertThat(re0.matches("aac"), is(true));
		assertThat(re0.matches("abc"), is(true));
		assertThat(re0.matches("bac"), is(true));
		assertThat(re0.matches("bbc"), is(true));
	}

}
