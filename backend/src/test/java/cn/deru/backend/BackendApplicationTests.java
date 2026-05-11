package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {

    public String multiply(String num1, String num2) {

        if(num1.charAt(0) == 0 || num2.charAt(0) == 0) return "0";

        int m = num1.length();
        int n = num2.length();

        int[] res = new int[m+n];
        for(int i=m-1;i>=0;i--){

            for(int j=m-1;j>=0;j--){

                int a = num1.charAt(i)-'0';
                int b = num2.charAt(j)-'0';

                int mul = a*b;

                int p1 = i+j;
                int p2 = i+j+1;

                res[p2] += mul;

                res[p1] += res[p2]/10;
                res[p2] = res[p2]%10;
            }
        }

        StringBuilder str = new StringBuilder();
        for(int num:res){

            if(str.length()==0 && num == 0) continue;

            str.append(num);
        }
        return str.toString();
    }

    @Test
    void contextLoads() {

        System.out.println(multiply("0", "0"));

    }


}
