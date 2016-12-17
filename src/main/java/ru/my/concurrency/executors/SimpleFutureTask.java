package ru.my.concurrency.executors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * FutureTaskEdu
 * <p></p>
 *
 * @author Platonov Alexey
 * @since 1.0 17.12.16
 */
public class SimpleFutureTask {

    static class SimpleTaskWithBlock {

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            FutureTask<String> result = new FutureTask<>(
                    () -> "Server responded in thread");

            new Thread(result).start();

            try {
                //Поток заблокируется, пока не получит результат
                System.out.println(result.get(10, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                System.out.println("Timeout exceeded");
            }
        }
    }

    static class SimpleTaskPool {

        static Future<String> startTask(Callable<String> task) {
            FutureTask<String> result = new FutureTask<>(task);
            new Thread(result).start();

            return result;
        }

        public static void main(String[] args) throws ExecutionException, InterruptedException {

            ArrayList<Future<String>> taskList = new ArrayList<>();
            taskList.add(startTask(() -> {
                        Thread.sleep(10000); //Отдохнем 10 секунд
                        return "Task 1 is done";
                    })
            );

            taskList.add(startTask(() -> {
                        Thread.sleep(35000); //Отдохнем 35 секунд
                        return "Task 2 is done";
                    })
            );

            taskList.add(
                    startTask(() -> {
                        Thread.sleep(20000); //Отдохнем 35 секунд
                        return "Task 3 is done";
                    })
            );

            boolean isWorking = true;

            while (isWorking) {
                final Iterator<Future<String>> iterator = taskList.iterator();

                while (iterator.hasNext()) {
                    final Future<String> task = iterator.next();

                    if (task.isDone()) {
                        System.out.println(task.get());
                        iterator.remove();
                    }
                }

                if (taskList.isEmpty()) {
                    System.out.println("Work is done");
                    isWorking = false;
                }
            }
        }
    }

    static class TestSceduledThreadPool {

        public static void main(String[] args) {
            final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

            //Продолжим завтра
            scheduledExecutorService.shutdown();
        }
    }

    public static void main(String[] args) {
        //
    }

}
