package com.saffron.texttospeech;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by saffron on 2/15/2018.
 */

public class RecordingListAdapter extends RecyclerView.Adapter<RecordingListAdapter.ViewHolder> {

    private List<Getter> list;

    RecordingListAdapter(List<Getter> list, Context context) {
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Getter g = list.get(position);
        holder.t11.setText(g.getHeader());
        holder.t22.setText(g.getBanner());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder

    {
        private TextView t11, t22;


        ViewHolder(View itemView) {
            super(itemView);

            t11 = itemView.findViewById(R.id.t1);
            t22 = itemView.findViewById(R.id.t2);
        }
    }
}
