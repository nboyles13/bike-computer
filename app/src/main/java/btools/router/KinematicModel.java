package btools.router;

import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class KinematicModel extends OsmPathModel {
    public double cost0;
    protected BExpressionContextNode ctxNode;
    protected BExpressionContextWay ctxWay;
    public double f_air;
    public double f_recup;
    public double f_roll;
    private boolean initDone = false;
    private double lastBreakingSpeed;
    private double lastEffectiveLimit;
    public double leftWaySpeed;
    private int nodeIdxMaxspeed;
    public double outside_temp;
    public double p_standby;
    protected Map<String, String> params;
    public double pw;
    public double recup_efficiency;
    public double rightWaySpeed;
    public double totalweight;
    public double turnAngleDecayTime;
    public double vmax;
    private int wayIdxMaxspeed;
    private int wayIdxMaxspeedExplicit;
    private int wayIdxMinspeed;

    KinematicModel() {
    }

    @Override // btools.router.OsmPathModel
    public OsmPrePath createPrePath() {
        return new KinematicPrePath();
    }

    @Override // btools.router.OsmPathModel
    public OsmPath createPath() {
        return new KinematicPath();
    }

    @Override // btools.router.OsmPathModel
    public void init(BExpressionContextWay expctxWay, BExpressionContextNode expctxNode, Map<String, String> extraParams) {
        if (!this.initDone) {
            this.ctxWay = expctxWay;
            this.ctxNode = expctxNode;
            this.wayIdxMaxspeed = this.ctxWay.getOutputVariableIndex("maxspeed", false);
            this.wayIdxMaxspeedExplicit = this.ctxWay.getOutputVariableIndex("maxspeed_explicit", false);
            this.wayIdxMinspeed = this.ctxWay.getOutputVariableIndex("minspeed", false);
            this.nodeIdxMaxspeed = this.ctxNode.getOutputVariableIndex("maxspeed", false);
            this.initDone = true;
        }
        this.params = extraParams;
        this.turnAngleDecayTime = getParam("turnAngleDecayTime", 5.0f);
        this.f_roll = getParam("f_roll", 232.0f);
        this.f_air = getParam("f_air", 0.4f);
        this.f_recup = getParam("f_recup", 400.0f);
        this.p_standby = getParam("p_standby", 250.0f);
        this.outside_temp = getParam("outside_temp", 20.0f);
        this.recup_efficiency = getParam("recup_efficiency", 0.7f);
        this.totalweight = getParam("totalweight", 1640.0f);
        this.vmax = ((double) getParam("vmax", 80.0f)) / 3.6d;
        this.leftWaySpeed = ((double) getParam("leftWaySpeed", 12.0f)) / 3.6d;
        this.rightWaySpeed = ((double) getParam("rightWaySpeed", 12.0f)) / 3.6d;
        this.pw = ((((this.f_air * 2.0d) * this.vmax) * this.vmax) * this.vmax) - this.p_standby;
        this.cost0 = ((this.pw + this.p_standby) / this.vmax) + this.f_roll + (this.f_air * this.vmax * this.vmax);
    }

    protected float getParam(String name, float defaultValue) {
        String sval = this.params == null ? null : this.params.get(name);
        if (sval != null) {
            return Float.parseFloat(sval);
        }
        float v = this.ctxWay.getVariableValue(name, defaultValue);
        if (this.params != null) {
            this.params.put(name, new StringBuilder().append(v).toString());
        }
        return v;
    }

    public float getWayMaxspeed() {
        return this.ctxWay.getBuildInVariable(this.wayIdxMaxspeed) / 3.6f;
    }

    public float getWayMaxspeedExplicit() {
        return this.ctxWay.getBuildInVariable(this.wayIdxMaxspeedExplicit) / 3.6f;
    }

    public float getWayMinspeed() {
        return this.ctxWay.getBuildInVariable(this.wayIdxMinspeed) / 3.6f;
    }

    public float getNodeMaxspeed() {
        return this.ctxNode.getBuildInVariable(this.nodeIdxMaxspeed) / 3.6f;
    }

    public double getEffectiveSpeedLimit() {
        double minspeed = getWayMinspeed();
        double espeed = minspeed > this.vmax ? minspeed : this.vmax;
        double maxspeed = getWayMaxspeed();
        return maxspeed < espeed ? maxspeed : espeed;
    }

    public double getBreakingSpeed(double vl) {
        if (vl == this.lastEffectiveLimit) {
            return this.lastBreakingSpeed;
        }
        double v = 0.8d * vl;
        double pw2 = this.pw + this.p_standby;
        double e = this.recup_efficiency;
        double x0 = (pw2 / vl) + (this.f_air * e * vl * vl) + ((1.0d - e) * this.f_roll);
        int i = 0;
        while (i < 5) {
            double v2 = v * v;
            double x = ((pw2 / v) + ((this.f_air * e) * v2)) - x0;
            double dx = (((2.0d * e) * this.f_air) * v) - (pw2 / v2);
            v -= x / dx;
            i++;
            e = e;
        }
        this.lastEffectiveLimit = vl;
        this.lastBreakingSpeed = v;
        return v;
    }
}
