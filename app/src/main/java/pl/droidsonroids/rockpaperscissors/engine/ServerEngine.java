package pl.droidsonroids.rockpaperscissors.engine;

import android.view.View;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.rockpaperscissors.Endpoint;
import pl.droidsonroids.rockpaperscissors.GameHostListener;
import pl.droidsonroids.rockpaperscissors.NearbyManager;

public class ServerEngine {
    private static ServerEngine sInstance;

    private List<UserChoice> mCurrentGameResults = new ArrayList<>();
    private ServerView mServerView = new ServerViewNullObject();
    private NearbyManager mNearbyManager = NearbyManager.getInstance();

    public static ServerEngine getInstance() {
        if (sInstance == null) {
            sInstance = new ServerEngine();
        }
        return sInstance;
    }

    public ServerEngine() {
        mNearbyManager.setGameHostListener(new GameHostListener() {
            @Override
            public void onGameRequestReceived(final Endpoint endpoint) {

            }

            @Override
            public void onClientJoined(final Endpoint endpoint) {

            }

            @Override
            public void onMessageReceived(final String message) {
                try {
                    UserChoice choice = new Gson().fromJson(message, UserChoice.class);
                    for (UserChoice currenUserChoice : mCurrentGameResults) {
                        if (choice.getAndroid_id().equals(currenUserChoice.getAndroid_id())) {
                            currenUserChoice.setName(choice.getName());
                            currenUserChoice.setChoice(choice.getUser_choice());
                            return;
                        }
                    }
                    mCurrentGameResults.add(choice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                broadcastResults();
            }
        });
    }

    public void attachView(ServerView serverView) {
        mServerView = serverView;
        mServerView.setOnFinishClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                broadcastResults();
            }
        });
    }

    private void broadcastResults() {
        FlexibleObjectConverter.flexiblyConvertObjectsList(mCurrentGameResults);
        mNearbyManager.sendMessageToAllClients(new Gson().toJson(mCurrentGameResults));
    }

    public void detachView() {
        mServerView.setOnFinishClickListener(null);
        mServerView.setOnRestartClickedListener(null);
        mServerView = new ServerViewNullObject();
    }
}
