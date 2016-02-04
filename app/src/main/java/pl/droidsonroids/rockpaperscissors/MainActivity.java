package pl.droidsonroids.rockpaperscissors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.EndpointDiscoveryListener, Connections.MessageListener {

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    @Bind(R.id.recycler_games) RecyclerView mGamesRecycler;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private GoogleApiClient mGoogleApiClient;

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
                connectTo(endpoint);
            }
        });

        mGamesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mGamesRecycler.setAdapter(mGamesAdapter);

        setSupportActionBar(mToolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
    public void onConnected(final Bundle bundle) {
        startDiscovery();
    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionRequest(final String remoteEndpointId, final String remoteDeviceId, final String remoteEndpointName, final byte[] payload) {
        final Endpoint endpoint = new Endpoint(remoteEndpointId, remoteDeviceId, null, remoteEndpointName);

        new AlertDialog.Builder(this)
                .setTitle("New connection request")
                .setMessage(remoteEndpointName + " wants to join your game. Accept?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        acceptConnectionRequest(endpoint);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        rejectConnectionRequest(endpoint);
                    }
                })
                .show();
    }

    @Override
    public void onEndpointFound(final String endpointId, final String deviceId, final String serviceId, final String endpointName) {
        final Endpoint endpoint = new Endpoint(endpointId, deviceId, serviceId, endpointName);
        mGamesAdapter.addEndpoint(endpoint);
        Log.d(MainActivity.class.getSimpleName(), "Endpoint found: " + endpointId + " " + deviceId + " " + serviceId + " " + endpointName);
    }

    @Override
    public void onEndpointLost(final String endpointId) {
        final Endpoint endpoint = new Endpoint(endpointId);
        mGamesAdapter.removeEndpoint(endpoint);
        Log.d(MainActivity.class.getSimpleName(), "Endpoint lost: " + endpointId);
    }

    @Override
    public void onMessageReceived(final String remoteEndpointId, final byte[] payload, final boolean isReliable) {
        final String message = new String(payload);
        Toast.makeText(this, remoteEndpointId + " send you a message: " + message, Toast.LENGTH_SHORT).show();
        if ("Hello!".equals(message)) {
            Nearby.Connections.sendReliableMessage(mGoogleApiClient, remoteEndpointId, "Hello to you too!".getBytes());
        }
    }

    @Override
    public void onDisconnected(final String remoteEndpointId) {

    }

    @OnClick(R.id.button_start_game)
    public void startAdvertising() {
        if (isConnectedToNetwork()) {
            final List<AppIdentifier> appIdentifierList = new ArrayList<>();
            appIdentifierList.add(new AppIdentifier(getPackageName()));

            final AppMetadata appMetadata = new AppMetadata(appIdentifierList);

            final String name = null;

            Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, 0L, this)
                    .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                        @Override
                        public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                            if (result.getStatus().isSuccess()) {
                                Log.d(MainActivity.class.getSimpleName(), "Advertising started!");
                            } else {
                                int statusCode = result.getStatus().getStatusCode();
                                Log.d(MainActivity.class.getSimpleName(), "Advertising failed: " + statusCode);
                            }
                        }
                    });
        }
    }

    private void startDiscovery() {
        if (isConnectedToNetwork()) {
            final String serviceId = getString(R.string.service_id);

            Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, Connections.DURATION_INDEFINITE, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(MainActivity.class.getSimpleName(), "Discovery started!");
                            } else {
                                int statusCode = status.getStatusCode();
                                Log.d(MainActivity.class.getSimpleName(), "Discovery failed: " + statusCode);
                            }
                        }
                    });
        }
    }

    private void connectTo(final Endpoint endpoint) {
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, null, endpoint.getEndpointId(), null, new Connections.ConnectionResponseCallback() {
            @Override
            public void onConnectionResponse(String remoteEndpointId, Status status, byte[] bytes) {
                if (status.isSuccess()) {
                    Nearby.Connections.sendReliableMessage(mGoogleApiClient, endpoint.getEndpointId(), "Hello!".getBytes());
                    Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not connected!", Toast.LENGTH_SHORT).show();
                }
            }
        }, this);
    }

    private void acceptConnectionRequest(final Endpoint endpoint) {
        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpoint.getEndpointId(), null, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Toast.makeText(MainActivity.this, "Connected to " + endpoint.getEndpointName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to connect to: " + endpoint.getEndpointName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void rejectConnectionRequest(final Endpoint endpoint) {
        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpoint.getEndpointId());
    }

    @SuppressWarnings("deprecation")
    private boolean isConnectedToNetwork() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connectivityManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }
}
