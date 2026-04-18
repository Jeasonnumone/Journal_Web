package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

//@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {

        restoreIpAddresses("25525511135");

    }


    List<String> list = new ArrayList<>();
    List<String> res = new ArrayList<>();
    public List<String> restoreIpAddresses(String s) {
        backTracking(s, 0);
        return res;
    }

    public void backTracking(String s, int startIndex) {

        if (list.size() == 4) {
            // String ans = ".".join(list);
            if (startIndex < s.length()) return;
            String ans = String.join(".", list);
            res.add(ans);
            return;
        }

        for (int i = startIndex; i < s.length(); i++) {
            if (i - startIndex > 2) break;
            String str = s.substring(startIndex, i + 1);

            if (i > startIndex && str.charAt(startIndex) == '0') break;

            int val = Integer.valueOf(str);
            if (val > 255) continue;

            list.add(str);
            backTracking(s, i + 1);
            list.remove(list.size() - 1);
        }

    }

}
