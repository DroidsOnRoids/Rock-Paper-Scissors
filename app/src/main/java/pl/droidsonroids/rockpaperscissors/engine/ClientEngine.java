package pl.droidsonroids.rockpaperscissors.engine;

import android.content.Context;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.rockpaperscissors.UserPreferences;

public class ClientEngine {
    private List<UserChoice> mLastGameResults = new ArrayList<>();
    private List<UserChoice> mCurrentGameResults = new ArrayList<>();

    private String mUserName;
    private String mAndroidId;

    private ClientView mClientView = new ClientViewNullObject();

    public ClientEngine(Context context) {
        mUserName = UserPreferences.getInstance(context).getUserName();
        mAndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void attachView(ClientView clientView) {
        mClientView = clientView;
        mClientView.setOnChoiceListener(new OnUserChoiceListener() {
            @Override
            public void onUserChoice(final UserChoice.Choice choice) {
                sendClientChoice(new UserChoice(mAndroidId, mUserName, choice));
            }
        });
        notifyView();
    }

    public void detachView() {
        mClientView = new ClientViewNullObject();
    }

    void processCurrentStatus(List<UserChoice> results) {
        if (!results.isEmpty() && results.get(0).getIs_winner() != null) {
            mLastGameResults = results;
        } else {
            mCurrentGameResults = results;
        }
    }

    private void notifyView() {
        mClientView.showCurrentResults(mCurrentGameResults);
        mClientView.showLastResults(mLastGameResults);
    }

    private void sendClientChoice(UserChoice userChoice) {

    }
}
