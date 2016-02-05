package pl.droidsonroids.rockpaperscissors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.Collections;
import java.util.List;
import pl.droidsonroids.rockpaperscissors.engine.ClientEngine;
import pl.droidsonroids.rockpaperscissors.engine.ClientView;
import pl.droidsonroids.rockpaperscissors.engine.OnUserChoiceListener;
import pl.droidsonroids.rockpaperscissors.engine.ServerEngine;
import pl.droidsonroids.rockpaperscissors.engine.ServerView;
import pl.droidsonroids.rockpaperscissors.engine.UserChoice;
import pl.droidsonroids.rockpaperscissors.widget.RockPaperScissorsView;

public class GameRoomActivity extends AppCompatActivity implements ClientView, ServerView {
    private static String BUNDLE_KEY_IS_CROUPIER = "is_croupier";

    @Bind(R.id.recyycler_view_previous_results) RecyclerView mRecyclerPreviousResults;
    @Bind(R.id.recycler_view_current_results)
    RecyclerView mRecyclerCurrentResults;
    @Bind(R.id.layout_server_buttons)
    LinearLayout mLayoutServerButtons;
    @Bind(R.id.button_finish)
    Button mButtonFinish;
    @Bind(R.id.button_restart)
    Button mButtonRestart;
    @Bind(R.id.rock_scissors_view)
    RockPaperScissorsView mRockScissorsView;

    private boolean mIsCrupierMode;
    private UserChoiceAdapter mPreviousResultsAdapter = new UserChoiceAdapter(true);
    private UserChoiceAdapter mCurrentChoiceAdapter = new UserChoiceAdapter(false);

    public static void startActivity(Context context, boolean isCroupier) {
        Intent intent = new Intent(context, GameRoomActivity.class);
        intent.putExtra(BUNDLE_KEY_IS_CROUPIER, isCroupier);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        ButterKnife.bind(this);

        mIsCrupierMode = getIntent().getBooleanExtra(BUNDLE_KEY_IS_CROUPIER, false);

        mLayoutServerButtons.setVisibility(mIsCrupierMode ? View.VISIBLE : View.GONE);

        setupRecyclerViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsCrupierMode) {
            ServerEngine.getInstance().attachView(this);
        } else {
            ClientEngine.getInstance(this).attachView(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCrupierMode) {
            ServerEngine.getInstance().detachView();
        } else {
            ClientEngine.getInstance(this).detachView();
        }
    }

    private void setupRecyclerViews() {
        mRecyclerPreviousResults.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerPreviousResults.setAdapter(mPreviousResultsAdapter);

        mRecyclerCurrentResults.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCurrentResults.setAdapter(mCurrentChoiceAdapter);
    }

    @Override
    public void showLastResults(final List<UserChoice> results) {
        mPreviousResultsAdapter.setResults(results);
    }

    @Override
    public void showCurrentResults(final List<UserChoice> results) {
        mCurrentChoiceAdapter.setResults(results);
    }

    @Override
    public void setOnChoiceListener(final OnUserChoiceListener listener) {
        mRockScissorsView.setOnUserChoiceListener(new RockPaperScissorsView.OnUserChoiceListener() {
            @Override
            public void onUserChoice(final UserChoice.Choice choice) {
                listener.onUserChoice(choice);
            }
        });
    }

    @Override
    public void setOnRestartClickedListener(final View.OnClickListener listener) {
        mButtonRestart.setOnClickListener(listener);
    }

    @Override
    public void setOnFinishClickListener(final View.OnClickListener listener) {
        mButtonFinish.setOnClickListener(listener);
    }

    class UserChoiceViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.text_username)
        TextView mTextUsername;
        @Bind(R.id.image_choice)
        ImageView mImageChoice;
        @Bind(R.id.image_status_indicator) ImageView mImageStatus;

        public UserChoiceViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class UserChoiceAdapter extends RecyclerView.Adapter<UserChoiceViewHolder> {

        private List<UserChoice> mItems = Collections.emptyList();
        private boolean mShowChoices;

        public UserChoiceAdapter(boolean showChoices) {
            mShowChoices = showChoices;
        }

        @Override
        public UserChoiceViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new UserChoiceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_results,parent,false));
        }

        @Override
        public void onBindViewHolder(final UserChoiceViewHolder holder, final int position) {
            UserChoice userChoice = mItems.get(position);
            holder.mTextUsername.setText(userChoice.getName());

            if (mShowChoices) {
                holder.mImageChoice.setVisibility(View.VISIBLE);
                holder.mImageStatus.setVisibility(View.VISIBLE);

                //todo put images
                if (userChoice.getIs_winner() == Boolean.TRUE) {
                    holder.mImageStatus.setColorFilter(Color.GREEN);
                } else {
                    holder.mImageStatus.setColorFilter(Color.RED);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setResults(List<UserChoice> results) {
            mItems = results;
            notifyDataSetChanged();
        }
    }
}
