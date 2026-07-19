package btools.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Area {
    private List<Polygon> poslist = new ArrayList();
    private List<Polygon> neglist = new ArrayList();

    public static void main(String[] args) throws IOException {
        Area a = new Area(new File(args[0]));
        System.out.println(args[1] + " is in " + args[0] + "=" + a.isInArea(Long.parseLong(args[1])));
    }

    public Area(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine();
        while (true) {
            String head = br.readLine();
            if (head != null && !"END".equals(head)) {
                Polygon pol = new Polygon(br);
                if (head.startsWith("!")) {
                    this.neglist.add(pol);
                } else {
                    this.poslist.add(pol);
                }
            } else {
                return;
            }
        }
    }

    public boolean isInArea(long id) {
        for (int i = 0; i < this.poslist.size(); i++) {
            if (this.poslist.get(i).isInPolygon(id)) {
                for (int j = 0; j < this.neglist.size(); j++) {
                    if (this.neglist.get(j).isInPolygon(id)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isInBoundingBox(long id) {
        for (int i = 0; i < this.poslist.size(); i++) {
            if (this.poslist.get(i).isInBoundingBox(id)) {
                return true;
            }
        }
        return false;
    }
}
