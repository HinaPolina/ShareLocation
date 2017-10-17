package hinapolina.com.sharelocation.ui;


/**
 * Created by hinaikhan on 10/16/17.
 */

public class BatteryStatus {
    private boolean charging;
    private boolean acCharge;
    private boolean usbCharge;

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public boolean isAcCharge() {
        return acCharge;
    }

    public void setAcCharge(boolean acCharge) {
        this.acCharge = acCharge;
    }

    public boolean isUsbCharge() {
        return usbCharge;
    }

    public void setUsbCharge(boolean usbCharge) {
        this.usbCharge = usbCharge;
    }
}
