package ru.nsu.resortbooking;

import org.springframework.boot.SpringApplication;

public class TestResortBookingApplication {

    public static void main(String[] args) {
        SpringApplication.from(ResortBookingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
