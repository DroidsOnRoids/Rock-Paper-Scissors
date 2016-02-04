package pl.droidsonroids.rockpaperscissors.engine;

import java.util.List;

public class ClientViewNullObject implements ClientView {
    @Override
    public void showLastResults(final List<UserChoice> results) {
        //no-op
    }

    @Override
    public void showCurrentResults(final List<UserChoice> results) {
        //no-op
    }

    @Override
    public void setOnChoiceListener(final OnUserChoiceListener listener) {
        //no-op
    }
}
