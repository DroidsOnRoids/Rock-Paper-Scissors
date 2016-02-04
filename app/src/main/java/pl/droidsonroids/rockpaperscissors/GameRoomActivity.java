package pl.droidsonroids.rockpaperscissors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class GameRoomActivity extends AppCompatActivity {
    private static String BUNDLE_KEY_IS_CROUPIER = "is_croupier";

    public static void startActivity(Context context, boolean isCroupier) {
        Intent intent = new Intent(context, GameRoomActivity.class);
        intent.putExtra(BUNDLE_KEY_IS_CROUPIER, isCroupier);
        context.startActivity(intent);
    }
}
