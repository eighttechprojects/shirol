package com.eighttechprojects.propertytaxshirol.WMSMap;

public class WMSProvider {

    public String srs = "EPSG:900913"; //""EPSG:900913";
    public String url;
    public String layers;
    public String styles = "default";
    public String transparent = "true";

//------------------------------------------------- WMSProvider ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider() {
    }

//------------------------------------------------- layers ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider layers(String layers) {
        this.layers = layers;
        return this;
    }

//------------------------------------------------- url ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider url(String url) {
        this.url = url;
        return this;
    }

//------------------------------------------------- srs ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider srs(String srs) {
        this.srs = srs;
        return this;
    }

//------------------------------------------------- styles ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider styles(String styles) {
        this.styles = styles;
        return this;
    }

//------------------------------------------------- transparent ------------------------------------------------------------------------------------------------------------------------

    public WMSProvider transparent(boolean transparent) {
        this.transparent = Boolean.toString(transparent);
        return this;
    }

//------------------------------------------------- getWMSLayer ------------------------------------------------------------------------------------------------------------------------

    public static WMSProvider getWMSLayer(String wmsLayerURl){
        String url  = wmsLayerURl.substring(0,wmsLayerURl.indexOf("?"));
        String[] providerArray = wmsLayerURl.split("&");
        String layers = "";
        for (String s : providerArray) {
            if (s.startsWith("layers") || s.startsWith("LAYERS")) {
                String lay = s.split("=")[1];
                if (lay.contains("%3a") || lay.contains("%3A")) {
                    if (lay.contains("%3a")) {
                        layers = lay.replace("%3a", ":");
                    }
                    else if (lay.contains("%3A")) {
                        layers = lay.replace("%3A", ":");
                    }
                }
                else{
                    layers = lay;
                }
                break;
            }
        }
        return new WMSProvider().url(url).layers(layers);
    }


}
