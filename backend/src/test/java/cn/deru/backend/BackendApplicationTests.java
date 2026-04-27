package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {

        StringBuilder str =  new StringBuilder();
        str.deleteCharAt(str.length()-1);

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((a, b) -> b-a);
        Deque<Integer> deque = new ArrayDeque<>();

        List<Integer> list = new ArrayList<>();
        list.sort((x,y)->y-x);

    }


}
