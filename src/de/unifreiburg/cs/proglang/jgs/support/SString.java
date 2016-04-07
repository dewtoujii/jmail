package de.unifreiburg.cs.proglang.jgs.support;

import java.util.ArrayList;
import java.util.List;

public class SString {

    static public final SString MAX_WAS_CALLED = new SString(1);
    static public final SString PUBLIC_RESULT = new SString(2);
    static public final SString SECRET_RESULT = new SString(3);

    public static SString append(SString s1, SString s2) {
        SString result = new SString();
        result.content.addAll(s1.content);
        result.content.addAll(s2.content);
        return result;
    }

    private List<Integer> content;

    private SString(int i) {
        content = new ArrayList<>();
        content.add(i);
    }

    private SString() {
        content = new ArrayList<>();
    }
}