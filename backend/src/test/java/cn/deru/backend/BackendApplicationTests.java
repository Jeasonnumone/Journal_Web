package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {

    public String decodeString(String s) {

        char[] chs = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        StringBuilder res = new StringBuilder();
        for(int i=0;i<chs.length;i++){
            if(chs[i] == ']'){
                StringBuilder str = new StringBuilder();
                while(stack.peek()!='['){
                    str.append(stack.pop());
                }
                stack.pop(); //去除[
                str.reverse();

                String code = new String("");
                int num = stack.pop();
                for(int j=0;j<num;j++){
                    code += str.toString();
                }

                res.append(code);
                continue;
            }
            stack.push(chs[i]);
        }

        return res.toString();
    }

    @Test
    void contextLoads() {

        decodeString("3[a]2[bc]");


    }


}
