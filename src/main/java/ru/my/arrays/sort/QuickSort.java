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

    public static void sort(int[]array) {

        int leftIndex = 0;
        int rightIndex = array.length - 1;

        recursiveSort(leftIndex, rightIndex, array);
    }

    private static void recursiveSort(int startLeftIndex, int endRightIndex, int[]array) {

        //Выйти, если в отрезке один элемент
        if (endRightIndex - startLeftIndex <= 1) {
            return;
        }

        int leftIndex = startLeftIndex;
        int rightIndex = endRightIndex;
        int bearingIndex = (rightIndex - leftIndex) / 2;
        int bearingElement = array[bearingIndex];
        boolean leftSwap = false;
        boolean rightSwap = false;


        while (leftIndex != rightIndex) {

            if ((array[leftIndex] <= bearingElement) && (leftIndex != bearingIndex)) {
                leftIndex ++;
            } else {
                //Остановится и ожидать элемента для обмена
                leftSwap = true;
            }

            if ((array[rightIndex] > bearingElement) && (rightIndex != bearingIndex)) {
                rightIndex --;
            } else {
                //Остановится и ожидать элемента для обмена
                rightSwap = true;
            }

            if (leftSwap && rightSwap) {
                int tmp = array[leftIndex];
                array[leftIndex] = array[rightIndex];
                array[rightIndex] = tmp;

                leftSwap = false;
                rightSwap = false;

                if (leftIndex != bearingIndex) {
                    leftIndex++;
                }

                if (rightIndex != bearingIndex) {
                    rightIndex--;
                }

            }
        }

        //Рекурсивно отсортировать каждый из подмассивов
        recursiveSort(startLeftIndex, leftIndex, array);
        recursiveSort(rightIndex, endRightIndex, array);
    }

    public static void main(String[] args) {
        int[] array = ThreadLocalRandom.current().ints(100, 0, 100).distinct().toArray();
        Arrays.stream(array).forEach(i -> System.out.print(i + ", "));
        System.out.println();
        int[] systemSortedArray = Arrays.stream(array).sorted().toArray();
        Arrays.stream(systemSortedArray).forEach(i -> System.out.print(i + ", "));

        QuickSort.sort2(array);
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

    public static void sort2(int [] array) {
        int leftIndex = 0;
        int rightIndex = array.length - 1;

        recursive2(leftIndex, rightIndex, array);
    }

    public static void recursive2(int startLeftIndex, int endRightIndex, int[]array) {
        //Выйти, если в отрезке один элемент
        if (endRightIndex - startLeftIndex < 1) {
            return;
        }

        int leftIndex = startLeftIndex;
        int rightIndex = endRightIndex;

        int bearingIndex = leftIndex + (rightIndex - leftIndex)/2;
        int bearingElement = array[bearingIndex];
        boolean leftSwap = false;
        boolean rightSwap = false;

        while (leftIndex != rightIndex) {

            if (array[leftIndex] <= bearingElement && leftIndex != bearingIndex) {
                leftIndex++;
            } else {
                leftSwap = true;
            }

            if (array[rightIndex] > bearingElement && rightIndex != bearingIndex) {
                rightIndex--;
            } else {
                rightSwap = true;
            }


            if (leftSwap && rightSwap) {
                int tmp = array[leftIndex];
                array[leftIndex] = array[rightIndex];
                array[rightIndex] = tmp;

                leftSwap = false;
                rightSwap = false;

                if (leftIndex == bearingIndex) {
                    bearingIndex = rightIndex;
                    //no decrement right value
                    leftIndex ++;

                } else if (rightIndex == bearingIndex) {
                    bearingIndex = leftIndex;
                    //no increment left value
                    rightIndex --;
                } else {
                    rightIndex--;
                    leftIndex++;
                }
            }

        }

        recursive2(startLeftIndex, bearingIndex-1, array);
        recursive2(bearingIndex+1, endRightIndex, array);

    }

}
