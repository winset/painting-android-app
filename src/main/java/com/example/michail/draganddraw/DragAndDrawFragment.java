package com.example.michail.draganddraw;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DragAndDrawFragment extends Fragment implements ItemProvider {
    public static final String TAG = "DragAndDrawFragment";
    public static String mItemName;
    public static Boolean mIndex;

    public static DragAndDrawFragment newInstance() {
        Bundle args = new Bundle();
        args.putString("itemName", mItemName);
        DragAndDrawFragment dragAndDrawFragment = new DragAndDrawFragment();
        dragAndDrawFragment.setArguments(args);
        return dragAndDrawFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drag_and_draw, container, false);

        return v;
    }

    @Override
    public String getItemShape() {
        return mItemName;
    }


    public void setItemName(String itemName) {
        mItemName = itemName;
        Log.i(TAG, mItemName);
    }

    public void setRedo(Boolean index) {
        mIndex = index;
    }

    public Boolean getRedo() {
        return mIndex;
    }

    public String getItemName() {
        return mItemName;
    }
}
