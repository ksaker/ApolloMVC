package ru.my.collections;

/**
 * MyLinkedList
 * <p></p>
 *
 * @author Platonov Alexey
 * @since 1.0 18.12.16
 */
public class MyLinkedList<E> {

    private Element<E> header;
    private int size = 0;

    public MyLinkedList() {
        this(0);
    }

    public MyLinkedList(int size){
        header = new Element<>(null);
        header.next = header;
        header.prev = header;

        this.size = size;
    }

    private class Element<E> {
        E value;
        Element<E> prev;
        Element<E> next;

        Element(E value) {
            this.value = value;
        }

    }

    public E add(E value) {
        final Element<E> element = new Element<>(value);

        element.next = header;  //ссылаемся на следующий элемент (это голова, т.к больше элементов нет)
        element.prev = header.prev; //там все еще хранится предыдущий элемент
        element.prev.next = element; //У предыдущего элемента следующий теперь это текущий элемент
        header.prev = element; //Хедер теперь ссылается на текущий элемент

        size++;
        return value;
    }

    public E remove(int index) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("No such element");
        }

        Element<E> tmp = getElement(index);

        //Переписать ссылки на элементы, что бы не оставлять дырок
        tmp.prev.next = tmp.next;
        tmp.next.prev = tmp.prev;

        //Удалить все ссылки на элемент для GC, предварительно записав его поле value
        tmp.next = null;
        tmp.prev = null;
        E returnValue = tmp.value;
        tmp.value = null;

        size--;

        return returnValue;
    }

    private Element<E> getElement(int index) {
        Element<E> tmp = header.next;
        //самый простой алгоритм
//        for (int i = 0; i < index ; i++) {
//            tmp = tmp.next;
//        }

        //Улучшеный алгоритм поиска по индексу
        if (index > size >> 1) {
            //Если индекс находится в во второй половине списка, то логичней начинать поиск с конца
            for (int i = 0; i > index ; i--) {
                tmp = tmp.prev;
            }
        } else {
            //Если же в первой половие, то ищем сначала
            for (int i = 0; i < index ; i++) {
                tmp = tmp.next;
            }
        }

        return tmp;
    }

    public E get(int index) {
        return getElement(index).value;
    }

    int size() {
        return this.size;
    }
}
