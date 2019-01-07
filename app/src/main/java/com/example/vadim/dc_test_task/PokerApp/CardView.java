package com.example.vadim.dc_test_task.PokerApp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.vadim.dc_test_task.R;

import java.util.List;


public class CardView extends LinearLayout {
    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_cardview, this, true);
    }

    // sets the ImageViews' drawables
    public void setImages(List<Drawable> cards) {
        LinearLayout view = (LinearLayout) findViewById(R.id.linearLayout_cards);
        for (int i = 0; i < view.getChildCount(); i++) {
            ImageView v = (ImageView) view.getChildAt(i);
            v.setImageDrawable(cards.get(i));
        }
    }

    // resets the images before new round
    public void resetImages() {
        LinearLayout view = (LinearLayout) findViewById(R.id.linearLayout_cards);
        for (int i = 0; i < view.getChildCount(); i++) {
            ImageView v = (ImageView) view.getChildAt(i);
            v.setImageDrawable(getResources().getDrawable(R.drawable.b2fv));
        }
    }
}

