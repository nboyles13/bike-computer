package btools.router;

/* JADX INFO: loaded from: classes.dex */
final class KinematicPath extends OsmPath {
    private double ekin;
    private float floatingAngleLeft;
    private float floatingAngleRight;
    private double totalEnergy;
    private double totalTime;

    KinematicPath() {
    }

    @Override // btools.router.OsmPath
    protected void init(OsmPath orig) {
        KinematicPath origin = (KinematicPath) orig;
        this.ekin = origin.ekin;
        this.totalTime = origin.totalTime;
        this.totalEnergy = origin.totalEnergy;
        this.floatingAngleLeft = origin.floatingAngleLeft;
        this.floatingAngleRight = origin.floatingAngleRight;
    }

    @Override // btools.router.OsmPath
    protected void resetState() {
        this.ekin = 0.0d;
        this.totalTime = 0.0d;
        this.totalEnergy = 0.0d;
        this.floatingAngleLeft = 0.0f;
        this.floatingAngleRight = 0.0f;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0162  */
    @Override // btools.router.OsmPath
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected double processWaySection(RoutingContext rc, double dist, double delta_h, double elevation, double angle, double cosangle, boolean isStartpoint, int nsection, int lastpriorityclassifier) {
        double turnspeed;
        double cost;
        double turnspeed2;
        double extraTime;
        double extraTime2;
        double curveSpeed;
        KinematicModel km = (KinematicModel) rc.pm;
        double extraTime3 = 0.0d;
        if (isStartpoint) {
            if (!rc.inverseDirection) {
                extraTime = (1.0d - cosangle) * 0.5d * 40.0d;
                cost = 0.0d;
            } else {
                extraTime = 0.0d;
                cost = 0.0d;
            }
        } else {
            if (km.turnAngleDecayTime != 0.0d) {
                if (angle < 0.0d) {
                    this.floatingAngleLeft -= (float) angle;
                } else {
                    this.floatingAngleRight += (float) angle;
                }
                float aa = Math.max(this.floatingAngleLeft, this.floatingAngleRight);
                if (aa > 10.0d) {
                    turnspeed = 999.0d;
                    curveSpeed = 200.0d / ((double) aa);
                } else {
                    turnspeed = 999.0d;
                    curveSpeed = 20.0d;
                }
                double distanceTime = dist / curveSpeed;
                cost = 0.0d;
                double cost2 = -distanceTime;
                double decayFactor = Math.exp(cost2 / km.turnAngleDecayTime);
                this.floatingAngleLeft = (float) (((double) this.floatingAngleLeft) * decayFactor);
                this.floatingAngleRight = (float) (((double) this.floatingAngleRight) * decayFactor);
                if (curveSpeed < 20.0d) {
                    turnspeed2 = curveSpeed;
                }
                if (nsection != 0) {
                    double junctionspeed = 999.0d;
                    int classifiermask = (int) rc.expctxWay.getClassifierMask();
                    boolean hasRightWay = false;
                    boolean hasResidential = false;
                    boolean hasLeftWay = false;
                    OsmPrePath prePath = rc.firstPrePath;
                    while (prePath != null) {
                        KinematicPrePath pp = (KinematicPrePath) prePath;
                        double turnspeed3 = turnspeed2;
                        if (((pp.classifiermask ^ classifiermask) & 8) == 0) {
                            if ((pp.classifiermask & 32) != 0) {
                                hasResidential = true;
                            }
                            if (pp.priorityclassifier > this.priorityclassifier || (pp.priorityclassifier == this.priorityclassifier && this.priorityclassifier < 20)) {
                                double diff = pp.angle - angle;
                                if (diff < -40.0d && diff > -140.0d) {
                                    hasLeftWay = true;
                                }
                                if (diff > 40.0d && diff < 140.0d) {
                                    hasRightWay = true;
                                }
                            }
                        }
                        prePath = prePath.next;
                        turnspeed2 = turnspeed3;
                    }
                    double turnspeed4 = turnspeed2;
                    if (hasLeftWay && 999.0d > km.leftWaySpeed) {
                        junctionspeed = km.leftWaySpeed;
                    }
                    if (hasRightWay && junctionspeed > km.rightWaySpeed) {
                        junctionspeed = km.rightWaySpeed;
                    }
                    if (hasResidential && junctionspeed > 13.0d) {
                        junctionspeed = 13.0d;
                    }
                    if ((this.priorityclassifier < 20) ^ (lastpriorityclassifier < 20)) {
                        extraTime3 = 0.0d + 10.0d;
                        junctionspeed = 0.0d;
                    }
                    if (lastpriorityclassifier != this.priorityclassifier && (classifiermask & 8) != 0) {
                        extraTime3 += 2.0d;
                    }
                    double turnspeed5 = turnspeed4 > junctionspeed ? junctionspeed : turnspeed4;
                    if (this.message == null) {
                        extraTime2 = extraTime3;
                    } else {
                        extraTime2 = extraTime3;
                        this.message.vnode0 = (int) ((junctionspeed * 3.6d) + 0.5d);
                    }
                    turnspeed2 = turnspeed5;
                    extraTime3 = extraTime2;
                }
                cutEkin(km.totalweight, turnspeed2);
                extraTime = extraTime3;
            } else {
                turnspeed = 999.0d;
                cost = 0.0d;
            }
            turnspeed2 = turnspeed;
            if (nsection != 0) {
            }
            cutEkin(km.totalweight, turnspeed2);
            extraTime = extraTime3;
        }
        double tcorr = (20.0d - km.outside_temp) * 0.0035d;
        double ecorr = (elevation - 100.0d) * 1.375E-4d;
        double f_air = km.f_air * ((tcorr + 1.0d) - ecorr);
        double distanceCost = evolveDistance(km, dist, delta_h, f_air);
        float cf = rc.expctxWay.getCostfactor();
        if (this.message != null) {
            this.message.costfactor = (float) (distanceCost / dist);
            this.message.vmax = (int) ((((double) km.getWayMaxspeed()) * 3.6d) + 0.5d);
            this.message.vmaxExplicit = (int) ((((double) km.getWayMaxspeedExplicit()) * 3.6d) + 0.5d);
            this.message.vmin = (int) ((((double) km.getWayMinspeed()) * 3.6d) + 0.5d);
            this.message.extraTime = (int) (1000.0d * extraTime);
        }
        double cost3 = cost + (((double) cf) * dist) + 0.5d + ((km.pw * extraTime) / km.cost0);
        this.totalTime += extraTime;
        return cost3 + distanceCost;
    }

    protected double evolveDistance(KinematicModel km, double dist, double delta_h, double f_air) {
        double elow;
        double elow2;
        double x;
        double timeStep;
        double b;
        double fh = ((km.totalweight * delta_h) * 9.81d) / dist;
        double eaux = km.getEffectiveSpeedLimit();
        double emax = km.totalweight * 0.5d * eaux * eaux;
        if (emax <= 0.0d) {
            return -1.0d;
        }
        double vb = km.getBreakingSpeed(eaux);
        double elow3 = km.totalweight * 0.5d * vb * vb;
        double elapsedTime = 0.0d;
        double dissipatedEnergy = 0.0d;
        double v = Math.sqrt((this.ekin * 2.0d) / km.totalweight);
        double d = dist;
        while (d > 0.0d) {
            double effectiveSpeedLimit = eaux;
            double effectiveSpeedLimit2 = this.ekin;
            boolean slow = effectiveSpeedLimit2 < elow3;
            double vb2 = vb;
            boolean fast = this.ekin >= emax;
            double etarget = slow ? elow3 : emax;
            double emax2 = emax;
            double f = km.f_roll + (f_air * v * v) + fh;
            if (fast) {
                elow = elow3;
                elow2 = -f;
            } else {
                elow = elow3;
                elow2 = (slow ? km.f_recup : 0.0d) - fh;
            }
            double fh2 = fh;
            double f_recup = Math.max(0.0d, elow2);
            double f2 = f + f_recup;
            if (fast) {
                double x2 = d;
                b = x2 * f2;
                double timeStep2 = x2 / v;
                this.ekin = etarget;
                x = x2;
                timeStep = timeStep2;
            } else {
                double delta_ekin = etarget - this.ekin;
                double b2 = (f_air * 2.0d) / km.totalweight;
                double x0 = delta_ekin / f2;
                double x0b = x0 * b2;
                x = (1.0d - ((((0.333333333d - (0.25d * x0b)) * x0b) + 0.5d) * x0b)) * x0;
                double delta_ekin2 = delta_ekin;
                double maxstep = Math.min(50.0d, d);
                if (x < maxstep) {
                    this.ekin = etarget;
                } else {
                    x = maxstep;
                    double xb = x * b2;
                    double delta_ekin3 = x * f2 * ((((((0.0416666667d * xb) + 0.166666667d) * xb) + 0.5d) * xb) + 1.0d);
                    this.ekin += delta_ekin3;
                    delta_ekin2 = delta_ekin3;
                }
                double v2 = Math.sqrt((this.ekin * 2.0d) / km.totalweight);
                double a = f2 / km.totalweight;
                timeStep = (v2 - v) / a;
                v = v2;
                b = delta_ekin2;
            }
            d -= x;
            elapsedTime += timeStep;
            double dissipatedEnergy2 = dissipatedEnergy + (b - ((fh2 + (km.recup_efficiency * f_recup)) * x));
            double ieRecup = x * f_recup * (1.0d - km.recup_efficiency);
            dissipatedEnergy = dissipatedEnergy2 - (Math.max(ieRecup, km.p_standby * timeStep) * 0.5d);
            eaux = effectiveSpeedLimit;
            vb = vb2;
            emax = emax2;
            elow3 = elow;
            fh = fh2;
        }
        double fh3 = fh;
        double fh4 = km.p_standby;
        double dissipatedEnergy3 = dissipatedEnergy + (fh4 * elapsedTime);
        this.totalTime += elapsedTime;
        this.totalEnergy += dissipatedEnergy3 + (dist * fh3);
        return ((km.pw * elapsedTime) + dissipatedEnergy3) / km.cost0;
    }

    @Override // btools.router.OsmPath
    protected double processTargetNode(RoutingContext rc) {
        KinematicModel km = (KinematicModel) rc.pm;
        if (this.targetNode.nodeDescription != null) {
            rc.expctxNode.evaluate(false, this.targetNode.nodeDescription);
            float initialcost = rc.expctxNode.getInitialcost();
            if (initialcost >= 1000000.0d) {
                return -1.0d;
            }
            cutEkin(km.totalweight, km.getNodeMaxspeed());
            if (this.message != null) {
                this.message.linknodecost += (int) initialcost;
                this.message.nodeKeyValues = rc.expctxNode.getKeyValueDescription(false, this.targetNode.nodeDescription);
                this.message.vnode1 = (int) ((((double) km.getNodeMaxspeed()) * 3.6d) + 0.5d);
            }
            return initialcost;
        }
        return 0.0d;
    }

    private void cutEkin(double weight, double speed) {
        double e = 0.5d * weight * speed * speed;
        if (this.ekin > e) {
            this.ekin = e;
        }
    }

    @Override // btools.router.OsmPath
    public int elevationCorrection() {
        return 0;
    }

    @Override // btools.router.OsmPath
    public boolean definitlyWorseThan(OsmPath path) {
        KinematicPath p = (KinematicPath) path;
        int c = p.cost;
        return this.cost > c + 100;
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
