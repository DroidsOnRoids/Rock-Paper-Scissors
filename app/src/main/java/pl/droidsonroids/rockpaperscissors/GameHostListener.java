package pl.droidsonroids.rockpaperscissors;

public interface GameHostListener extends GameListener {

    void onGameRequestReceived(final Endpoint endpoint);
    void onClientJoined(final Endpoint endpoint);
}
