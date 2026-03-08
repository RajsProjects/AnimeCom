package com.animecom.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies the Spring application context loads without errors.
 *
 * Uses MOCK web environment so the test does not require a running Redis
 * instance or a reachable JWT issuer — those are integration-time concerns.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
