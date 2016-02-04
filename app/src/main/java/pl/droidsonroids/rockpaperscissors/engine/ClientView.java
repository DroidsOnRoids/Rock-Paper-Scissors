package pl.droidsonroids.rockpaperscissors.engine;

import java.util.List;

public interface ClientView {
    void showLastResults(List<UserChoice> results);
    void showCurrentResults(List<UserChoice> results);
    void setOnChoiceListener(OnUserChoiceListener listener);
}
