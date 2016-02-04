package pl.droidsonroids.rockpaperscissors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {

    private final Context mContext;

    private final List<Endpoint> mEndpoints = new ArrayList<>();

    private OnEndpointClickedListener mOnEndpointClickedListener;

    public GamesAdapter(final Context context) {
        mContext = context;
    }

    @Override
    public GamesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new GamesViewHolder(LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(final GamesViewHolder holder, final int position) {
        holder.displayEndpoint(mEndpoints.get(position));
    }

    @Override
    public int getItemCount() {
        return mEndpoints.size();
    }

    public void addEndpoint(final Endpoint endpoint) {
        mEndpoints.add(endpoint);
        notifyDataSetChanged();
    }

    public void removeEndpoint(final Endpoint endpoint) {
        mEndpoints.remove(endpoint);
        notifyDataSetChanged();
    }

    public void setOnEndpointClickedListener(final OnEndpointClickedListener onEndpointClickedListener) {
        mOnEndpointClickedListener = onEndpointClickedListener;
    }

    class GamesViewHolder extends RecyclerView.ViewHolder {

        @Bind(android.R.id.text1) TextView mEndpointNameText;

        public GamesViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void displayEndpoint(final Endpoint endpoint) {
            mEndpointNameText.setText(endpoint.getEndpointName());
            mEndpointNameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (mOnEndpointClickedListener != null) {
                        mOnEndpointClickedListener.onEndpointClicked(endpoint);
                    }
                }
            });
        }
    }
}