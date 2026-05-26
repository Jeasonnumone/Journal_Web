package cn.deru.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

//@SpringBootTest
class BackendApplicationTests {



    @Test
    void contextLoads() {



        String encoded = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encoded);

    }


}
