package btools.router;

import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
final class StdPath extends OsmPath {
    private static final double GRAVITY = 9.81d;
    private int downhillcostdiv;
    private int ehbd;
    private int ehbu;
    private float elevation_buffer;
    private float totalEnergy;
    private float totalTime;
    private int uphillcostdiv;

    StdPath() {
    }

    @Override // btools.router.OsmPath
    public void init(OsmPath orig) {
        StdPath origin = (StdPath) orig;
        this.ehbd = origin.ehbd;
        this.ehbu = origin.ehbu;
        this.totalTime = origin.totalTime;
        this.totalEnergy = origin.totalEnergy;
        this.elevation_buffer = origin.elevation_buffer;
    }

    @Override // btools.router.OsmPath
    protected void resetState() {
        this.ehbd = 0;
        this.ehbu = 0;
        this.totalTime = 0.0f;
        this.totalEnergy = 0.0f;
        this.uphillcostdiv = 0;
        this.downhillcostdiv = 0;
        this.elevation_buffer = 0.0f;
    }

    @Override // btools.router.OsmPath
    protected double processWaySection(RoutingContext rc, double distance, double delta_h, double elevation, double angle, double cosangle, boolean isStartpoint, int nsection, int lastpriorityclassifier) {
        int downhillmaxslopecostdiv;
        int uphillmaxslopecostdiv;
        float uphillcutoff;
        float cfup;
        float uphillmaxslope;
        float downhillmaxslope;
        float cf;
        int uphillmaxslopecostdiv2;
        int dist;
        int oldPrio;
        float downhillcutoff;
        float upweight;
        float turncostbase = rc.expctxWay.getTurncost();
        float uphillcutoff2 = rc.expctxWay.getUphillcutoff() * 10000.0f;
        float downhillcutoff2 = rc.expctxWay.getDownhillcutoff() * 10000.0f;
        float uphillmaxslope2 = rc.expctxWay.getUphillmaxslope() * 10000.0f;
        float downhillmaxslope2 = rc.expctxWay.getDownhillmaxslope() * 10000.0f;
        float cfup2 = rc.expctxWay.getUphillCostfactor();
        float cfdown = rc.expctxWay.getDownhillCostfactor();
        float cf2 = rc.expctxWay.getCostfactor();
        float cfup3 = cfup2 == 0.0f ? cf2 : cfup2;
        float cfdown2 = cfdown == 0.0f ? cf2 : cfdown;
        this.downhillcostdiv = (int) rc.expctxWay.getDownhillcost();
        if (this.downhillcostdiv > 0) {
            this.downhillcostdiv = DurationKt.NANOS_IN_MILLIS / this.downhillcostdiv;
        }
        int downhillmaxslopecostdiv2 = (int) rc.expctxWay.getDownhillmaxslopecost();
        if (downhillmaxslopecostdiv2 > 0) {
            downhillmaxslopecostdiv = DurationKt.NANOS_IN_MILLIS / downhillmaxslopecostdiv2;
        } else {
            downhillmaxslopecostdiv = this.downhillcostdiv;
        }
        this.uphillcostdiv = (int) rc.expctxWay.getUphillcost();
        if (this.uphillcostdiv > 0) {
            this.uphillcostdiv = DurationKt.NANOS_IN_MILLIS / this.uphillcostdiv;
        }
        int uphillmaxslopecostdiv3 = (int) rc.expctxWay.getUphillmaxslopecost();
        if (uphillmaxslopecostdiv3 > 0) {
            uphillmaxslopecostdiv = DurationKt.NANOS_IN_MILLIS / uphillmaxslopecostdiv3;
        } else {
            uphillmaxslopecostdiv = this.uphillcostdiv;
        }
        int downhillmaxslopecostdiv3 = downhillmaxslopecostdiv;
        int dist2 = (int) distance;
        int turncost = (int) (((1.0d - cosangle) * ((double) turncostbase)) + 0.2d);
        int newPrio = (int) rc.expctxWay.getPriorityClassifier();
        if (!rc.bikeMode) {
            uphillcutoff = uphillcutoff2;
            cfup = cfup3;
            uphillmaxslope = uphillmaxslope2;
            downhillmaxslope = downhillmaxslope2;
            cf = cf2;
            uphillmaxslopecostdiv2 = uphillmaxslopecostdiv;
            dist = dist2;
            oldPrio = lastpriorityclassifier;
            downhillcutoff = downhillcutoff2;
        } else {
            cf = cf2;
            cfup = cfup3;
            uphillmaxslopecostdiv2 = uphillmaxslopecostdiv;
            uphillmaxslope = uphillmaxslope2;
            downhillmaxslope = downhillmaxslope2;
            uphillcutoff = uphillcutoff2;
            if (rc.consider_crossing) {
                oldPrio = lastpriorityclassifier;
                if (oldPrio <= 0 || nsection != 0 || angle >= 0.0d) {
                    downhillcutoff = downhillcutoff2;
                    dist = dist2;
                } else {
                    downhillcutoff = downhillcutoff2;
                    if (oldPrio >= rc.crossing_Prio_H && newPrio <= rc.crossing_Prio_L && this.sourceNode.nodeDescription != null) {
                        boolean nodeAccessGranted = ((double) rc.expctxWay.getNodeAccessGranted()) != 0.0d;
                        String node_tags = rc.expctxNode.getKeyValueDescription(nodeAccessGranted, this.sourceNode.nodeDescription);
                        int class_index = node_tags.indexOf("estimated_crossing_class=");
                        if (class_index <= -1) {
                            dist = dist2;
                        } else {
                            dist = dist2;
                            String crossing_class = node_tags.substring(class_index + 25, class_index + 26);
                            int additional_turn_cost = crossing_class.equals("1") ? rc.cost_ToLeft_from_H_class1 : 0;
                            if (crossing_class.equals("2")) {
                                additional_turn_cost = rc.cost_ToLeft_from_H_class2;
                            }
                            if (crossing_class.equals("3")) {
                                additional_turn_cost = rc.cost_ToLeft_from_H_class3;
                            }
                            if (crossing_class.equals("4")) {
                                additional_turn_cost = rc.cost_ToLeft_from_H_class4;
                            }
                            if (crossing_class.equals("5")) {
                                additional_turn_cost = rc.cost_ToLeft_from_H_class5;
                            }
                            if (crossing_class.equals("6")) {
                                additional_turn_cost = rc.cost_ToLeft_from_H_class6;
                            }
                            turncost += additional_turn_cost;
                        }
                    } else {
                        dist = dist2;
                    }
                }
            } else {
                dist = dist2;
                oldPrio = lastpriorityclassifier;
                downhillcutoff = downhillcutoff2;
            }
            if (rc.consider_crossing && oldPrio > 0 && nsection == 0 && angle > 0.0d && oldPrio >= rc.crossing_Prio_H && newPrio <= rc.crossing_Prio_L && this.sourceNode.nodeDescription != null) {
                boolean nodeAccessGranted2 = ((double) rc.expctxWay.getNodeAccessGranted()) != 0.0d;
                String node_tags2 = rc.expctxNode.getKeyValueDescription(nodeAccessGranted2, this.sourceNode.nodeDescription);
                int class_index2 = node_tags2.indexOf("estimated_crossing_class=");
                if (class_index2 > -1) {
                    String crossing_class2 = node_tags2.substring(class_index2 + 25, class_index2 + 26);
                    int additional_turn_cost2 = crossing_class2.equals("1") ? rc.cost_ToRight_from_H_class1 : 0;
                    if (crossing_class2.equals("2")) {
                        additional_turn_cost2 = rc.cost_ToRight_from_H_class2;
                    }
                    if (crossing_class2.equals("3")) {
                        additional_turn_cost2 = rc.cost_ToRight_from_H_class3;
                    }
                    if (crossing_class2.equals("4")) {
                        additional_turn_cost2 = rc.cost_ToRight_from_H_class4;
                    }
                    if (crossing_class2.equals("5")) {
                        additional_turn_cost2 = rc.cost_ToRight_from_H_class5;
                    }
                    if (crossing_class2.equals("6")) {
                        additional_turn_cost2 = rc.cost_ToRight_from_H_class6;
                    }
                    turncost += additional_turn_cost2;
                }
            }
        }
        if (this.message != null) {
            this.message.linkturncost += turncost;
            this.message.turnangle = (float) angle;
        }
        double sectionCost = turncost;
        int delta_h_micros = (int) (1000000.0d * delta_h);
        int dist3 = dist;
        this.ehbd = (int) (this.ehbd + ((-delta_h_micros) - (dist3 * downhillcutoff)));
        this.ehbu = (int) (this.ehbu + (delta_h_micros - (dist3 * uphillcutoff)));
        float downweight = 0.0f;
        if (this.ehbd <= rc.elevationpenaltybuffer) {
            if (this.ehbd < 0) {
                this.ehbd = 0;
            }
        } else {
            downweight = 1.0f;
            int excess = this.ehbd - rc.elevationpenaltybuffer;
            int reduce = dist3 * rc.elevationbufferreduce;
            if (reduce > excess) {
                float downweight2 = excess / reduce;
                reduce = excess;
                downweight = downweight2;
            }
            int excess2 = this.ehbd - rc.elevationmaxbuffer;
            if (reduce < excess2) {
                reduce = excess2;
            }
            this.ehbd -= reduce;
            float elevationCost = 0.0f;
            if (this.downhillcostdiv > 0) {
                elevationCost = 0.0f + (Math.min(reduce, dist3 * downhillmaxslope) / this.downhillcostdiv);
            }
            if (downhillmaxslopecostdiv3 > 0) {
                elevationCost += Math.max(0.0f, reduce - (dist3 * downhillmaxslope)) / downhillmaxslopecostdiv3;
            }
            if (elevationCost > 0.0f) {
                sectionCost += (double) elevationCost;
                if (this.message != null) {
                    this.message.linkelevationcost = (int) (r5.linkelevationcost + elevationCost);
                }
            }
        }
        float upweight2 = 0.0f;
        if (this.ehbu <= rc.elevationpenaltybuffer) {
            if (this.ehbu < 0) {
                this.ehbu = 0;
            }
        } else {
            float upweight3 = 1.0f;
            int excess3 = this.ehbu - rc.elevationpenaltybuffer;
            int reduce2 = dist3 * rc.elevationbufferreduce;
            if (reduce2 > excess3) {
                float upweight4 = excess3 / reduce2;
                reduce2 = excess3;
                upweight3 = upweight4;
            }
            int excess4 = this.ehbu - rc.elevationmaxbuffer;
            if (reduce2 < excess4) {
                reduce2 = excess4;
            }
            this.ehbu -= reduce2;
            float elevationCost2 = this.uphillcostdiv > 0 ? 0.0f + (Math.min(reduce2, dist3 * uphillmaxslope) / this.uphillcostdiv) : 0.0f;
            if (uphillmaxslopecostdiv2 > 0) {
                elevationCost2 += Math.max(0.0f, reduce2 - (dist3 * uphillmaxslope)) / uphillmaxslopecostdiv2;
            }
            if (elevationCost2 <= 0.0f) {
                upweight = upweight3;
            } else {
                upweight = upweight3;
                sectionCost += (double) elevationCost2;
                if (this.message != null) {
                    this.message.linkelevationcost = (int) (r4.linkelevationcost + elevationCost2);
                }
            }
            upweight2 = upweight;
        }
        float costfactor = (cfup * upweight2) + (((1.0f - upweight2) - downweight) * cf) + (cfdown2 * downweight);
        if (this.message != null) {
            this.message.costfactor = costfactor;
        }
        return sectionCost + ((double) ((dist3 * costfactor) + 0.5f));
    }

    @Override // btools.router.OsmPath
    protected double processTargetNode(RoutingContext rc) {
        if (this.targetNode.nodeDescription == null) {
            return 0.0d;
        }
        boolean nodeAccessGranted = ((double) rc.expctxWay.getNodeAccessGranted()) != 0.0d;
        rc.expctxNode.evaluate(nodeAccessGranted, this.targetNode.nodeDescription);
        float initialcost = rc.expctxNode.getInitialcost();
        if (initialcost >= 1000000.0d) {
            return -1.0d;
        }
        if (this.message != null) {
            this.message.linknodecost += (int) initialcost;
            this.message.nodeKeyValues = rc.expctxNode.getKeyValueDescription(nodeAccessGranted, this.targetNode.nodeDescription);
        }
        return initialcost;
    }

    @Override // btools.router.OsmPath
    public int elevationCorrection() {
        return (this.downhillcostdiv > 0 ? this.ehbd / this.downhillcostdiv : 0) + (this.uphillcostdiv > 0 ? this.ehbu / this.uphillcostdiv : 0);
    }

    @Override // btools.router.OsmPath
    public boolean definitlyWorseThan(OsmPath path) {
        StdPath p = (StdPath) path;
        int c = p.cost;
        if (p.downhillcostdiv > 0) {
            int delta = (p.ehbd / p.downhillcostdiv) - (this.downhillcostdiv > 0 ? this.ehbd / this.downhillcostdiv : 0);
            if (delta > 0) {
                c += delta;
            }
        }
        int delta2 = p.uphillcostdiv;
        if (delta2 > 0) {
            int delta3 = (p.ehbu / p.uphillcostdiv) - (this.uphillcostdiv > 0 ? this.ehbu / this.uphillcostdiv : 0);
            if (delta3 > 0) {
                c += delta3;
            }
        }
        int delta4 = this.cost;
        return delta4 > c;
    }

    private double calcIncline(double dist) {
        double shift = 0.0d;
        if (this.elevation_buffer > 3.0d) {
            shift = -3.0d;
        } else if (this.elevation_buffer < (-3.0d)) {
            shift = 3.0d;
        }
        double decayFactor = Math.exp((-dist) / 100.0d);
        float new_elevation_buffer = (float) (((((double) this.elevation_buffer) + shift) * decayFactor) - shift);
        double incline = ((double) (this.elevation_buffer - new_elevation_buffer)) / dist;
        this.elevation_buffer = new_elevation_buffer;
        return incline;
    }

    @Override // btools.router.OsmPath
    protected void computeKinematic(RoutingContext rc, double dist, double delta_h, boolean detailMode) {
        if (detailMode) {
            this.elevation_buffer = (float) (((double) this.elevation_buffer) + delta_h);
            double incline = calcIncline(dist);
            double maxSpeed = rc.maxSpeed;
            double speedLimit = rc.expctxWay.getMaxspeed() / 3.6f;
            if (speedLimit > 0.0d) {
                maxSpeed = Math.min(maxSpeed, speedLimit);
            }
            double speed = maxSpeed;
            double f_roll = rc.totalMass * GRAVITY * (rc.defaultC_r + incline);
            if (rc.footMode) {
                speed = rc.maxSpeed * Math.exp(Math.abs(incline + 0.05d) * (-3.5d));
            } else if (rc.bikeMode) {
                double speed2 = solveCubic(rc.S_C_x, f_roll, rc.bikerPower);
                speed = Math.min(speed2, maxSpeed);
            }
            float dt = (float) (dist / speed);
            this.totalTime += dt;
            double energy = ((rc.S_C_x * speed * speed) + f_roll) * dist;
            if (energy > 0.0d) {
                this.totalEnergy = (float) (((double) this.totalEnergy) + energy);
            }
        }
    }

    private static double solveCubic(double a, double c, double d) {
        double v = 8.0d;
        boolean findingStartvalue = true;
        for (int i = 0; i < 10; i++) {
            double y = ((((a * v) * v) + c) * v) - d;
            if (y < 0.1d) {
                if (!findingStartvalue) {
                    break;
                }
                v *= 2.0d;
            } else {
                findingStartvalue = false;
                double y_prime = (3.0d * a * v * v) + c;
                v -= y / y_prime;
            }
        }
        return v;
    }

    @Override // btools.router.OsmPath
    public double getTotalTime() {
        return this.totalTime;
    }

    @Override // btools.router.OsmPath
    public double getTotalEnergy() {
        return this.totalEnergy;
    }
}
