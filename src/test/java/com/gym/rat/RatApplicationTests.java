package com.gym.rat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // <-- le indica a Spring usar application-test.properties
class RatApplicationTests {

    @Test
    void contextLoads() {
    }

}
