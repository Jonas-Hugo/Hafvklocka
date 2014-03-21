package com.hugo.hafvklocka.activities;

import com.hugo.hafvklocka.R;
import com.hugo.hafvklocka.R.id;
import com.hugo.hafvklocka.R.layout;
import com.hugo.hafvklocka.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class FinalViewActivity extends Activity {
	
	private GridLayout grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_view);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        grid = (GridLayout)findViewById(R.id.gridView);
        grid.setColumnCount(7);
        grid.setRowCount(21);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	final int size = grid.getChildCount();
    	for(int i = 0; i < size; i++) {
    	  ViewGroup gridChild = (ViewGroup) grid.getChildAt(i);
    	  int childSize = gridChild.getChildCount();
    	  for(int k = 0; k < childSize; k++) {
    	    gridChild.getChildAt(k);
    	  }
    	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_final_view, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
