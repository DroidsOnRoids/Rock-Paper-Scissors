package pl.droidsonroids.rockpaperscissors.engine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class FlexibleObjectConverter {
    static void flexiblyConvertObjectsList(List<UserChoice> results) {
        EnumMap<UserChoice.Choice, List<UserChoice>> choiceListEnumMap = new EnumMap<>(UserChoice.Choice.class);
        for (UserChoice.Choice choice : UserChoice.Choice.values()) {
            choiceListEnumMap.put(choice, new ArrayList<UserChoice>());
        }

        for (UserChoice choice : results) {
            choice.setIsWinner(true);
            List<UserChoice> currentChoices = choiceListEnumMap.get(choice.getUser_choice());
            if (currentChoices != null) {
                currentChoices.add(choice);
            }
        }

        for (UserChoice choice : results) {
            for (UserChoice.Choice weakChoice : choice.getUser_choice().getWeakerChoices()) {
                for (UserChoice defendedUsers : choiceListEnumMap.get(weakChoice)) {
                    defendedUsers.setIsWinner(false);
                }
            }
        }
    }
}
