package com.example.dropdownmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DropDownMenuView dropDownMenu;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dropDownMenu = (DropDownMenuView)findViewById(R.id.dropDownMenu);
        TextView textView = (TextView) findViewById(R.id.view_top);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(!dropDownMenu.isOpen()){
                    dropDownMenu.open();
                }
            }
        });
    }
}
