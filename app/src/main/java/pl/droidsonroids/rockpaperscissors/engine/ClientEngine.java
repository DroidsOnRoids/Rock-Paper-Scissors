package pl.droidsonroids.rockpaperscissors.engine;

import android.content.Context;
import android.provider.Settings;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.rockpaperscissors.Endpoint;
import pl.droidsonroids.rockpaperscissors.GameClientListener;
import pl.droidsonroids.rockpaperscissors.NearbyManager;
import pl.droidsonroids.rockpaperscissors.UserPreferences;

public class ClientEngine {

    private static ClientEngine sInstance;

    private List<UserChoice> mLastGameResults = new ArrayList<>();
    private List<UserChoice> mCurrentGameResults = new ArrayList<>();

    private String mUserName;
    private String mAndroidId;

    private ClientView mClientView = new ClientViewNullObject();

    private NearbyManager mNearbyManager = NearbyManager.getInstance();

    public static ClientEngine getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ClientEngine(context);
        }
        return sInstance;
    }

    private ClientEngine(Context context) {
        mUserName = UserPreferences.getInstance(context).getUserName();
        mAndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        mNearbyManager.setGameClientListener(new GameClientListener() {
            @Override
            public void onHostConnected(final Endpoint endpoint) {

            }

            @Override
            public void onMessageReceived(final String message) {
                try {
                    List<UserChoice> results = new Gson().fromJson(message, new ArrayList<UserChoice>().getClass());
                    if (!results.isEmpty() && results.get(0).getIs_winner() != null) {
                        mLastGameResults = results;
                    } else {
                        mCurrentGameResults = results;
                    }
                    notifyView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void attachView(ClientView clientView) {
        mClientView = clientView;
        mClientView.setOnChoiceListener(new OnUserChoiceListener() {
            @Override
            public void onUserChoice(final UserChoice.Choice choice) {
                mNearbyManager.sendMessageToServer(new Gson().toJson(new UserChoice(mAndroidId, mUserName, choice)));
            }
        });
        notifyView();
    }

    public void detachView() {
        mClientView.setOnChoiceListener(null);
        mClientView = new ClientViewNullObject();
    }

    private void notifyView() {
        mClientView.showCurrentResults(mCurrentGameResults);
        mClientView.showLastResults(mLastGameResults);
    }
}
