package com.softgyan.pets.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softgyan.pets.CatalogActivity;
import com.softgyan.pets.EditorActivity;
import com.softgyan.pets.R;
import com.softgyan.pets.models.PetsModels;

import java.util.List;
import java.util.Locale;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ViewHolder> {

    final private List<PetsModels> petsModels;
    final Context mContext;

    public PetsAdapter(Context context, List<PetsModels> petsModels) {
        mContext = context;
        this.petsModels = petsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pets_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetsModels models = petsModels.get(position);

        holder.petName.setText(models.getBread());
        holder.name.setText(models.getName());
        holder.id.setText(String.format("%d.", position + 1));


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditorActivity.class);
            intent.putExtra(EditorActivity.PET_MODELS, models);
            intent.putExtra(CatalogActivity.CURD_OPERATION, CatalogActivity.UPDATE);
            intent.putExtra(CatalogActivity.INDEX, position);
            ((Activity) mContext).startActivityForResult(intent, CatalogActivity.CALL_EDITOR_REQUEST_CODE);

        });
    }

    @Override
    public int getItemCount() {
        return petsModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView petName, name, id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            name = itemView.findViewById(R.id.name);
            id = itemView.findViewById(R.id.id);
        }
    }
}
