package cn.deru.backend;

import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {

    @Test
    void solution() {
        String str1 = "11";
        String str2 = "1";
        System.out.println(str1.compareTo(str2));
    }

    @Test
    void contextLoads() {



        String encoded = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encoded);

    }


}
