package btools.router;

import androidx.core.view.PointerIconCompat;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VoiceHint {
    static final int BL = 16;
    static final int C = 1;
    static final int EL = 17;
    static final int END = 100;
    static final int ER = 18;
    static final int KL = 8;
    static final int KR = 9;
    static final int OFFR = 12;
    static final int RNDB = 13;
    static final int RNLB = 14;
    static final int TL = 2;
    static final int TLU = 10;
    static final int TR = 5;
    static final int TRU = 11;
    static final int TSHL = 4;
    static final int TSHR = 7;
    static final int TSLL = 3;
    static final int TSLR = 6;
    static final int TU = 15;
    List<MessageData> badWays;
    int cmd;
    double distanceToNext;
    MessageData goodWay;
    int ilat;
    int ilon;
    int indexInTrack;
    boolean needsRealTurn;
    MessageData oldWay;
    int roundaboutExit;
    short selev;
    boolean turnAngleConsumed;
    float angle = Float.MAX_VALUE;
    float lowerBadWayAngle = -181.0f;
    float higherBadWayAngle = 181.0f;
    int maxBadPrio = -1;

    public float getTime() {
        if (this.oldWay == null) {
            return 0.0f;
        }
        return this.oldWay.time;
    }

    boolean isRoundabout() {
        return this.roundaboutExit != 0;
    }

    public void addBadWay(MessageData badWay) {
        if (badWay == null) {
            return;
        }
        if (this.badWays == null) {
            this.badWays = new ArrayList();
        }
        this.badWays.add(badWay);
    }

    public int getJsonCommandIndex(int timode) {
        switch (this.cmd) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return (timode == 2 || timode == 9) ? 17 : 8;
            case 18:
                return (timode == 2 || timode == 9) ? 18 : 9;
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
        }
    }

    public int getExitNumber() {
        return this.roundaboutExit;
    }

    public String getCommandString(int timode) {
        switch (this.cmd) {
            case 1:
                return "C";
            case 2:
                return "TL";
            case 3:
                return "TSLL";
            case 4:
                return "TSHL";
            case 5:
                return "TR";
            case 6:
                return "TSLR";
            case 7:
                return "TSHR";
            case 8:
                return "KL";
            case 9:
                return "KR";
            case 10:
                return "TU";
            case 11:
                return "TRU";
            case 12:
                return "OFFR";
            case 13:
                return "RNDB" + this.roundaboutExit;
            case 14:
                return "RNLB" + (-this.roundaboutExit);
            case 15:
                return "TU";
            case 16:
                return "BL";
            case 17:
                return (timode == 2 || timode == 9) ? "EL" : "KL";
            case 18:
                return (timode == 2 || timode == 9) ? "ER" : "KR";
            case 100:
                return "END";
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
        }
    }

    public String getCommandString(int c, int timode) {
        switch (c) {
            case 1:
                return "C";
            case 2:
                return "TL";
            case 3:
                return "TSLL";
            case 4:
                return "TSHL";
            case 5:
                return "TR";
            case 6:
                return "TSLR";
            case 7:
                return "TSHR";
            case 8:
                return "KL";
            case 9:
                return "KR";
            case 10:
                return "TLU";
            case 11:
                return "TRU";
            case 12:
                return "OFFR";
            case 13:
                return "RNDB" + this.roundaboutExit;
            case 14:
                return "RNLB" + (-this.roundaboutExit);
            case 15:
                return "TU";
            case 16:
                return "BL";
            case 17:
                return (timode == 2 || timode == 9) ? "EL" : "KL";
            case 18:
                return (timode == 2 || timode == 9) ? "ER" : "KR";
            default:
                return "unknown command: " + c;
        }
    }

    public String getSymbolString(int timode) {
        switch (this.cmd) {
            case 1:
                return "Straight";
            case 2:
                return "Left";
            case 3:
                return "TSLL";
            case 4:
                return "TSHL";
            case 5:
                return "Right";
            case 6:
                return "TSLR";
            case 7:
                return "TSHR";
            case 8:
                return "TSLL";
            case 9:
                return "TSLR";
            case 10:
                return "TU";
            case 11:
                return "TU";
            case 12:
                return "OFFR";
            case 13:
                return "RNDB" + this.roundaboutExit;
            case 14:
                return "RNLB" + (-this.roundaboutExit);
            case 15:
                return "TU";
            case 16:
                return "BL";
            case 17:
                return (timode == 2 || timode == 9) ? "EL" : "KL";
            case 18:
                return (timode == 2 || timode == 9) ? "ER" : "KR";
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
        }
    }

    public String getLocusSymbolString() {
        switch (this.cmd) {
            case 1:
                return "straight";
            case 2:
                return "left";
            case 3:
                return "left_slight";
            case 4:
                return "left_sharp";
            case 5:
                return "right";
            case 6:
                return "right_slight";
            case 7:
                return "right_sharp";
            case 8:
                return "stay_left";
            case 9:
                return "stay_right";
            case 10:
                return "u-turn_left";
            case 11:
                return "u-turn_right";
            case 12:
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
            case 13:
                return "roundabout_e" + this.roundaboutExit;
            case 14:
                return "roundabout_e" + (-this.roundaboutExit);
            case 15:
                return "u-turn";
            case 16:
                return "beeline";
            case 17:
                return "exit_left";
            case 18:
                return "exit_right";
        }
    }

    public String getMessageString(int timode) {
        switch (this.cmd) {
            case 1:
                return "straight";
            case 2:
                return "left";
            case 3:
                return "slight left";
            case 4:
                return "sharp left";
            case 5:
                return "right";
            case 6:
                return "slight right";
            case 7:
                return "sharp right";
            case 8:
                return "keep left";
            case 9:
                return "keep right";
            case 10:
                return "u-turn";
            case 11:
                return "u-turn";
            case 12:
            case 16:
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
            case 13:
                return "Take exit " + this.roundaboutExit;
            case 14:
                return "Take exit " + (-this.roundaboutExit);
            case 15:
                return "u-turn";
            case 17:
                return (timode == 2 || timode == 9) ? "exit left" : "keep left";
            case 18:
                return (timode == 2 || timode == 9) ? "exit right" : "keep right";
        }
    }

    public int getLocusAction() {
        switch (this.cmd) {
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 5;
            case 5:
                return 7;
            case 6:
                return 6;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 10;
            case 10:
                return 13;
            case 11:
                return 14;
            case 12:
            case 16:
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
            case 13:
                return this.roundaboutExit + 26;
            case 14:
                return 26 - this.roundaboutExit;
            case 15:
                return 12;
            case 17:
                return 9;
            case 18:
                return 10;
        }
    }

    public int getOruxAction() {
        switch (this.cmd) {
            case 1:
                return PointerIconCompat.TYPE_HAND;
            case 2:
                return 1000;
            case 3:
                return PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW;
            case 4:
                return PointerIconCompat.TYPE_ZOOM_OUT;
            case 5:
                return 1001;
            case 6:
                return PointerIconCompat.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW;
            case 7:
                return PointerIconCompat.TYPE_ZOOM_IN;
            case 8:
                return PointerIconCompat.TYPE_VERTICAL_DOUBLE_ARROW;
            case 9:
                return PointerIconCompat.TYPE_HORIZONTAL_DOUBLE_ARROW;
            case 10:
                return PointerIconCompat.TYPE_HELP;
            case 11:
                return PointerIconCompat.TYPE_HELP;
            case 12:
            case 16:
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
            case 13:
                return this.roundaboutExit + PointerIconCompat.TYPE_TEXT;
            case 14:
                return this.roundaboutExit + PointerIconCompat.TYPE_TEXT;
            case 15:
                return PointerIconCompat.TYPE_HELP;
            case 17:
                return PointerIconCompat.TYPE_VERTICAL_DOUBLE_ARROW;
            case 18:
                return PointerIconCompat.TYPE_HORIZONTAL_DOUBLE_ARROW;
        }
    }

    public String getCruiserCommandString() {
        switch (this.cmd) {
            case 1:
                return "C";
            case 2:
                return "TL";
            case 3:
                return "TSLL";
            case 4:
                return "TSHL";
            case 5:
                return "TR";
            case 6:
                return "TSLR";
            case 7:
                return "TSHR";
            case 8:
                return "KL";
            case 9:
                return "KR";
            case 10:
                return "TLU";
            case 11:
                return "TRU";
            case 12:
                return "OFFR";
            case 13:
                return "RNDB" + this.roundaboutExit;
            case 14:
                return "RNLB" + (-this.roundaboutExit);
            case 15:
                return "TU";
            case 16:
                return "BL";
            case 17:
                return "EL";
            case 18:
                return "ER";
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
        }
    }

    public String getCruiserMessageString() {
        switch (this.cmd) {
            case 1:
                return "straight";
            case 2:
                return "left";
            case 3:
                return "slight left";
            case 4:
                return "sharp left";
            case 5:
                return "right";
            case 6:
                return "slight right";
            case 7:
                return "sharp right";
            case 8:
                return "keep left";
            case 9:
                return "keep right";
            case 10:
                return "u-turn left";
            case 11:
                return "u-turn right";
            case 12:
                return "offroad";
            case 13:
                return "take exit " + this.roundaboutExit;
            case 14:
                return "take exit " + (-this.roundaboutExit);
            case 15:
                return "u-turn";
            case 16:
                return "beeline";
            case 17:
                return "exit left";
            case 18:
                return "exit right";
            default:
                throw new IllegalArgumentException("unknown command: " + this.cmd);
        }
    }

    public void calcCommand() {
        if (this.badWays != null) {
            for (MessageData badWay : this.badWays) {
                if (!badWay.isBadOneway()) {
                    if (this.lowerBadWayAngle < badWay.turnangle && badWay.turnangle < this.goodWay.turnangle) {
                        this.lowerBadWayAngle = badWay.turnangle;
                    }
                    if (this.higherBadWayAngle > badWay.turnangle && badWay.turnangle > this.goodWay.turnangle) {
                        this.higherBadWayAngle = badWay.turnangle;
                    }
                }
            }
        }
        float cmdAngle = this.angle;
        if (this.angle == Float.MAX_VALUE) {
            cmdAngle = this.goodWay.turnangle;
        }
        if (this.cmd == 16) {
            return;
        }
        if (this.roundaboutExit > 0) {
            this.cmd = 13;
            return;
        }
        if (this.roundaboutExit < 0) {
            this.cmd = 14;
            return;
        }
        if (is180DegAngle(cmdAngle) && cmdAngle <= -179.0f && this.higherBadWayAngle == 181.0f && this.lowerBadWayAngle == -181.0f) {
            this.cmd = 15;
            return;
        }
        if (cmdAngle < -159.0f) {
            this.cmd = 10;
            return;
        }
        if (cmdAngle < -135.0f) {
            this.cmd = 4;
            return;
        }
        if (cmdAngle < -45.0f) {
            if (cmdAngle < -95.0f && this.higherBadWayAngle < -30.0f && this.lowerBadWayAngle < -180.0f) {
                this.cmd = 4;
                return;
            }
            if (cmdAngle > -85.0f && this.lowerBadWayAngle > -180.0f && this.higherBadWayAngle > -10.0f) {
                this.cmd = 3;
                return;
            }
            if (cmdAngle < -110.0f) {
                this.cmd = 4;
                return;
            } else if (cmdAngle > -60.0f) {
                this.cmd = 3;
                return;
            } else {
                this.cmd = 2;
                return;
            }
        }
        if (cmdAngle < -21.0f) {
            if (this.cmd != 9) {
                this.cmd = 3;
                return;
            }
            return;
        }
        if (cmdAngle < -5.0f) {
            if (this.lowerBadWayAngle < -100.0f && this.higherBadWayAngle < 45.0f) {
                this.cmd = 3;
                return;
            }
            if (this.lowerBadWayAngle >= -100.0f && this.higherBadWayAngle < 45.0f) {
                this.cmd = 8;
                return;
            } else if (this.lowerBadWayAngle > -35.0f && this.higherBadWayAngle > 55.0f) {
                this.cmd = 9;
                return;
            } else {
                this.cmd = 1;
                return;
            }
        }
        if (cmdAngle < 5.0f) {
            if (this.lowerBadWayAngle > -30.0f) {
                this.cmd = 9;
                return;
            } else if (this.higherBadWayAngle < 30.0f) {
                this.cmd = 8;
                return;
            } else {
                this.cmd = 1;
                return;
            }
        }
        if (cmdAngle < 21.0f) {
            if (this.lowerBadWayAngle > -45.0f && this.higherBadWayAngle > 100.0f) {
                this.cmd = 6;
                return;
            }
            if (this.lowerBadWayAngle > -45.0f && this.higherBadWayAngle <= 100.0f) {
                this.cmd = 9;
                return;
            } else if (this.lowerBadWayAngle < -55.0f && this.higherBadWayAngle < 35.0f) {
                this.cmd = 8;
                return;
            } else {
                this.cmd = 1;
                return;
            }
        }
        if (cmdAngle < 45.0f) {
            this.cmd = 6;
            return;
        }
        if (cmdAngle >= 135.0f) {
            if (cmdAngle < 159.0f) {
                this.cmd = 7;
                return;
            }
            if (is180DegAngle(cmdAngle) && cmdAngle >= 179.0f && this.higherBadWayAngle == 181.0f && this.lowerBadWayAngle == -181.0f) {
                this.cmd = 15;
                return;
            } else {
                this.cmd = 11;
                return;
            }
        }
        if (cmdAngle < 85.0f && this.higherBadWayAngle < 180.0f && this.lowerBadWayAngle < 10.0f) {
            this.cmd = 6;
            return;
        }
        if (cmdAngle > 95.0f && this.lowerBadWayAngle > 30.0f && this.higherBadWayAngle > 180.0f) {
            this.cmd = 7;
            return;
        }
        if (cmdAngle > 110.0d) {
            this.cmd = 7;
        } else if (cmdAngle < 60.0d) {
            this.cmd = 6;
        } else {
            this.cmd = 5;
        }
    }

    static boolean is180DegAngle(float angle) {
        return Math.abs(angle) <= 180.0f && Math.abs(angle) >= 179.0f;
    }

    public String formatGeometry() {
        float oldPrio = this.oldWay == null ? 0.0f : this.oldWay.priorityclassifier;
        StringBuilder sb = new StringBuilder(30);
        sb.append(' ').append((int) oldPrio);
        appendTurnGeometry(sb, this.goodWay);
        if (this.badWays != null) {
            for (MessageData badWay : this.badWays) {
                sb.append(" ");
                appendTurnGeometry(sb, badWay);
            }
        }
        return sb.toString();
    }

    private void appendTurnGeometry(StringBuilder sb, MessageData msg) {
        sb.append("(").append((int) (((double) msg.turnangle) + 0.5d)).append(")").append(msg.priorityclassifier);
    }

    public boolean hasGiveWay() {
        if (this.oldWay == null || this.oldWay.nodeKeyValues == null) {
            return false;
        }
        return this.oldWay.wayKeyValues.contains("reversedirection=yes") ? (this.oldWay.nodeKeyValues.contains("highway=give_way") || this.oldWay.nodeKeyValues.contains("highway=stop")) && this.oldWay.nodeKeyValues.contains("direction=backward") : (this.oldWay.nodeKeyValues.contains("highway=give_way") || this.oldWay.nodeKeyValues.contains("highway=stop")) && !this.oldWay.nodeKeyValues.contains("direction=backward");
    }
}
