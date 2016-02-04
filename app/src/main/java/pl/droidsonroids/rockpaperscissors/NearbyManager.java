package pl.droidsonroids.rockpaperscissors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

public class NearbyManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.EndpointDiscoveryListener, Connections.ConnectionRequestListener, Connections.MessageListener {

    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    private static NearbyManager sInstance;

    private static Context sContext;

    private final Context mContext;

    private final GoogleApiClient mGoogleApiClient;

    private final List<Endpoint> mConnectedEndpoints = new ArrayList<>();

    private Endpoint mHostEndpoint;

    private EndpointDiscoveryListener mEndpointDiscoveryListener;
    private GameHostListener mGameHostListener;
    private GameClientListener mGameClientListener;

    private String mServiceId;

    public static void initialize(final Context context) {
        sContext = context.getApplicationContext();
    }

    public static synchronized NearbyManager getInstance() {
        if (sInstance == null) {
            sInstance = new NearbyManager(sContext);
        }

        return sInstance;
    }

    private NearbyManager(final Context context) {
        mContext = context;

        mServiceId = mContext.getString(R.string.service_id);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        startDiscovery();
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // todo: error handling
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // todo: error handling
    }

    @Override
    public void onConnectionRequest(final String remoteEndpointId, final String remoteDeviceId, final String remoteEndpointName, final byte[] payload) {
        final Endpoint endpoint = new Endpoint(remoteEndpointId, remoteDeviceId, null, remoteEndpointName);

        if (mGameHostListener != null) {
            mGameHostListener.onGameRequestReceived(endpoint);
        }
    }

    @Override
    public void onEndpointFound(final String endpointId, final String deviceId, final String serviceId, final String endpointName) {
        final Endpoint endpoint = new Endpoint(endpointId, deviceId, serviceId, endpointName);

        if (mEndpointDiscoveryListener != null) {
            mEndpointDiscoveryListener.onEndpointFound(endpoint);
        }
    }

    @Override
    public void onEndpointLost(final String endpointId) {
        final Endpoint endpoint = new Endpoint(endpointId);

        if (mEndpointDiscoveryListener != null) {
            mEndpointDiscoveryListener.onEndpointLost(endpoint);
        }
    }

    @Override
    public void onMessageReceived(final String remoteEndpointId, final byte[] payload, final boolean isReliable) {
        final String message = new String(payload);

        if (mGameClientListener != null) {
            mGameClientListener.onMessageReceived(message);
        } else if (mGameHostListener != null) {
            mGameHostListener.onMessageReceived(message);
        }
    }

    @Override
    public void onDisconnected(final String remoteEndpointId) {
        mConnectedEndpoints.remove(new Endpoint(remoteEndpointId));
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void startDiscovery() {
        if (isConnectedToNetwork()) {
            Nearby.Connections.startDiscovery(mGoogleApiClient, mServiceId, Connections.DURATION_INDEFINITE, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(MainActivity.class.getSimpleName(), "Discovery started!");
                            } else {
                                int statusCode = status.getStatusCode();
                                Log.d(MainActivity.class.getSimpleName(), "Discovery failed: " + statusCode);
                                // todo: error handling
                            }
                        }
                    });
        }
    }

    private void stopDiscovery() {
        if (isConnectedToNetwork()) {
            Nearby.Connections.stopDiscovery(mGoogleApiClient, mServiceId);
        }
    }

    public void startGame() {
        startAdvertising();
    }

    public void stopGame() {
        stopAdvertising();
    }

    public void connectToGame(final Endpoint endpoint) {
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, null, endpoint.getEndpointId(), null, new Connections.ConnectionResponseCallback() {
            @Override
            public void onConnectionResponse(String remoteEndpointId, Status status, byte[] bytes) {
                if (status.isSuccess()) {
                    mHostEndpoint = endpoint;

                    if (mGameClientListener != null) {
                        mGameClientListener.onHostConnected(endpoint);
                    }

                    Toast.makeText(mContext, "Request accepted :)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Request rejected :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, this);
    }

    private void startAdvertising() {
        if (isConnectedToNetwork()) {
            final AppMetadata appMetadata = new AppMetadata(Collections.singletonList(new AppIdentifier(mContext.getPackageName())));

            Nearby.Connections.startAdvertising(mGoogleApiClient, null, appMetadata, Connections.DURATION_INDEFINITE, this)
                    .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                        @Override
                        public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                            if (result.getStatus().isSuccess()) {
                                Log.d(MainActivity.class.getSimpleName(), "Advertising started!");
                            } else {
                                int statusCode = result.getStatus().getStatusCode();
                                Log.d(MainActivity.class.getSimpleName(), "Advertising failed: " + statusCode);
                                // todo: error handling
                            }
                        }
                    });
        }
    }

    private void stopAdvertising() {
        if (isConnectedToNetwork()) {
            Nearby.Connections.stopAdvertising(mGoogleApiClient);
        }
    }

    public void acceptGameRequest(final Endpoint endpoint) {
        acceptConnectionRequest(endpoint);
    }

    public void rejectGameRequest(final Endpoint endpoint) {
        rejectConnectionRequest(endpoint);
    }

    private void acceptConnectionRequest(final Endpoint endpoint) {
        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpoint.getEndpointId(), null, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            mConnectedEndpoints.add(endpoint);

                            if (mGameHostListener != null) {
                                mGameHostListener.onClientJoined(endpoint);
                            }

                            Toast.makeText(mContext, "Connected to " + endpoint.getEndpointName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Failed to connect to: " + endpoint.getEndpointName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void rejectConnectionRequest(final Endpoint endpoint) {
        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpoint.getEndpointId());
    }

    public void sendMessageToAllClients(final String message) {
        final List<String> endpointIds = new ArrayList<>();

        for (final Endpoint connectedEndpoint : mConnectedEndpoints) {
            endpointIds.add(connectedEndpoint.getEndpointId());
        }

        sendMessage(endpointIds, message);
    }

    public void sendMessageToHost(final String message) {
        sendMessage(mHostEndpoint, message);
    }

    private void sendMessage(final List<String> endpointIds, final String message) {
        Nearby.Connections.sendReliableMessage(mGoogleApiClient, endpointIds, message.getBytes());
    }

    private void sendMessage(final Endpoint endpoint, final String message) {
        Nearby.Connections.sendReliableMessage(mGoogleApiClient, endpoint.getEndpointId(), message.getBytes());
    }

    public void setEndpointDiscoveryListener(final EndpointDiscoveryListener endpointDiscoveryListener) {
        mEndpointDiscoveryListener = endpointDiscoveryListener;
    }

    public void setGameHostListener(final GameHostListener gameHostListener) {
        mGameHostListener = gameHostListener;
    }

    public void setGameClientListener(final GameClientListener gameClientListener) {
        mGameClientListener = gameClientListener;
    }

    @SuppressWarnings("deprecation")
    private boolean isConnectedToNetwork() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networkType);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }
}
