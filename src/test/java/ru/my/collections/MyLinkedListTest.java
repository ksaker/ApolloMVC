package ru.my.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * MyLinkedListTest
 * <p></p>
 *
 * @author Platonov Alexey
 * @since 1.0 18.12.16
 */
public class MyLinkedListTest {

    @Test
    public void should_addElement_when_allCorrect() {
        final MyLinkedList<String> stringMyLinkedList = new MyLinkedList<>();

        final String test = "Test";
        stringMyLinkedList.add(test);
        final String test2 = "Test2";
        stringMyLinkedList.add(test2);

        Assert.assertEquals(2, stringMyLinkedList.size());
        Assert.assertEquals(test, stringMyLinkedList.get(0));
        Assert.assertEquals(test2, stringMyLinkedList.get(1));
    }

    @Test
    public void should_removeElement_when_allCorrect() {
        final MyLinkedList<String> stringMyLinkedList = new MyLinkedList<>();

        final String test = "Test";
        stringMyLinkedList.add(test);
        final String test2 = "Test2";
        stringMyLinkedList.add(test2);

        stringMyLinkedList.remove(0);
        Assert.assertEquals(test2, stringMyLinkedList.get(0));
    }

    @Test
    public void should_searchElement_when_allCorrect() {
        int maxSize = 1000;

        MyLinkedList<Integer> integerMyLinkedList = new MyLinkedList<>(maxSize);
        Random random = new Random();

        int controlValue1 = 0;
        int controlValue2 = 0;

        for (int i = 0; i < maxSize; i++) {
            int randomInt = random.nextInt();
            integerMyLinkedList.add(randomInt);

            if (i == 2) {
                controlValue1 = randomInt;
            }

            if (i == 998) {
                controlValue2 = randomInt;
            }
        }

        Assert.assertEquals(controlValue1, integerMyLinkedList.get(2).intValue());
        Assert.assertEquals(controlValue2, integerMyLinkedList.get(998).intValue());

    }

}
