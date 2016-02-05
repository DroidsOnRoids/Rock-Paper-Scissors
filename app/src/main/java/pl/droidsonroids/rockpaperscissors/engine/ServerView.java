package pl.droidsonroids.rockpaperscissors.engine;

import android.view.View;

public interface ServerView {
    void setOnRestartClickedListener(View.OnClickListener listener);
    void setOnFinishClickListener(View.OnClickListener listener);
}
