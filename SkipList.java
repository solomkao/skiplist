package com.solomka;

import java.util.Random;

public class SkipList {

    private Node head = null;
    private Node tail = null;
    private int maxLevel;
    private int size;

    public SkipList() {
    }

    private class Node{
        int data;
        Node next;
        Node prev;
        Node up;
        Node down;

        public Node(int data) {
            this.data = data;
        }
    }

    public int getSize() {
        return size;
    }

    public boolean add(int data){
        Node newNode = new Node(data);

        if (head == null){
            //создаем пустую верхушку
            this.head = new Node(Integer.MIN_VALUE);
            this.tail = new Node(Integer.MAX_VALUE);

            head.next = tail;
            tail.prev = head;

            //создаем нулевой уровень, где разместим наш узел (и все остальные размещаются здесь в дальнейшем)
            Node newHead = new Node(Integer.MIN_VALUE);
            Node newTail = new Node(Integer.MAX_VALUE);

            head.down = newHead;
            tail.down = newTail;

            newHead.up = head;
            newTail.up = tail;

            newHead.next = newNode;
            newTail.prev = newNode;

            newNode.next = newTail;
            newNode.prev = newHead;

            size++;
            maxLevel = 0;

            //подбрасываем узел наверрх
            up(newHead,newNode);

            return true;
        }

        if(contains(data)){
            return false;
        }

        //если список не пустой, то вставляем новый узел
        //предварительно получив ссылку на узел, после которого необходимо вставить новый узел
        Node prevNode = findNode(data);
        newNode.next = prevNode.next;
        prevNode.next.prev = newNode;
        newNode.prev = prevNode;
        prevNode.next = newNode;

        //подбрасываем узел наверх
        up(prevNode, newNode);

        size++;
        return true;
    }


    private void up(Node prevNode, Node newNode){
        Node aboveNode;
        int currentLevel = 0;
        //flag нужен для того, чтобы вовремя остановить подбрасывание узла наверха
        //это происходит при создании нового уровня
        boolean flag = false;
        boolean flipCoin = flipCoin();
        System.out.println("Подбрасываем монетку "+flipCoin+" "+newNode.data);

        while(flipCoin){
            flipCoin = flipCoin();

            System.out.println("\tследующая попытка "+flipCoin);

            //создаем новый уровень
            if(currentLevel == maxLevel){
                createNewLevel();
                flag = true;
            }

            //ищем соседний узел на следующем уровне
            while(prevNode.up == null){
                    prevNode = prevNode.prev;
            }

            prevNode = prevNode.up;
            //и соединяем его с новым узлом на этои уровне
            aboveNode = new Node(newNode.data);
            newNode.up = aboveNode;
            aboveNode.down = newNode;

            prevNode.next.prev = aboveNode;
            aboveNode.next =  prevNode.next;
            aboveNode.prev = prevNode;
            prevNode.next = aboveNode;

            newNode = aboveNode;
            currentLevel++;

            //выходим из метода, если узел вышел на новый уровень
            //чтобы не плодить бесполезные башни
            if(flag){
                return;
            }
        }
    }

    private void createNewLevel(){
        Node newHead = new Node(head.data);
        Node newTail = new Node(tail.data);

        newHead.next = newTail;
        newTail.prev = newHead;

        newHead.down  = head;
        newTail.down = tail;

        head.up = newHead;
        tail.up = newTail;

        head = newHead;
        tail = newTail;
        maxLevel++;
    }



    public boolean remove(int data){
        Node removedNode = findNode(data);

        if (removedNode.data != data ){
            return false;
        }
        //удаление на нулевом уровне
        removedNode.prev.next = removedNode.next;
        removedNode.next.prev = removedNode.prev;

        //ищем и удаляем все копии на уровнях выше
        while(removedNode.up != null){
            removedNode = removedNode.up;
            removedNode.prev.next = removedNode.next;
            removedNode.next.prev = removedNode.prev;
        }
        //частный случай - если удаляемый элемент - единственный на верхнем уровне
        //то удаляем весь уровень с переопределением ссылок головы и хвоста
        if(removedNode.prev == head.down && removedNode.next == tail.down){
            head.down = head.down.down;
            tail.down = tail.down.down;
            maxLevel--;
        }

        size--;
        return true;
    }

    public boolean contains(int data){
        Node p = findNode(data);
        return (data == p.data);
    }

    private Node findNode(int x){
        Node current = head;
        while (true){
            while(current.next.data != tail.data && x >= current.next.data ){
                current = current.next;
            }
            //если мы дошли к этой точки, то:
            //текущий элемент current меньше, чем Х (или равен ему), но элемент, следующий за current, больше за Х.
            //другими словами, место для нового элемента находится между current и current.next
            //поэтому спускаемся вниз
            //и повторяем цикл заново
            if(current.down != null){
                current = current.down;
            }
            //пока не дойдем до самого низа - нулевого уровня
            else{
                break;
            }
        }
        //возвращаем элемент, после которого необходимо вставить новый элемент
        return current;
    }
    //D:\Udemy\skipList\src\com\solomka\SkipList.java


    private boolean flipCoin(){
        Random random = new Random();
        int coin = random.nextInt(100);
        boolean sideOne =  coin > 50;
        coin = random.nextInt(100);
        boolean sideTwo =  coin > 50;
        return sideOne || sideTwo;
    }

    public void print(){
        Node newHead = head;
        Node current = newHead;
        while(true){
            while (current.data != tail.data){
                System.out.print(current.data+"\u001B[32m"+" <-> "+"\u001B[0m");
                current = current.next;
            }
            System.out.print(current.data);

            if(current.down != null){
                newHead = newHead.down;
                current = newHead;
                System.out.println();
            }else{
                break;
            }
        }
        System.out.println();
    }


    public void printInARow(){
        Node current = head.down;
        while (current.down != null)
            current = current.down;

        while (current != null){
            System.out.print(current.data+"  ");
            current = current.next;
        }
        System.out.println();
    }


}
