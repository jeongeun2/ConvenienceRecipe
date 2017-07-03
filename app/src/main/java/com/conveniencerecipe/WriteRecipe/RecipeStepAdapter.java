package com.conveniencerecipe.WriteRecipe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccei on 2016-08-05.
 */
public class RecipeStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<RecipeStepData> items = new ArrayList<RecipeStepData>();

    public void add(RecipeStepData data) {
        items.add(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_write_recipe_step, parent, false);

        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((RecipeStepViewHolder)holder).setMyData(items.get(position));
        ((RecipeStepViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.RecipeContext(), WriteRecipeDetailActivity.class);
                intent.putExtra("modify", true);
                intent.putExtra("position", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.RecipeContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView imageView;
        TextView stepNum;
        TextView textView;
        ImageView deleteBtn;

        public RecipeStepViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView)itemView.findViewById(R.id.recipe_image);
            stepNum = (TextView)itemView.findViewById(R.id.recipe_num);
            textView = (TextView)itemView.findViewById(R.id.recipe_text);
            deleteBtn = (ImageView)itemView.findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getPosition();
                    WriteRecipeMainActivity.images.remove(position);
                    WriteRecipeMainActivity.methods.remove(position);
                    for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                        if(WriteRecipeMainActivity.upLoadfiles.get(i).num-1 == position)
                            WriteRecipeMainActivity.upLoadfiles.remove(position);
                    }
                    for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                        if(WriteRecipeMainActivity.upLoadfiles.get(i).num >= position) {
                            WriteRecipeMainActivity.upLoadfiles.get(i).num--;
                        }
                    }
                    items.remove(getPosition());
                    WriteRecipeMainActivity.stepNum--;
                    for(int i=position; i<items.size(); i++) {
                        items.get(i).i = String.valueOf(Integer.parseInt(items.get(i).i) - 1);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void setMyData(RecipeStepData data) {
            if(data.image instanceof Bitmap) {
                imageView.setImageBitmap((Bitmap)data.image);
            } else if(data.image instanceof String) {
                Glide.with(MyApplication.RecipeContext()).load(Uri.parse((String)data.image)).override(240,240).into(imageView);
            }
            stepNum.setText(data.i);
            textView.setText(data.text);
        }
    }
}