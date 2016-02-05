package pl.droidsonroids.rockpaperscissors.engine;

import android.view.View;

public class ServerViewNullObject implements ServerView {
    @Override
    public void setOnRestartClickedListener(final View.OnClickListener listener) {
        //no-op
    }

    @Override
    public void setOnFinishClickListener(final View.OnClickListener listener) {
        //no-op
    }
}
