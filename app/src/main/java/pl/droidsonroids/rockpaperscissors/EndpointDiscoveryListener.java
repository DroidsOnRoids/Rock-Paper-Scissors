package pl.droidsonroids.rockpaperscissors;

public interface EndpointDiscoveryListener {

    void onEndpointFound(final Endpoint endpoint);
    void onEndpointLost(final Endpoint endpoint);
}
