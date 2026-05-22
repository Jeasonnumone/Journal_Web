package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {

    class Solution {
        public int longestConsecutive(int[] nums) { //遍历从最小的遍历

            Set<Integer> set = new HashSet<>();

            for(int num:nums){
                set.add(num);
            }

            int res = 0;
            for(int num:set){

                if(!set.contains(num-1)){
                    int cur = num;
                    int len = 0;
                    while(set.contains(cur)){
                        len++;
                        cur++;
                    }
                    res = Math.max(res, len);
                }

            }
            return res;
        }
    }




    @Test
    void contextLoads() {


        String encoded = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encoded);

    }


}
