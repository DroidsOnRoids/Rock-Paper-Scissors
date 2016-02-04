package pl.droidsonroids.rockpaperscissors;

public class Endpoint {

    private final String mEndpointId;
    private final String mDeviceId;
    private final String mServiceId;
    private final String mEndpointName;

    public Endpoint(final String endpointId, final String deviceId, final String serviceId, final String endpointName) {
        mEndpointId = endpointId;
        mDeviceId = deviceId;
        mServiceId = serviceId;
        mEndpointName = endpointName;
    }

    public Endpoint(final String endpointId) {
        mEndpointId = endpointId;
        mDeviceId = null;
        mServiceId = null;
        mEndpointName = null;
    }

    public String getEndpointId() {
        return mEndpointId;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public String getServiceId() {
        return mServiceId;
    }

    public String getEndpointName() {
        return mEndpointName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Endpoint endpoint = (Endpoint) o;

        return mEndpointId.equals(endpoint.mEndpointId);

    }

    @Override
    public int hashCode() {
        return mEndpointId.hashCode();
    }
}
