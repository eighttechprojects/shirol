package com.eighttechprojects.propertytaxshirol.WMSMap;

import com.google.android.gms.maps.model.UrlTileProvider;

public abstract class WMSTileProvider extends UrlTileProvider {

    // Web Mercator n/w corner of the map.
    private static final double[] TILE_ORIGIN = {-20037508.34789244, 20037508.34789244};
    //array indexes for that data
    private static final int ORIG_X = 0;
    private static final int ORIG_Y = 1; // "

    // Size of square world map in meters, using WebMerc projection.
    private static final double MAP_SIZE = 20037508.34789244 * 2;

    // array indexes for array to hold bounding boxes.
    protected static final int MINX = 0;
    protected static final int MAXX = 1;
    protected static final int MINY = 2;
    protected static final int MAXY = 3;

//------------------------------------------------- Constructor ------------------------------------------------------------------------------------------------------------------------

    // Construct with tile size in pixels, normally 256, see parent class.
    public WMSTileProvider(int x, int y) {
        super(x, y);
    }

//------------------------------------------------- getBoundingBox ------------------------------------------------------------------------------------------------------------------------

    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    protected double[] getBoundingBox(int x, int y, int zoom) {
        double tileSize = MAP_SIZE / Math.pow(2, zoom);
        double minX = TILE_ORIGIN[ORIG_X] + x * tileSize;
        double maxX = TILE_ORIGIN[ORIG_X] + (x+1) * tileSize;
        double minY = TILE_ORIGIN[ORIG_Y] - (y+1) * tileSize;
        double maxY = TILE_ORIGIN[ORIG_Y] - y * tileSize;
        double[] box = new double[4];
        box[MINX] = minX;
        box[MINY] = minY;
        box[MAXX] = maxX;
        box[MAXY] = maxY;
        return box;
    }

}
