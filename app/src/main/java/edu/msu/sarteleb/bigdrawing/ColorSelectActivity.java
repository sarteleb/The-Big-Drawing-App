package edu.msu.sarteleb.bigdrawing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Brandon on 5/4/2015.
 */
public class ColorSelectActivity extends ActionBarActivity{
    public static final String COLOR = "COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_color_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
    public void selectColor(int color) {
        Intent result = new Intent();
        result.putExtra(COLOR, color);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
