package ru.my.arrays.sort;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.my.arrays.sort.QuickSort;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * QuickSortTest
 * <p></p>
 *
 * @author Platonov Alexey
 * @since 1.0 10.01.17
 */
@RunWith(Parameterized.class)
public class QuickSortTest {

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[10][0]);
    }

    @Test
    public void should_NormalResult() {
        int[] array = ThreadLocalRandom.current().ints(10000000, 0, 100).distinct().toArray();

        long systemTime = System.nanoTime();
        int[] systemSortedArray = Arrays.stream(array).sorted().parallel().toArray();
        long systemEnd = System.nanoTime() - systemTime;

        long time = System.nanoTime();
        QuickSort.sort(array);
        long timeEnd = System.nanoTime() - time;

        System.out.println(String.format("Система: %f, QuickSort: %f", (float)systemEnd / 1000000, (float)timeEnd / 1000000));
        Assert.assertArrayEquals("Есть расхождения!", systemSortedArray, array);
    }
}
