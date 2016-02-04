package pl.droidsonroids.rockpaperscissors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends AppCompatActivity implements EndpointDiscoveryListener, GameHostListener, GameClientListener {

    @Bind(R.id.recycler_games) RecyclerView mGamesRecycler;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private NearbyManager mNearbyManager;

    private GamesAdapter mGamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mGamesAdapter = new GamesAdapter(this);
        mGamesAdapter.setOnEndpointClickedListener(new OnEndpointClickedListener() {
            @Override
            public void onEndpointClicked(final Endpoint endpoint) {
                mNearbyManager.connectToGame(endpoint);
            }
        });

        mGamesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mGamesRecycler.setAdapter(mGamesAdapter);

        setSupportActionBar(mToolbar);

        NearbyManager.initialize(this);

        mNearbyManager = NearbyManager.getInstance();
        mNearbyManager.setEndpointDiscoveryListener(this);
        mNearbyManager.setGameHostListener(this);
        mNearbyManager.setGameClientListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNearbyManager.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNearbyManager.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onEndpointFound(final Endpoint endpoint) {
        mGamesAdapter.addEndpoint(endpoint);
    }

    @Override
    public void onEndpointLost(final Endpoint endpoint) {
        mGamesAdapter.removeEndpoint(endpoint);
    }

    @Override
    public void onGameRequestReceived(final Endpoint endpoint) {
        new AlertDialog.Builder(this)
                .setTitle("New connection request")
                .setMessage(endpoint.getEndpointName() + " wants to join your game. Accept?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mNearbyManager.acceptGameRequest(endpoint);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mNearbyManager.rejectGameRequest(endpoint);
                    }
                })
                .show();
    }

    @Override
    public void onClientJoined(final Endpoint endpoint) {
        mNearbyManager.sendMessageToAllClients("Hello client!");
    }

    @OnCheckedChanged(R.id.button_start_game)
    public void startGame(final CompoundButton view, final boolean checked) {
        if (checked) {
            mNearbyManager.startGame();
        } else {
            mNearbyManager.stopGame();
        }
    }

    @Override
    public void onHostConnected(final Endpoint endpoint) {
        mNearbyManager.sendMessageToHost("Hello host!");
    }

    @Override
    public void onMessageReceived(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
