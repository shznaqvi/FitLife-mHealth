package edu.aku.hassannaqvi.fitlife.core.location;

import android.location.Location;

public interface LocationObserver {
    void onLocationChanged(Location location);
}
