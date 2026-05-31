package com.trace360.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * ══════════════════════════════════════════
 *  Fix 6 — GeoService
 *  Automatic ETA + Distance Calculation
 * ══════════════════════════════════════════
 *
 * Uses Google Maps Distance Matrix API to:
 * 1. Calculate distance between agent and destination
 * 2. Get real traffic-aware ETA automatically
 * 3. Detect if agent is within 500m of destination
 *    → triggers auto status change to OUT_FOR_DELIVERY
 *
 * No more manually typing "Today 4:30 PM" as ETA!
 */
@Service
public class GeoService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GeoService.class);

    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calculate straight-line distance between two GPS points
     * Uses Haversine formula — same formula Google Maps uses internally
     *
     * @param lat1 Agent latitude
     * @param lng1 Agent longitude
     * @param lat2 Destination latitude
     * @param lng2 Destination longitude
     * @return Distance in kilometers
     */
    public double calculateDistanceKm(double lat1, double lng1,
                                       double lat2, double lng2) {
        // Earth radius in km
        final double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Check if agent is within 500 meters of destination
     * Used to auto-trigger OUT_FOR_DELIVERY status
     *
     * @param agentLat Agent latitude
     * @param agentLng Agent longitude
     * @param destLat  Destination latitude
     * @param destLng  Destination longitude
     * @return true if within 500m
     */
    public boolean isWithin500Meters(double agentLat, double agentLng,
                                      double destLat, double destLng) {
        double distanceKm = calculateDistanceKm(agentLat, agentLng, destLat, destLng);
        return distanceKm <= 0.5; // 0.5 km = 500 meters
    }

    /**
     * Get real-time ETA from Google Maps Distance Matrix API
     * Falls back to estimated calculation if no API key configured
     *
     * @param agentLat   Agent's current latitude
     * @param agentLng   Agent's current longitude
     * @param destLat    Destination latitude
     * @param destLng    Destination longitude
     * @return Human readable ETA string e.g. "~25 mins"
     */
    public String calculateEta(double agentLat, double agentLng,
                                double destLat, double destLng) {
        // If Google Maps API key is configured, use real traffic data
        if (googleMapsApiKey != null && !googleMapsApiKey.isEmpty()) {
            try {
                return getEtaFromGoogleMaps(agentLat, agentLng, destLat, destLng);
            } catch (Exception e) {
                log.warn("Google Maps API failed, using estimate: {}", e.getMessage());
            }
        }
        // Fallback: calculate from straight-line distance
        return estimateEtaFromDistance(agentLat, agentLng, destLat, destLng);
    }

    /**
     * Call Google Maps Distance Matrix API for traffic-aware ETA
     */
    private String getEtaFromGoogleMaps(double agentLat, double agentLng,
                                         double destLat, double destLng) {
        String url = String.format(
            "https://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=%f,%f&destinations=%f,%f&mode=driving&departure_time=now&key=%s",
            agentLat, agentLng, destLat, destLng, googleMapsApiKey
        );

        try {
            var response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null) {
                var rows = (java.util.List<?>) response.get("rows");
                if (rows != null && !rows.isEmpty()) {
                    var elements = (java.util.List<?>) ((java.util.Map<?,?>) rows.get(0)).get("elements");
                    if (elements != null && !elements.isEmpty()) {
                        var element = (java.util.Map<?,?>) elements.get(0);
                        var duration = (java.util.Map<?,?>) element.get("duration_in_traffic");
                        if (duration == null) duration = (java.util.Map<?,?>) element.get("duration");
                        if (duration != null) {
                            return "~" + duration.get("text");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Google Maps API error: {}", e.getMessage());
        }
        return estimateEtaFromDistance(agentLat, agentLng, destLat, destLng);
    }

    /**
     * Fallback ETA: estimate based on distance + average speed
     * Assumes average delivery speed of 30 km/h in city
     */
    private String estimateEtaFromDistance(double agentLat, double agentLng,
                                            double destLat, double destLng) {
        double distanceKm = calculateDistanceKm(agentLat, agentLng, destLat, destLng);
        double avgSpeedKmh = 30.0; // average city delivery speed
        double timeHours = distanceKm / avgSpeedKmh;
        int timeMinutes = (int) Math.ceil(timeHours * 60);

        if (timeMinutes < 2) return "Arriving now";
        if (timeMinutes < 60) return "~" + timeMinutes + " mins";

        int hours = timeMinutes / 60;
        int mins = timeMinutes % 60;
        return "~" + hours + "h " + mins + "m";
    }
}
