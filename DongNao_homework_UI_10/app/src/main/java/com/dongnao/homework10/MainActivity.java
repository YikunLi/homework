package com.dongnao.homework10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SearchDrawable searchDrawable = new SearchDrawable();
        ImageView imageView = (ImageView) this.findViewById(R.id.imageView);
        imageView.setImageDrawable(searchDrawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDrawable.isLoading()) {
                    searchDrawable.reset();
                } else {
                    searchDrawable.loading();
                }
            }
        });
    }
}
