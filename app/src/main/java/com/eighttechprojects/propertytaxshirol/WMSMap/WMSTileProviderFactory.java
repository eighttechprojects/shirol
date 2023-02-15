package com.eighttechprojects.propertytaxshirol.WMSMap;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class WMSTileProviderFactory {

    private static final String TAG = "WMSDEMO";

    private static final String WMS_SERVICE_PARAMETERS =
            "?service=WMS&"
                    + "request=GetMap&"
                    + "version=1.1.1&"
                    + "format=image/png&"
                    + "width=256&"
                    + "height=256&"
                    + "transparent=true&"
                    + "bbox=%f,%f,%f,%f&"
                    + "srs=%s&"
                    + "layers=%s";

//------------------------------------------------- getTileProvider ------------------------------------------------------------------------------------------------------------------------

    public static WMSTileProvider getWMSTileProvider(final WMSProvider provider) {

        return new WMSTileProvider(256,256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String urlStr = provider.url + String.format(Locale.US, WMS_SERVICE_PARAMETERS, bbox[MINX], bbox[MINY], bbox[MAXX], bbox[MAXY], provider.srs, provider.layers, provider.styles);
                Log.d(TAG, urlStr);
                URL url = null;
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.getMessage());
                }
                return url;
            }
        };
    }

}
