package com.dongnao.homework9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.start_trashBin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrashBinView trashBinView = (TrashBinView) MainActivity.this
                        .findViewById(R.id.trashBin);
                if (trashBinView.isOpened()) {
                    trashBinView.close();
                } else {
                    trashBinView.open();
                }
            }
        });
        this.findViewById(R.id.start_ripple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RippleView rippleView = (RippleView) MainActivity.this
                        .findViewById(R.id.rippleView);
                rippleView.restart();
            }
        });
    }
}
