package com.houtai.utils;



public class UserContext {
    private static final ThreadLocal<String> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setToken(String token) {
        USER_THREAD_LOCAL.set(token);
    }

    public static String getToken() {
        return USER_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
