package com.example.yingjianwu.event_recommendation;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    List<Comment> cList = new ArrayList<>();

    public CommentAdapter(List<Comment> cList) {
        this.cList = cList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(
                viewGroup.getContext());
        View v = inflater.inflate(R.layout.comment_list_item, viewGroup, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.text.setText(cList.get(i).getText());
        viewHolder.user.setText(cList.get(i).getUserId());
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView user;
        private TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.comment_item_user);
            text = (TextView) itemView.findViewById(R.id.comment_item_description);
        }
    }

}
