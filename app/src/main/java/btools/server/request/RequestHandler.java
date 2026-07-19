package btools.server.request;

import btools.router.OsmTrack;
import btools.router.RoutingContext;
import btools.server.ServiceContext;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class RequestHandler {
    protected Map<String, String> params;
    protected ServiceContext serviceContext;

    public abstract String formatTrack(OsmTrack osmTrack);

    public abstract String getFileName();

    public abstract String getMimeType();

    public abstract RoutingContext readRoutingContext();

    public RequestHandler(ServiceContext serviceContext, Map<String, String> params) {
        this.serviceContext = serviceContext;
        this.params = params;
    }
}
