package com.example.michail.draganddraw;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import top.defaults.colorpicker.ColorPickerPopup;

public class NavigationFragment extends Fragment {
    public static final String TAG = "NavigationFragment";
    public static final String PREF_UNDO = "PrefUndo";
    public static final String PREF_SAVE = "PrefSave";
    public static final String PREF_COLOR = "PrefColor";
    public static final String PREF_SIZE = "PrefSize";
    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private NavigationFragmentListner mNavigationFragmentListner;
    RecyclerView mRecyclerView;
    NavigationAdapter mNavigationAdapter;


    public static NavigationFragment newInstance(String key, String message) {
        Bundle args = new Bundle();
        args.putString(key, message);
        NavigationFragment fragment = new NavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.rvRecyclerView);
        final ArrayList<Item> items = getItems();
        final ArrayList<Item> shapeItems = getShapeItems();
        final ArrayList<Item> strokeItems = getStrokeItems();
        onClickItem(items, shapeItems, strokeItems);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(horizontalLayoutManager);

        return view;
    }

    private void onClickItem(final ArrayList<Item> items, final ArrayList<Item> shapeItems, final ArrayList<Item> strokeItems) {
        mNavigationAdapter = new NavigationAdapter(items);

        mRecyclerView.setAdapter(mNavigationAdapter);
        mNavigationAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (items.get(position).getItemText()) {
                    case R.string.item_shape:
                        shapeItems.get(4).setItemText(R.string.item_back);
                        items.get(0).setItemText(0);
                        mNavigationAdapter.notifyItemRangeRemoved(0, mNavigationAdapter.getItemCount());
                        mNavigationAdapter = new NavigationAdapter(shapeItems);
                        mNavigationAdapter.notifyItemRangeInserted(0, shapeItems.size());
                        mRecyclerView.setAdapter(mNavigationAdapter);
                        onClickShape(items, shapeItems, strokeItems);
                        break;
                    case R.string.item_undo:
                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_undo));
                            mNavigationFragmentListner.setUndo(true);
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PREF_UNDO, true).apply();
                            Log.i(TAG, PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PREF_UNDO, false) + " index from navigation");
                        }
                        break;
                    case R.string.item_color:
                        new ColorPickerPopup.Builder(getContext())
                                .initialColor(Color.RED) // Set initial color
                                .enableBrightness(true) // Enable brightness slider or not
                                .enableAlpha(true) // Enable alpha slider or not
                                .okTitle("Choose")
                                .cancelTitle("Cancel")
                                .showIndicator(true)
                                .showValue(true)
                                .build()
                                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                                    @Override
                                    public void onColorPicked(int color) {
                                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(PREF_COLOR, color).apply();
                                    }
                                });
                        break;
                    case R.string.item_stroke:

                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_stroke));


                        }
                        strokeItems.get(1).setItemText(R.string.item_back);
                        items.get(1).setItemText(0);
                        mNavigationAdapter.notifyItemRangeRemoved(0, mNavigationAdapter.getItemCount());
                        mNavigationAdapter = new NavigationAdapter(strokeItems);
                        mNavigationAdapter.notifyItemRangeInserted(0, strokeItems.size());
                        mRecyclerView.setAdapter(mNavigationAdapter);
                        onClickStroke(items, shapeItems, strokeItems);
                        break;
                    case R.string.item_clear:
                        if (mNavigationFragmentListner != null) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.item_clear);
                            builder.setMessage(R.string.dialog_clear);
                            builder.setIcon(R.drawable.ic_clear);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNavigationFragmentListner.setItem(getString(R.string.item_clear));
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                        break;
                    case R.string.item_save:
                        if (mNavigationFragmentListner != null) {
                            int permissionStatus = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

                                mNavigationFragmentListner.setItem(getString(R.string.item_save));
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PREF_SAVE, true).apply();

                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);

                                mNavigationFragmentListner.setItem(getString(R.string.item_save));
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PREF_SAVE, true).apply();

                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    mNavigationFragmentListner.setItem(getString(R.string.item_save));
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PREF_SAVE, true).apply();
                } else {
                    // permission denied
                    Toast permissionToast = Toast.makeText(getContext(),
                            R.string.toast_negative_save, Toast.LENGTH_LONG);
                    permissionToast.show();
                }
                return;
        }
    }

    private void onClickShape(final ArrayList<Item> items, final ArrayList<Item> shapeItems, final ArrayList<Item> strokeItems) {
        mNavigationAdapter = new NavigationAdapter(shapeItems);
        mRecyclerView.setAdapter(mNavigationAdapter);
        mNavigationAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                switch (shapeItems.get(position).getItemText()) {
                    case R.string.item_back:
                        shapeItems.get(4).setItemText(0);
                        items.get(0).setItemText(R.string.item_shape);
                        mNavigationAdapter.notifyItemRangeRemoved(0, mNavigationAdapter.getItemCount());
                        mNavigationAdapter = new NavigationAdapter(items);
                        mNavigationAdapter.notifyItemRangeInserted(0, items.size());
                        mRecyclerView.setAdapter(mNavigationAdapter);
                        onClickItem(items, shapeItems, strokeItems);
                        break;
                    case R.string.item_square:
                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_square));
                        }

                        break;
                    case R.string.item_circle:
                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_circle));
                        }
                        break;
                    case R.string.item_star:
                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_star));
                        }
                        break;
                    case R.string.item_triangle:
                        if (mNavigationFragmentListner != null) {
                            mNavigationFragmentListner.setItem(getString(R.string.item_triangle));
                        }
                        break;
                }
            }
        });
    }

    private void onClickStroke(final ArrayList<Item> items, final ArrayList<Item> shapeItems, final ArrayList<Item> strokeItems) {
        mNavigationAdapter = new NavigationAdapter(strokeItems);
        mRecyclerView.setAdapter(mNavigationAdapter);
        mNavigationAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                switch (strokeItems.get(position).getItemText()) {
                    case R.string.item_back:
                        strokeItems.get(1).setItemText(0);
                        items.get(1).setItemText(R.string.item_stroke);
                        mNavigationAdapter.notifyItemRangeRemoved(0, mNavigationAdapter.getItemCount());
                        mNavigationAdapter = new NavigationAdapter(items);
                        mNavigationAdapter.notifyItemRangeInserted(0, items.size());
                        mRecyclerView.setAdapter(mNavigationAdapter);
                        onClickItem(items, shapeItems, strokeItems);
                        break;
                    case R.string.item_size:
                        final AlertDialog.Builder sizeDialog = new AlertDialog.Builder(getContext());
                        sizeDialog.setTitle(R.string.dialog_size_title);
                        View linearLayout = getLayoutInflater().inflate(R.layout.stroke_size_dialog, null);
                        sizeDialog.setView(linearLayout);
                        final TextView textSize = linearLayout.findViewById(R.id.textView_size);

                        final SeekBar seekBar = linearLayout.findViewById(R.id.seekBar);
                        seekBar.setProgress((int)PreferenceManager.getDefaultSharedPreferences(getContext()).getFloat(PREF_SIZE,20f));
                        textSize.setText(String.valueOf(seekBar.getProgress()));
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                textSize.setText(String.valueOf(seekBar.getProgress()));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                textSize.setText(String.valueOf(seekBar.getProgress()));
                            }
                        });
                        sizeDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putFloat(PREF_SIZE,seekBar.getProgress()).apply();
                                dialog.dismiss();
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        sizeDialog.create();
                        sizeDialog.show();
                        break;
                }
            }
        });
    }

    public interface NavigationFragmentListner {
        void setItem(String itemName);

        void setUndo(Boolean index);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationFragmentListner) {
            mNavigationFragmentListner = (NavigationFragmentListner) context;
        }
    }

    public ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        Item Shape = new Item();
        Shape.setItemText(R.string.item_shape);
        Shape.setIconId(R.drawable.ic_shape);
        items.add(Shape);
        Item Stroke = new Item();
        Stroke.setItemText(R.string.item_stroke);
        Stroke.setIconId(R.drawable.ic_action_name);
        items.add(Stroke);
        Item Color = new Item();
        Color.setItemText(R.string.item_color);
        Color.setIconId(R.drawable.ic_color);
        items.add(Color);
        Item Undo = new Item();
        Undo.setItemText(R.string.item_undo);
        Undo.setIconId(R.drawable.ic_undo);
        items.add(Undo);
        Item Save = new Item();
        Save.setItemText(R.string.item_save);
        Save.setIconId(R.drawable.ic_save);
        items.add(Save);
        Item Clear = new Item();
        Clear.setItemText(R.string.item_clear);
        Clear.setIconId(R.drawable.ic_clear);
        items.add(Clear);
        return items;
    }

    public ArrayList<Item> getShapeItems() {
        ArrayList<Item> shapeItems = new ArrayList<>();
        Item square = new Item();
        square.setItemText(R.string.item_square);
        square.setIconId(R.drawable.ic_square);
        shapeItems.add(square);
        Item Circle = new Item();
        Circle.setItemText(R.string.item_circle);
        Circle.setIconId(R.drawable.ic_circle);
        shapeItems.add(Circle);
        Item Star = new Item();
        Star.setItemText(R.string.item_star);
        Star.setIconId(R.drawable.ic_shape);
        shapeItems.add(Star);
        Item Triangle = new Item();
        Triangle.setItemText(R.string.item_triangle);
        Triangle.setIconId(R.drawable.ic_triangle);
        shapeItems.add(Triangle);
        Item Back = new Item();
        Back.setItemText(R.string.item_back);
        Back.setIconId(R.drawable.ic_back);
        shapeItems.add(Back);
        return shapeItems;
    }

    public ArrayList<Item> getStrokeItems() {
        ArrayList<Item> strokeItems = new ArrayList<>();
        Item size = new Item();
        size.setItemText(R.string.item_size);
        size.setIconId(R.drawable.ic_stroke_size);
        strokeItems.add(size);
        Item back = new Item();
        back.setIconId(R.drawable.ic_back);
        back.setItemText(R.string.item_back);
        strokeItems.add(back);
        return strokeItems;
    }

}


