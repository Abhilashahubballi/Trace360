package com.trace360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Trace360Application {
    public static void main(String[] args) {
        SpringApplication.run(Trace360Application.class, args);
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   Trace360 Backend is running!   ║");
        System.out.println("║   PostgreSQL + Render Ready       ║");
        System.out.println("╚══════════════════════════════════╝");
    }
}
