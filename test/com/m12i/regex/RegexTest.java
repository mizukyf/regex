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
}
