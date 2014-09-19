package com.leexplorer.app.util.ble.firs;

import com.leexplorer.app.models.IBeacon;

public interface BleFir {
  void addAdvertisement(IBeacon beacon);

  Double getDistance();
}
