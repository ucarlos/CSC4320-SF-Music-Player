package com.example.csc4320_project_2.ui.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csc4320_project_2.R;

import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.ViewHolder> {
    private List<DatabaseItem> local_dataset;
    private TextView textView;

    // Constructor
    public DatabaseAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_database, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(local_dataset.get(position).get_item_name());
    }

    @Override
    public int getItemCount() {
        return local_dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view){
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int element_value = getAdapterPosition();
                    System.out.println("You clicked on " + element_value);

                }
            });

            textView =  view.findViewById(R.id.fs_item_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
