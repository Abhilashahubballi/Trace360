package com.trace360.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * ══════════════════════════════════════════
 *  Fix 2 — WebSocket Configuration
 *  Enables REAL-TIME live tracking on map
 * ══════════════════════════════════════════
 *
 * How it works:
 * - Agent sends GPS update → Spring Boot receives it
 * - Spring Boot immediately PUSHES it to all browsers
 *   watching that package — no refresh needed
 *
 * This is exactly how Swiggy/Zomato shows
 * the delivery partner moving on the map live.
 *
 * @EnableWebSocketMessageBroker → enables WebSocket + STOMP
 * STOMP = Simple Text Oriented Messaging Protocol
 * It sits on top of WebSocket and gives us topic-based messaging
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure the message broker
     * /topic → used for broadcasting to multiple subscribers
     * /app  → prefix for messages coming FROM client TO server
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        // Customers subscribe to /topic/track/{trackingId}
        // When agent updates — backend publishes to that topic
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register WebSocket endpoint
     * Frontend connects to: ws://localhost:8080/ws
     * setAllowedOriginPatterns("*") → allow all origins (CORS for WebSocket)
     * withSockJS() → fallback for browsers that don't support WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
