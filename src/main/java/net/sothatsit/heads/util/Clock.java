package net.sothatsit.heads.util;

import java.text.DecimalFormat;

public class Clock {

    private static final DecimalFormat millisecondsFormat = new DecimalFormat("#.##");

    private final long start;
    private long end;

    public Clock() {
        this.start = System.nanoTime();
        this.end = -1;
    }

    public boolean hasEnded() {
        return end >= 0;
    }

    public void stop() {
        if(hasEnded())
            throw new IllegalStateException("Timer has already been stopped.");

        this.end = System.nanoTime();
    }

    public double getDuration() {
        return (hasEnded() ? end - start : System.nanoTime() - start) / 1e6;
    }

    @Override
    public String toString() {
        return "(" + millisecondsFormat.format(getDuration()) + " ms)";
    }

    public static Clock start() {
        return new Clock();
    }

}
