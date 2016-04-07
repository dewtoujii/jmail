package de.unifreiburg.cs.proglang.jgs.support;


/**
 * Constraints of an JGS signature. Their value is an array of Strings. Each
 * array element encodes a single constraint.
 */
public @interface Constraints {
    String[] value();
}

