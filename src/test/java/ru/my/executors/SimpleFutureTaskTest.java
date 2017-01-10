package ru.my.executors;

import com.sun.deploy.security.ruleset.ExceptionRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

/**
 * FutureTaskEdu
 * <p>
 * Поиграться с типичными реалзациями ExcecutorService
 * </p>
 *
 * @author Platonov Alexey
 * @since 1.0 17.12.16
 */
public class SimpleFutureTaskTest {

    /**
     * Посмотрим как создается и выполняется обычная асихнхронная задача
     */
    @Test
    public void should_printMsg_when_TimoutNotExceeded()
            throws ExecutionException, InterruptedException {
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


    /**
     * Реализация простейшего пула из 3 - х задач, основанного на простом списке
     */
    static Future<String> startTask(Callable<String> task) {
        FutureTask<String> result = new FutureTask<>(task);
        new Thread(result).start();

        return result;
    }

    @Test
    public void should_executeTasks_when_poolStarted() throws ExecutionException, InterruptedException {

        ArrayList<Future<String>> taskList = new ArrayList<>();
        taskList.add(startTask(() -> {
                    sleep(10000); //Отдохнем 10 секунд
                    return "Task 1 is done";
                })
        );

        taskList.add(startTask(() -> {
                    sleep(35000); //Отдохнем 35 секунд
                    return "Task 2 is done";
                })
        );

        taskList.add(
                startTask(() -> {
                    sleep(20000); //Отдохнем 35 секунд
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

    /**
     * Запуск асинхронных задач по времени.
     */
    private enum Delay {
        EVERY_10_SECONDS(10, TimeUnit.SECONDS);

        private final long period;
        private final TimeUnit timeUnit;

        Delay(long period, TimeUnit timeUnit) {
            this.period = period;
            this.timeUnit = timeUnit;
        }
    }

    private static ScheduledFuture<?> scheduleAtFixedRateWrapped(
            ScheduledExecutorService scheduledExecutorService, Runnable command, long initialDelay, Delay delay) {
        return scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, delay.period, delay.timeUnit);
    }

    @Test
    public void should_doAsyncTasks_when_mainIsSleep() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        long startWithoutDelay = 0;

        scheduleAtFixedRateWrapped(scheduledExecutorService,
                () -> System.out.println("next execution"), startWithoutDelay, Delay.EVERY_10_SECONDS);

        try {
            sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduledExecutorService.shutdown();
    }

    /**
     * Проверим как работаю policy в ThreadPoolExecutor'e
     */

    /**
     * Поведение по умолчанию возвращать Exception при попытке добавить задачу в обработку
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_throwException_when_tryToAddTaskWhenPoolShutDown()
            throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

        executorService.submit(() -> {
            System.out.println("task started");
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();

        exception.expect(RejectedExecutionException.class);
        executorService.submit(() -> System.out.println("new task"));

        while (!executorService.isShutdown()) {
            System.out.println("wait shut down");
            sleep(1000);
        }

    }

    /**
     * Почему взято 3 задачи и размер очереди 1, потому как одна задача сразу берется на исполнение и переполнение
     * очереди можно вызвать только если есть еще 2 задачи.
     * Таким образом одна из задач будет запущена в main потоке.
     */
    @Test
    public void should_startTaskInCurrentThread_when_tryToAddTaskWhenPoolQuieIsFull()
            throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                500L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());

        executorService.submit(pooledTask);

        Runnable lastTask = () -> System.out.println("Quid task started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask);
        Runnable lastTask1 = () -> System.out.println("Main task started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask1);

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    private final Runnable pooledTask = () -> {
        String name = Thread.currentThread().getName();

        System.out.println("Pooled task started in thread " + name);
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread with task stopped");
    };

    /**
     * Отменяет старую задачу в очереди и меняет ее на новую
     */
    @Test
    public void should_discardOldTask_when_tryToAddTaskWhenPoolQueIsFull()
            throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                500L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());

        executorService.submit(pooledTask);

        Runnable lastTask = () -> System.out.println(
                "Quid task discarded started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask);
        Runnable lastTask1 = () -> System.out.println("new Quid task started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask1);

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * Если очередь полна, то не берет в обработку
     */
    @Test
    public void should_discardNewTask_when_tryToAddTaskWhenPoolQueIsFull()
            throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                500L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardPolicy());

        executorService.submit(pooledTask);

        Runnable lastTask = () -> System.out.println(
                "Quid task started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask);
        Runnable lastTask1 = () -> System.out.println("New task will discard started in thread " + Thread.currentThread().getName());
        executorService.submit(lastTask1);

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

}
