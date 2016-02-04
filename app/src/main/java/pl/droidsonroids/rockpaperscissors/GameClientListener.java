package pl.droidsonroids.rockpaperscissors;

public interface GameClientListener extends GameListener {

    void onHostConnected(final Endpoint endpoint);
}
