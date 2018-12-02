package com.whiteroads.library.interfaces;

public interface DataEventListener {

    public void onAccelChange(String value);
    public void onLinAccelChange(String value);
    public void onSpeedChange(String value);
    public void onLatLonChange(String value);
    public void onGyroChange(String value);
    public void onStepsChange(String value);
    public void onProxiChange(String value);
    public void onRotationChange(String value);
    public void onOrientChange(String value);
    public void onLightChange(String value);
    public void onScoreChange(int value);
    public void onAddressChange(String value);


}
