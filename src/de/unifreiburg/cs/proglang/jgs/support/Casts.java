package de.unifreiburg.cs.proglang.jgs.support;

public class Casts {

    public static <T> T castHighToDyn(T x) {
        return x;
    }

    public static int castHighToDyn(int x) {
        return x;
    }

    public static <T> T castLowToDyn(T x) {
        return x;
    }

    public static int castLowToDyn(int x) {
        return x;
    }

    public static <T> T castDynToLow(T x) {
        return x;
    }

    public static int castDynToLow(int x) {
        return x;
    }

    public static <T> T castDynToHigh(T x) {
        return x;
    }

    public static int castDynToHigh(int x) {
        return x;
    }

    public static <T> T castDynToPub(T x) {
        return x;
    }

    public static int castDynToPub(int x) {
        return x;
    }

    public static <T> T castLowToPub(T x) {
        return x;
    }

    public static int castLowToPub(int x) {
        return x;
    }

    public static void castCxHighToDyn() {}
    public static void castCxLowToDyn() {}
    public static void castCxLowToPub() {}
    public static void castCxDynToLow() {}
    public static void castCxDynToHigh() {}
    public static void castCxDynToPub() {}
    public static void castCxEnd(){}

}