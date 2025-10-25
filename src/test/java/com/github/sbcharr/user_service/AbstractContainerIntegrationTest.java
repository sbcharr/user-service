//package com.github.sbcharr.user_service;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//@SpringBootTest
//@Testcontainers
//@ActiveProfiles("test")
//public abstract class AbstractContainerIntegrationTest {
//    @Container
//    public static final MySQLContainer<?> MYSQL =
//            new MySQLContainer<>("mysql:8.0")
//                    .withDatabaseName("testdb")
//                    .withUsername("test")
//                    .withPassword("test")
//                    .withReuse(true);
//
//    @DynamicPropertySource
//    static void props(DynamicPropertyRegistry r) {
//        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
//        r.add("spring.datasource.username", MYSQL::getUsername);
//        r.add("spring.datasource.password", MYSQL::getPassword);
//        r.add("spring.jpa.hibernate.ddl-auto", () -> "update"); // or validate/create-drop
//    }
//}
//
