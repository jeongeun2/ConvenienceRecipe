package com.conveniencerecipe.WriteRecipe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.conveniencerecipe.R;

/**
 * Created by ccei on 2016-08-28.
 */
public class CancelWriteRecipe extends Dialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.cancel_recipe_dialog);

        setLayout();
    }

    public CancelWriteRecipe(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    private ImageView cancelImage;
    private ImageView yesBtn;
    private ImageView noBtn;

    private void setLayout(){
        cancelImage = (ImageView)findViewById(R.id.cancel_image);
        cancelImage.setImageResource(R.drawable.cancel_momockgee);

        yesBtn = (ImageView)findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                WriteRecipeMainActivity.recipeMainActivity.finish();
            }
        });

        noBtn = (ImageView)findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
