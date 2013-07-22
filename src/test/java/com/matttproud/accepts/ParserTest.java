package com.matttproud.accepts;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * <p>
 * Blackbox tests for {@link Parser} and {@link Accept}.
 * </p>
 */
public class ParserTest {
  @Test
  public void TestChrome() {
    final String header =
        "*/*;q=0.5,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png";
    final List<Accept> out = Parser.parse(header);

    Assert.assertEquals(6, out.size());
    Assert.assertEquals(new Accept("application", "xml", 1.0f, ImmutableMap.<String, String> of()),
        out.get(0));
    Assert.assertEquals(
        new Accept("application", "xhtml+xml", 1.0f, ImmutableMap.<String, String> of()),
        out.get(1));
    Assert.assertEquals(new Accept("image", "png", 1.0f, ImmutableMap.<String, String> of()),
        out.get(2));
    Assert.assertEquals(new Accept("text", "html", 0.9f, ImmutableMap.<String, String> of()),
        out.get(3));
    Assert.assertEquals(new Accept("text", "plain", 0.8f, ImmutableMap.<String, String> of()),
        out.get(4));
    Assert.assertEquals(new Accept("*", "*", 0.5f, ImmutableMap.<String, String> of()), out.get(5));
  }

  @Test
  public void TestChromeInverted() {
    final String header =
        "*/*;q=0.5,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png";
    final List<Accept> out = Parser.parse(header);

    Assert.assertEquals(6, out.size());
    Assert.assertEquals(new Accept("application", "xml", 1.0f, ImmutableMap.<String, String> of()),
        out.get(0));
    Assert.assertEquals(
        new Accept("application", "xhtml+xml", 1.0f, ImmutableMap.<String, String> of()),
        out.get(1));
    Assert.assertEquals(new Accept("image", "png", 1.0f, ImmutableMap.<String, String> of()),
        out.get(2));
    Assert.assertEquals(new Accept("text", "html", 0.9f, ImmutableMap.<String, String> of()),
        out.get(3));
    Assert.assertEquals(new Accept("text", "plain", 0.8f, ImmutableMap.<String, String> of()),
        out.get(4));
    Assert.assertEquals(new Accept("*", "*", 0.5f, ImmutableMap.<String, String> of()), out.get(5));
  }
}
