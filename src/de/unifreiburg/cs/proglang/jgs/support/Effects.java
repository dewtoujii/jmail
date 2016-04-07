package de.unifreiburg.cs.proglang.jgs.support;

/**
 * Effects of a signature. Their value is an array of strings where each element
 * corresponds to a security type.
 */
public @interface Effects {
    String[] value();
}