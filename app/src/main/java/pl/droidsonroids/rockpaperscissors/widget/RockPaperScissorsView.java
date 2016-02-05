package pl.droidsonroids.rockpaperscissors.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import pl.droidsonroids.rockpaperscissors.R;
import pl.droidsonroids.rockpaperscissors.engine.UserChoice;

public class RockPaperScissorsView extends ImageView {

    private final GestureDetector mGestureDetector;

    private final Bitmap mMaskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mask);

    private OnUserChoiceListener mOnUserChoiceListener;

    public RockPaperScissorsView(final Context context) {
        this(context, null);
    }

    public RockPaperScissorsView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RockPaperScissorsView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.FIT_XY);
        setImageResource(R.drawable.diagram);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(final MotionEvent e) {
                final float scale = (float) mMaskBitmap.getWidth() / getWidth();

                final int color = mMaskBitmap.getPixel((int) (e.getX() * scale), (int) (e.getY() * scale));

                final UserChoice.Choice choice = getUserChoiceFromColor(color);

                Log.d("SDSD", choice + "");

                if (mOnUserChoiceListener != null && choice != null) {
                    mOnUserChoiceListener.onUserChoice(choice);
                }

                return true;
            }
        });
    }

    private UserChoice.Choice getUserChoiceFromColor(final int color) {
        switch (color) {
            case Color.BLACK:
                return UserChoice.Choice.scissors;
            case Color.WHITE:
                return UserChoice.Choice.lizard;
            case Color.RED:
                return UserChoice.Choice.paper;
            case Color.GREEN:
                return UserChoice.Choice.rock;
            case Color.BLUE:
                return UserChoice.Choice.spock;
            default:
                return null;
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public void setOnUserChoiceListener(final OnUserChoiceListener onUserChoiceListener) {
        mOnUserChoiceListener = onUserChoiceListener;
    }

    public interface OnUserChoiceListener {

        void onUserChoice(final UserChoice.Choice choice);
    }
}
