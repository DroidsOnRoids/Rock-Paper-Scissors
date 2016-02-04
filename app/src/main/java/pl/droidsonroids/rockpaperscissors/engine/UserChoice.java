package pl.droidsonroids.rockpaperscissors.engine;

public class UserChoice {
    private String android_id;
    private String name;
    private Choice user_choice;
    private Boolean is_winner;

    public UserChoice(final String android_id, final String name, final Choice user_choice) {
        this.android_id = android_id;
        this.name = name;
        this.user_choice = user_choice;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public String getName() {
        return name;
    }

    public Choice getUser_choice() {
        return user_choice;
    }

    public Boolean getIs_winner() {
        return is_winner;
    }

    void setIsWinner(boolean isWinner) {
        this.is_winner = isWinner;
    }

    public static enum Choice {
        paper,
        scissors,
        rock,
        lizard,
        spock
    }
}
