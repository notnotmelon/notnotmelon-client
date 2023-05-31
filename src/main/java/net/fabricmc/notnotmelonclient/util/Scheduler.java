package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.PriorityQueue;

public class Scheduler {
    private static long tick = 0;
    private static final PriorityQueue<ScheduledTask> tasks = new PriorityQueue<>();

    public static void tick(MinecraftClient client) {
        tick += 1;
        ScheduledTask task;
        while ((task = tasks.peek()) != null && task.schedule <= tick) {
            tasks.poll();
            task.run();
            if (task instanceof CyclicTask) tasks.add(task);
        }
    }

    public static void schedule(Runnable task, int delay) {
        tasks.add(new ScheduledTask(task, tick + delay, false));
    }

    public static void scheduleThreaded(Runnable task, int delay) {
        tasks.add(new ScheduledTask(task, tick + delay, true));
    }

    public static void scheduleCyclic(Runnable task, int period) {
        tasks.add(new CyclicTask(task, period, false));
    }

    public static void scheduleCyclicThreaded(Runnable task, int period) {
        tasks.add(new CyclicTask(task, period, true));
    }

    private static class ScheduledTask implements Comparable<ScheduledTask> {
        public final Runnable inner;
        public long schedule;
        public final boolean threaded;

        private ScheduledTask(Runnable inner, long schedule, boolean threaded) {
            this.inner = inner;
            this.schedule = schedule;
            this.threaded = threaded;
        }

        public void run() {
            if (threaded)
                new Thread(inner).start();
            else
                inner.run();
        }

        @Override public int compareTo(@NotNull Scheduler.ScheduledTask that) {
            return (int) (this.schedule - that.schedule);
        }
    }

    private static class CyclicTask extends ScheduledTask {
        public final int period;

        private CyclicTask(Runnable inner, int period, boolean threaded) {
            super(inner, tick, threaded);
            this.period = period;
            run();
        }

        @Override public void run() {
            super.run();
            schedule += period;
        }
    }
}