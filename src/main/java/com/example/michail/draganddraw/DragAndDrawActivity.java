package com.example.michail.draganddraw;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;



public class DragAndDrawActivity extends FragmentActivity implements NavigationFragment.NavigationFragmentListner {

    private DragAndDrawFragment dragAndDrawFragment = DragAndDrawFragment.newInstance();
    private NavigationFragment navigationFragment = new NavigationFragment();


    @Override
    public void setItem(String itemName) {
        Log.i("DragAndDrawActivity", itemName);
        dragAndDrawFragment.setItemName(itemName);
    }

    @Override
    public void setUndo(Boolean index) {
        dragAndDrawFragment.setRedo(index);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_draw,dragAndDrawFragment,dragAndDrawFragment.TAG);
        fragmentTransaction.add(R.id.fragment_navigation,navigationFragment,navigationFragment.TAG);
        fragmentTransaction.commit();
    }
}
