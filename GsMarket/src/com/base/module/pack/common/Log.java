package com.base.module.pack.common;

/**
 * User: czheng
 * Date: 3/15/13
 * Time: 10:12 AM
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved. 
 */
public final class Log {

    private static final String PROJECTNAME = "Market";

    private static long previousNanoTime  = 0;
    private static enum LogLevelEnum {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }


    private static StackTraceElement getCurrentExcuteTraceInfo(final int eleNum) {
        return (Thread.currentThread().getStackTrace())[eleNum];
    }

    private static StringBuilder getCurrentClassInfo(final int eleNum) {
        StackTraceElement currentExcuteTraceInfo = getCurrentExcuteTraceInfo(eleNum);
        long nanoTime = System.nanoTime() - previousNanoTime ;
        previousNanoTime = System.nanoTime();
        return new StringBuilder()
                .append("timeMill: "+nanoTime)
                .append(" class: ")
                .append(currentExcuteTraceInfo.getClassName())
                .append(" method: ")
                .append(currentExcuteTraceInfo.getMethodName())
                .append(" message: ");
    }

    private static void record (LogLevelEnum level, String... msgs) {
        if (msgs == null) {
            return ;
        }

        StringBuilder sb =  getCurrentClassInfo(6);
        for (String msg : msgs) {
            sb.append(" "+msg);
        }

        switch (level) {
            case INFO:
                android.util.Log.i(PROJECTNAME, sb.toString());
                return ;
            case ERROR:
                android.util.Log.e(PROJECTNAME, sb.toString());
                return ;
            case WARN:
                android.util.Log.w(PROJECTNAME, sb.toString());
                return ;
            case DEBUG:
                android.util.Log.d(PROJECTNAME, sb.toString());
                return ;
            case VERBOSE:
                android.util.Log.v(PROJECTNAME, sb.toString());
                return ;
        }
    }

    public static void i(String... msgs) {
        record(LogLevelEnum.INFO, msgs);
    }

    public static void e(String... msgs) {
        record(LogLevelEnum.ERROR, msgs);
    }

    public static void w(String... msgs) {
        record(LogLevelEnum.WARN, msgs);
    }

    public static void d(String... msgs) {
        record(LogLevelEnum.DEBUG, msgs);
    }

    public static void v(String... msgs) {
        record(LogLevelEnum.VERBOSE, msgs);
    }
}