package ru.my.arrays.sort;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * QuickSort
 * <p></p>
 *
 * @author Platonov Alexey
 * @since 1.0 09.01.17
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] array = ThreadLocalRandom.current().ints(100, 0, 100).distinct().toArray();
        Arrays.stream(array).forEach(i -> System.out.print(i + ", "));
        System.out.println();
        int[] systemSortedArray = Arrays.stream(array).sorted().toArray();
        Arrays.stream(systemSortedArray).forEach(i -> System.out.print(i + ", "));

        QuickSort.sort(array);
        System.out.println();

        Arrays.stream(array).forEach(i -> System.out.print(i + ", "));

        for (int i = 0; i < array.length; i++) {
            if (array[i] != systemSortedArray[i]) {
                System.out.println("Есть расхождения в элементе: " + systemSortedArray[i]);
            }
        }



        // 1 4 3 5 12
        //       1

        //1, 4, 5, 6, 2, 12
        //      1     1
        //1, 4, 2, 6, 5, 12
        //         1  1
        //1, 4, 2, 5, 6, 12
        //         1  1
        //1, 4, 2, 5, 6, 12, 36, 92
        //0  1  2  3  4  5   6   7

    }

    public static void sort(int [] array) {
        int leftIndex = 0;
        int rightIndex = array.length - 1;

        recursive(leftIndex, rightIndex, array);
    }

    public static void recursive(int startLeftIndex, int endRightIndex, int[]array) {

        int leftIndex = startLeftIndex;
        int rightIndex = endRightIndex;

        int bearingIndex = leftIndex + (rightIndex - leftIndex)/2;
        int pivot = array[bearingIndex];

        while (leftIndex <= rightIndex) {

            if (array[leftIndex] < pivot) {
                leftIndex++;
            } else if (array[rightIndex] > pivot) {
                rightIndex--;
            } else {
                int tmp = array[leftIndex];
                array[leftIndex] = array[rightIndex];
                array[rightIndex] = tmp;
                rightIndex--;
                leftIndex++;
            }

        }

        if (startLeftIndex < rightIndex) {
            recursive(startLeftIndex, rightIndex, array);
        }

        if (leftIndex < endRightIndex) {
            recursive(leftIndex, endRightIndex, array);
        }

    }

}
