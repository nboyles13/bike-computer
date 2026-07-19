package btools.router;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class VoiceHintProcessor {
    private boolean explicitRoundabouts;
    private int transportMode;
    double SIGNIFICANT_ANGLE = 22.5d;
    double INTERNAL_CATCHING_RANGE_NEAR = 2.0d;
    double INTERNAL_CATCHING_RANGE_WIDE = 10.0d;

    public VoiceHintProcessor(double catchingRange, boolean explicitRoundabouts, int transportMode) {
        this.explicitRoundabouts = explicitRoundabouts;
        this.transportMode = transportMode;
    }

    private float sumNonConsumedWithinCatchingRange(List<VoiceHint> inputs, int offset, double range) {
        double distance = 0.0d;
        float angle = 0.0f;
        while (offset >= 0 && distance < range) {
            int offset2 = offset - 1;
            VoiceHint input = inputs.get(offset);
            if (input.turnAngleConsumed || input.cmd == 16 || input.cmd == 100) {
                break;
            }
            angle += input.goodWay.turnangle;
            distance += (double) input.goodWay.linkdist;
            input.turnAngleConsumed = true;
            offset = offset2;
        }
        return angle;
    }

    public List<VoiceHint> process(List<VoiceHint> inputs) {
        List<VoiceHint> results;
        double distance;
        List<VoiceHint> results2;
        Iterator<MessageData> it;
        int currentPrio;
        int roundaboudStartIdx;
        int roundaboudStartIdx2;
        List<VoiceHint> results3 = new ArrayList<>();
        double distance2 = 0.0d;
        float roundAboutTurnAngle = 0.0f;
        int roundaboutExit = 0;
        int roundaboudStartIdx3 = -1;
        int hintIdx = 0;
        while (hintIdx < inputs.size()) {
            VoiceHint input = inputs.get(hintIdx);
            if (input.cmd == 16) {
                results3.add(input);
                results2 = results3;
            } else {
                float turnAngle = input.goodWay.turnangle;
                if (hintIdx != 0) {
                    distance2 += (double) input.goodWay.linkdist;
                }
                int currentPrio2 = input.goodWay.getPrio();
                int oldPrio = input.oldWay.getPrio();
                int minPrio = Math.min(oldPrio, currentPrio2);
                boolean isLink2Highway = input.oldWay.isLinktType() && !input.goodWay.isLinktType();
                boolean isHighway2Link = !input.oldWay.isLinktType() && input.goodWay.isLinktType();
                if (!this.explicitRoundabouts || !input.oldWay.isRoundabout()) {
                    boolean isHighway2Link2 = isHighway2Link;
                    int currentPrio3 = currentPrio2;
                    if (roundaboutExit > 0) {
                        input.angle = roundAboutTurnAngle;
                        input.goodWay.turnangle = roundAboutTurnAngle;
                        input.distanceToNext = distance2;
                        input.turnAngleConsumed = true;
                        input.roundaboutExit = roundAboutTurnAngle < 0.0f ? roundaboutExit : -roundaboutExit;
                        float tmpangle = 0.0f;
                        VoiceHint tmpRndAbt = new VoiceHint();
                        tmpRndAbt.badWays = new ArrayList();
                        int i = hintIdx - 1;
                        while (i > roundaboudStartIdx3) {
                            int roundaboutExit2 = roundaboutExit;
                            VoiceHint vh = inputs.get(i);
                            int roundaboudStartIdx4 = roundaboudStartIdx3;
                            tmpangle += inputs.get(i).goodWay.turnangle;
                            if (vh.badWays != null) {
                                Iterator<MessageData> it2 = vh.badWays.iterator();
                                while (it2.hasNext()) {
                                    if (it2.next().isBadOneway()) {
                                        it = it2;
                                        currentPrio = currentPrio3;
                                    } else {
                                        MessageData md = new MessageData();
                                        it = it2;
                                        currentPrio = currentPrio3;
                                        md.linkdist = vh.goodWay.linkdist;
                                        md.priorityclassifier = vh.goodWay.priorityclassifier;
                                        md.turnangle = tmpangle;
                                        tmpRndAbt.badWays.add(md);
                                    }
                                    it2 = it;
                                    currentPrio3 = currentPrio;
                                }
                            }
                            i--;
                            roundaboutExit = roundaboutExit2;
                            roundaboudStartIdx3 = roundaboudStartIdx4;
                            currentPrio3 = currentPrio3;
                        }
                        distance2 = 0.0d;
                        input.badWays = tmpRndAbt.badWays;
                        results3.add(input);
                        roundAboutTurnAngle = 0.0f;
                        roundaboutExit = 0;
                        roundaboudStartIdx3 = -1;
                        results2 = results3;
                    } else {
                        float roundAboutTurnAngle2 = roundAboutTurnAngle;
                        int roundaboutExit3 = roundaboutExit;
                        int roundaboudStartIdx5 = roundaboudStartIdx3;
                        VoiceHint inputNext = hintIdx + 1 < inputs.size() ? inputs.get(hintIdx + 1) : null;
                        int maxPrioAll = -1;
                        int maxPrioCandidates = -1;
                        float maxAngle = -180.0f;
                        float minAngle = 180.0f;
                        float minAbsAngeRaw = 180.0f;
                        boolean isBadwayLink = false;
                        if (input.badWays == null) {
                            results = results3;
                            distance = distance2;
                        } else {
                            Iterator<MessageData> it3 = input.badWays.iterator();
                            while (it3.hasNext()) {
                                Iterator<MessageData> it4 = it3;
                                MessageData badWay = it3.next();
                                List<VoiceHint> results4 = results3;
                                int badPrio = badWay.getPrio();
                                double distance3 = distance2;
                                float badTurn = badWay.turnangle;
                                if (badWay.isLinktType()) {
                                    isBadwayLink = true;
                                }
                                boolean isBadHighway2Link = !input.oldWay.isLinktType() && badWay.isLinktType();
                                if (badPrio > maxPrioAll) {
                                    maxPrioAll = badPrio;
                                    input.maxBadPrio = Math.max(input.maxBadPrio, badPrio);
                                }
                                boolean isBadHighway2Link2 = badWay.isBadOneway();
                                if (isBadHighway2Link2) {
                                    if (minAbsAngeRaw != 180.0f) {
                                        results3 = results4;
                                        it3 = it4;
                                        distance2 = distance3;
                                    } else {
                                        minAbsAngeRaw = Math.abs(turnAngle);
                                        results3 = results4;
                                        it3 = it4;
                                        distance2 = distance3;
                                    }
                                } else if (Math.abs(badTurn) - Math.abs(turnAngle) <= 80.0f) {
                                    if (badWay.costfactor < 20.0f && Math.abs(badTurn) < minAbsAngeRaw) {
                                        minAbsAngeRaw = Math.abs(badTurn);
                                    }
                                    if (badPrio > maxPrioCandidates) {
                                        maxPrioCandidates = badPrio;
                                        input.maxBadPrio = Math.max(input.maxBadPrio, badPrio);
                                    }
                                    if (badTurn > maxAngle) {
                                        maxAngle = badTurn;
                                    }
                                    if (badTurn < minAngle) {
                                        minAngle = badTurn;
                                    }
                                    results3 = results4;
                                    it3 = it4;
                                    distance2 = distance3;
                                } else if (minAbsAngeRaw != 180.0f) {
                                    results3 = results4;
                                    it3 = it4;
                                    distance2 = distance3;
                                } else {
                                    minAbsAngeRaw = Math.abs(turnAngle);
                                    results3 = results4;
                                    it3 = it4;
                                    distance2 = distance3;
                                }
                            }
                            results = results3;
                            distance = distance2;
                        }
                        boolean hasSomethingMoreStraight = Math.abs(turnAngle) > 35.0f && input.badWays != null;
                        boolean noLinkButBadWayPrio = maxPrioAll > minPrio && !isLink2Highway;
                        boolean badWayHasPrio = maxPrioCandidates > currentPrio3;
                        boolean isUTurn = VoiceHint.is180DegAngle(turnAngle);
                        boolean isBadWayLinkButNoLink = !isHighway2Link2 && isBadwayLink && Math.abs(turnAngle) > 5.0f;
                        boolean isLinkButNoBadWayLink = isHighway2Link2 && !isBadwayLink && Math.abs(turnAngle) < 5.0f;
                        int oldPrio2 = (currentPrio3 != oldPrio || minPrio - maxPrioAll > 2 || isBadwayLink || minAbsAngeRaw == 180.0f || minAbsAngeRaw >= 35.0f) ? 0 : 1;
                        boolean mustGiveWay = (this.transportMode == 1 || input.badWays == null || badWayHasPrio || (!input.hasGiveWay() && (inputNext == null || !inputNext.hasGiveWay()))) ? false : true;
                        boolean unconditionalTrigger = hasSomethingMoreStraight || noLinkButBadWayPrio || badWayHasPrio || isUTurn || isBadWayLinkButNoLink || isLinkButNoBadWayLink || oldPrio2 != 0 || mustGiveWay;
                        boolean conditionalTrigger = maxPrioCandidates >= minPrio;
                        if (unconditionalTrigger || conditionalTrigger) {
                            input.angle = turnAngle;
                            input.calcCommand();
                            boolean isStraight = input.cmd == 1;
                            input.needsRealTurn = !unconditionalTrigger && isStraight;
                            if (Math.abs(turnAngle) > 5.0d) {
                                if (maxAngle < turnAngle && maxAngle > (turnAngle - 45.0f) - Math.max(turnAngle, 0.0f)) {
                                    input.cmd = 9;
                                }
                                if (minAngle > turnAngle && minAngle < (turnAngle + 45.0f) - Math.min(turnAngle, 0.0f)) {
                                    input.cmd = 8;
                                }
                            }
                            if (this.explicitRoundabouts) {
                                input.angle = sumNonConsumedWithinCatchingRange(inputs, hintIdx, this.INTERNAL_CATCHING_RANGE_WIDE);
                            } else {
                                input.turnAngleConsumed = true;
                            }
                            input.distanceToNext = distance;
                            results2 = results;
                            results2.add(input);
                            distance = 0.0d;
                        } else {
                            results2 = results;
                        }
                        if (results2.size() > 0 && distance < this.INTERNAL_CATCHING_RANGE_NEAR) {
                            results2.get(results2.size() - 1).angle += sumNonConsumedWithinCatchingRange(inputs, hintIdx, this.INTERNAL_CATCHING_RANGE_NEAR);
                        }
                        roundAboutTurnAngle = roundAboutTurnAngle2;
                        roundaboutExit = roundaboutExit3;
                        roundaboudStartIdx3 = roundaboudStartIdx5;
                        distance2 = distance;
                    }
                } else {
                    if (roundaboudStartIdx3 == -1) {
                        roundaboudStartIdx3 = hintIdx;
                    }
                    roundAboutTurnAngle += sumNonConsumedWithinCatchingRange(inputs, hintIdx, this.INTERNAL_CATCHING_RANGE_NEAR);
                    if (roundaboudStartIdx3 != hintIdx || input.badWays == null) {
                        roundaboudStartIdx = roundaboudStartIdx3;
                    } else {
                        roundAboutTurnAngle -= input.goodWay.turnangle;
                        for (MessageData badWay2 : input.badWays) {
                            if (badWay2.isBadOneway()) {
                                roundaboudStartIdx2 = roundaboudStartIdx3;
                            } else {
                                roundaboudStartIdx2 = roundaboudStartIdx3;
                                roundAboutTurnAngle += badWay2.turnangle;
                            }
                            roundaboudStartIdx3 = roundaboudStartIdx2;
                        }
                        roundaboudStartIdx = roundaboudStartIdx3;
                    }
                    boolean isExit = roundaboutExit == 0;
                    if (input.badWays != null) {
                        Iterator<MessageData> it5 = input.badWays.iterator();
                        while (true) {
                            if (!it5.hasNext()) {
                                break;
                            }
                            MessageData badWay3 = it5.next();
                            if (!badWay3.isBadOneway() && badWay3.isGoodForCars()) {
                                isExit = true;
                                break;
                            }
                        }
                    }
                    if (!isExit) {
                        results2 = results3;
                        roundaboudStartIdx3 = roundaboudStartIdx;
                    } else {
                        roundaboutExit++;
                        results2 = results3;
                        roundaboudStartIdx3 = roundaboudStartIdx;
                    }
                }
            }
            hintIdx++;
            results3 = results2;
        }
        List<VoiceHint> results5 = results3;
        List<VoiceHint> results22 = new ArrayList<>();
        int i2 = results5.size();
        while (i2 > 0) {
            i2--;
            VoiceHint hint = results5.get(i2);
            if (hint.cmd == 0) {
                hint.calcCommand();
            }
            if (hint.cmd == 100) {
                results22.add(hint);
            } else {
                if (hint.needsRealTurn && (hint.cmd == 1 || hint.cmd == 16)) {
                    if (hint.cmd == 16) {
                        results22.add(hint);
                    } else if (results22.size() > 0) {
                        results22.get(results22.size() - 1).distanceToNext += hint.distanceToNext;
                    }
                }
                double dist = hint.distanceToNext;
                while (true) {
                    if (dist >= this.INTERNAL_CATCHING_RANGE_NEAR || i2 <= 0) {
                        break;
                    }
                    VoiceHint h2 = results5.get(i2 - 1);
                    dist = h2.distanceToNext;
                    hint.distanceToNext += dist;
                    hint.angle += h2.angle;
                    i2--;
                    if (h2.isRoundabout()) {
                        h2.angle = hint.angle;
                        hint = h2;
                        break;
                    }
                }
                if (!this.explicitRoundabouts) {
                    hint.roundaboutExit = 0;
                }
                hint.calcCommand();
                results22.add(hint);
            }
        }
        return results22;
    }

    /* JADX WARN: Removed duplicated region for block: B:160:0x0277  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public List<VoiceHint> postProcess(List<VoiceHint> inputs, double catchingRange, double minRange) {
        VoiceHint nextInput;
        List<VoiceHint> results = new ArrayList<>();
        VoiceHint inputLast = null;
        VoiceHint inputLastSaved = null;
        int hintIdx = 0;
        while (hintIdx < inputs.size()) {
            VoiceHint input = inputs.get(hintIdx);
            VoiceHint nextInput2 = null;
            if (hintIdx + 1 < inputs.size()) {
                VoiceHint nextInput3 = inputs.get(hintIdx + 1);
                nextInput2 = nextInput3;
            }
            if (input.cmd == 16) {
                results.add(input);
            } else {
                if (nextInput2 == null) {
                    if (input.cmd != 100) {
                        if ((input.cmd != 1 && input.cmd != 9 && input.cmd != 8) || input.goodWay.isLinktType() || checkStraightHold(input, inputLastSaved, minRange)) {
                            results.add(input);
                        } else if (inputLast != null) {
                            inputLast.distanceToNext += input.distanceToNext;
                        }
                    }
                } else {
                    if ((inputLastSaved == null || inputLastSaved.distanceToNext <= catchingRange) && input.distanceToNext <= catchingRange) {
                        if (input.distanceToNext < catchingRange) {
                            double dist = input.distanceToNext;
                            float angles = input.angle;
                            boolean save = false;
                            double d = dist + nextInput2.distanceToNext;
                            float angles2 = angles + nextInput2.angle;
                            if ((input.cmd == 1 || input.cmd == 9 || input.cmd == 8) && !input.goodWay.isLinktType()) {
                                if (input.goodWay.getPrio() < input.maxBadPrio) {
                                    if (inputLastSaved != null && inputLastSaved.cmd != 1 && inputLastSaved != null && inputLastSaved.distanceToNext > minRange && this.transportMode != 3) {
                                        save = true;
                                        if (nextInput2 != null && nextInput2.cmd == 1 && !nextInput2.goodWay.isLinktType()) {
                                            double d2 = input.distanceToNext;
                                            double dist2 = nextInput2.distanceToNext;
                                            input.distanceToNext = d2 + dist2;
                                            hintIdx++;
                                        }
                                    }
                                } else if (inputLastSaved != null) {
                                    inputLastSaved.distanceToNext += input.distanceToNext;
                                }
                                if (save) {
                                    results.add(input);
                                    inputLastSaved = input;
                                }
                            } else {
                                if (input.goodWay.getPrio() == 29 && input.maxBadPrio == 30) {
                                    if (input.cmd == 9 || input.cmd == 6) {
                                        input.cmd = 18;
                                    } else if (input.cmd == 8 || input.cmd == 3) {
                                        input.cmd = 17;
                                    }
                                    save = true;
                                } else if (VoiceHint.is180DegAngle(input.angle)) {
                                    save = true;
                                } else {
                                    if (this.transportMode == 3) {
                                        VoiceHint nextInput4 = nextInput2;
                                        if (Math.abs(angles2) <= 180.0d - this.SIGNIFICANT_ANGLE) {
                                            nextInput = nextInput4;
                                        } else {
                                            input.angle = angles2;
                                            input.calcCommand();
                                            input.distanceToNext += nextInput4.distanceToNext;
                                            save = true;
                                            hintIdx++;
                                        }
                                    } else {
                                        nextInput = nextInput2;
                                    }
                                    if (Math.abs(angles2) < this.SIGNIFICANT_ANGLE && input.distanceToNext < minRange) {
                                        input.angle = angles2;
                                        input.calcCommand();
                                        input.distanceToNext += nextInput.distanceToNext;
                                        save = true;
                                        hintIdx++;
                                    } else if (Math.abs(input.angle) > this.SIGNIFICANT_ANGLE) {
                                        save = true;
                                    } else if (Math.abs(input.angle) >= this.SIGNIFICANT_ANGLE) {
                                        nextInput.distanceToNext += input.distanceToNext;
                                        save = false;
                                    }
                                }
                                if (save) {
                                }
                            }
                        } else {
                            results.add(input);
                            inputLastSaved = input;
                        }
                    } else if (input.cmd == 1 || input.cmd == 9 || input.cmd == 8) {
                        if (checkStraightHold(input, inputLastSaved, minRange)) {
                            results.add(input);
                            inputLastSaved = input;
                        } else if (inputLastSaved != null) {
                            inputLastSaved.distanceToNext += input.distanceToNext;
                        }
                    } else if (input.goodWay.getPrio() == 29 && input.maxBadPrio == 30 && checkForNextNoneMotorway(inputs, hintIdx, 3)) {
                        if (input.cmd != 9 && input.cmd != 6) {
                            if (input.cmd == 8 || input.cmd == 3) {
                                input.cmd = 17;
                            }
                        } else {
                            input.cmd = 18;
                        }
                        results.add(input);
                        inputLastSaved = input;
                    } else if ((input.goodWay.getPrio() != 28 && input.goodWay.getPrio() != 30 && input.goodWay.getPrio() != 26) || input.isRoundabout() || Math.abs(input.angle) > 21.0f || Math.abs(input.angle) - input.lowerBadWayAngle < 21.0f) {
                        results.add(input);
                        inputLastSaved = input;
                    } else if (inputLastSaved != null) {
                        inputLastSaved.distanceToNext += input.distanceToNext;
                    }
                    inputLast = input;
                }
                inputLast = input;
            }
            hintIdx++;
        }
        int hintIdx2 = results.size();
        if (hintIdx2 > 0 && results.get(results.size() - 1).cmd == 100) {
            results.remove(results.size() - 1);
        }
        return results;
    }

    boolean checkForNextNoneMotorway(List<VoiceHint> inputs, int offset, int testsize) {
        for (int i = 1; i < testsize + 1 && offset + i < inputs.size(); i++) {
            int prio = inputs.get(offset + i).goodWay.getPrio();
            if (prio < 29) {
                return true;
            }
            if (prio == 30) {
                return false;
            }
        }
        return false;
    }

    boolean checkStraightHold(VoiceHint input, VoiceHint inputLastSaved, double minRange) {
        if (input.indexInTrack == 0) {
            return false;
        }
        boolean badOneWay = false;
        if (input.badWays != null) {
            for (MessageData md : input.badWays) {
                if (md.isBadOneway()) {
                    badOneWay = true;
                }
            }
        }
        if (badOneWay && input.lowerBadWayAngle == -181.0f && input.higherBadWayAngle == 181.0f) {
            return false;
        }
        if ((input.lowerBadWayAngle != -181.0f && Math.abs(input.lowerBadWayAngle) > 135.0f && Math.abs(input.higherBadWayAngle) > 35.0f) || (input.higherBadWayAngle != 181.0f && input.higherBadWayAngle > 135.0f && Math.abs(input.lowerBadWayAngle) > 35.0f)) {
            return false;
        }
        if (Math.abs(input.lowerBadWayAngle) < 35.0f || input.higherBadWayAngle < 35.0f || input.goodWay.getPrio() < input.maxBadPrio || input.goodWay.getPrio() > input.oldWay.getPrio()) {
            return (inputLastSaved == null || inputLastSaved.distanceToNext > minRange) && input.distanceToNext > minRange;
        }
        return false;
    }
}
