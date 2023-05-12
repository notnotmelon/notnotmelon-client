/*
 * This code is reused from
 * Source: https://github.com/SkyblockerMod/Skyblocker/blob/master/src/main/java/me/xmrvizzy/skyblocker/utils/Scheduler.java
 * 
 * Modifications: 
 * 1. ontick system is now event-based instead of mixin based
 * 2. switched logger to reuse logger from entrypoint
 * 
 * GNU Lesser General Public License v3.0
 */

package net.fabricmc.notnotmelonclient.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.notnotmelonclient.Main;

import java.util.PriorityQueue;

public class Scheduler {
    private static Scheduler instance;
    private int currentTick;
    private final PriorityQueue<ScheduledTask> tasks;

    public Scheduler() {
        currentTick = 0;
        tasks = new PriorityQueue<>();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentTick += 1;
            ScheduledTask task;
            while ((task = tasks.peek()) != null && task.schedule <= currentTick) {
                tasks.poll();
                task.run();
            }
        });
        instance = this;
    }

    public static Scheduler getInstance() {
        return instance;
    }

    public void schedule(Runnable task, int delay) {
        if (delay < 0)
            Main.LOGGER.warn("Scheduled a task with negative delay");
        ScheduledTask tmp = new ScheduledTask(currentTick + delay, task);
        tasks.add(tmp);
    }

    public void scheduleCyclic(Runnable task, int period) {
        if (period <= 0)
            Main.LOGGER.error("Attempted to schedule a cyclic task with period lower than 1");
        else
            new CyclicTask(task, period).run();
    }

    private class CyclicTask implements Runnable {
        private final Runnable inner;
        private final int period;

        public CyclicTask(Runnable task, int period) {
            this.inner = task;
            this.period = period;
        }

        @Override
        public void run() {
            schedule(this, period);
            inner.run();
        }
    }

    private record ScheduledTask(int schedule, Runnable inner) implements Comparable<ScheduledTask>, Runnable {
        @Override
        public int compareTo(ScheduledTask o) {
            return schedule - o.schedule;
        }

        @Override
        public void run() {
            inner.run();
        }
    }
}