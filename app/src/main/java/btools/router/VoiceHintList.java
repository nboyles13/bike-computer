package btools.router;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VoiceHintList {
    static final int TRANS_MODE_BIKE = 2;
    static final int TRANS_MODE_CAR = 3;
    static final int TRANS_MODE_FOOT = 1;
    static final int TRANS_MODE_NONE = 0;
    int turnInstructionMode;
    private int transportMode = 2;
    List<VoiceHint> list = new ArrayList();

    public void setTransportMode(boolean isCar, boolean isBike) {
        this.transportMode = isCar ? 3 : isBike ? 2 : 1;
    }

    public void setTransportMode(int mode) {
        this.transportMode = mode;
    }

    public String getTransportMode() {
        switch (this.transportMode) {
            case 1:
                return "foot";
            case 2:
            default:
                return "bike";
            case 3:
                return "car";
        }
    }

    public int transportMode() {
        return this.transportMode;
    }

    public int getLocusRouteType() {
        if (this.transportMode == 3) {
            return 0;
        }
        return this.transportMode == 2 ? 5 : 3;
    }
}
