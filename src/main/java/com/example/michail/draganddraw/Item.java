package com.example.michail.draganddraw;

public class Item {
    private int mItemText;
    private int mIconId;

    public int getItemText() {
        return mItemText;
    }

    public void setItemText(int name) {
        this.mItemText = name;
    }

    public int getIonId() {
        return mIconId;
    }

    public void setIconId(int photoResId) {
        this.mIconId = photoResId;
    }
}
