package concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author arloz
 * @version $Id: AtomicTest.java, v 0.1 2018/09/30 下午2:09 arloz Exp $$
 */
public class AtomicTest {
    private abstract static class Increment {
        public abstract int increase();

        public abstract int getCount();
    }

    private static class Increment1 extends Increment {
        private Integer count = 0;

        public int increase() {
            count++;
            return count;
        }

        @Override
        public int getCount() {
            return count;
        }

    }

    private static class Increment2 extends Increment {
        private Integer count = 0;

        public synchronized int increase() {
            count++;
            return count;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    private static class Increment3 extends Increment {
        private AtomicInteger count = new AtomicInteger(0);

        public int increase() {
            return count.getAndIncrement();
        }

        @Override
        public int getCount() {
            return count.get();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Increment1 increment1 = new Increment1();
            testIncrement(increment1);

            Increment2 increment2 = new Increment2();
            testIncrement(increment2);

            Increment3 increment3 = new Increment3();
            testIncrement(increment3);
            System.out.println("-------------------------");
        }
    }

    private static void testIncrement(final Increment increment) {
        // 启20个线程，进行递增操作
        int threadSize = 100;
        int loopSize = 5000;

        List<Thread> threads = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        for (int i = 0; i < threadSize; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < loopSize; i++) {
                        increment.increase();
                    }
                    countDownLatch.countDown();
                }
            }));
        }

        long begin = System.currentTimeMillis();
        threads.forEach((t) -> t.start());

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long cost = System.currentTimeMillis() - begin;
        System.out.println("testIncrement: count=" + increment.getCount() + " cost=" + cost + " ms");
    }
}
