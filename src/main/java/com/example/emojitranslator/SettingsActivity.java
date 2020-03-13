package com.example.emojitranslator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

    public class SettingsActivity extends AppCompatActivity {


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.second_activity);

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);

        }

        private void setSupportActionBar(Toolbar settingsToolbar) {
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            //inflate the menu. This adds items to the action bar, if it is present
            getMenuInflater().inflate(R.menu.settingspage_toolbar, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();

            switch (id) {
                case R.id.backButton:
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            return super.onOptionsItemSelected(menuItem);
        }

    }


