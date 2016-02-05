package pl.droidsonroids.rockpaperscissors.engine;

import java.util.ArrayList;
import java.util.List;

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

    public void setName(final String name) {
        this.name = name;
    }

    public void setChoice(Choice choice) {
        this.user_choice = choice;
    }

    public static enum Choice {
        scissors,
        paper,
        rock,
        lizard,
        spock;

        boolean isBeatingChoice(Choice choice) {
            if (choice.ordinal() > this.ordinal()) {
                return (choice.ordinal() - this.ordinal()) % 2 == 1;
            } else if (choice.ordinal() < this.ordinal()) {
                return (this.ordinal() - choice.ordinal()) % 2 == 0;
            } else {
                return false;
            }
        }

        public List<Choice> getWeakerChoices() {
            List<Choice> weakerChoices = new ArrayList<>(2);
            for (Choice choice : Choice.values()) {
                if (isBeatingChoice(choice)) {
                    weakerChoices.add(choice);
                }
            }
            return weakerChoices;
        }
    }
}
