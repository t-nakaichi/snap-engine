/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.snap.core.util.jai;

import com.sun.media.jai.util.CacheDiagnostics;

import javax.media.jai.TileCache;
import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Comparator;

public class VerboseTileCache implements TileCache {
    static boolean verbose = true;
    private final TileCache tileCache;
    private CacheDiagnostics cacheDiagnostics;

    public VerboseTileCache(TileCache tileCache) {
        this.tileCache = tileCache;
        if (tileCache instanceof CacheDiagnostics) {
            cacheDiagnostics = (CacheDiagnostics) tileCache;
            cacheDiagnostics.enableDiagnostics();
        }
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        VerboseTileCache.verbose = verbose;
    }

    private static String getTilePos(int tileX, int tileY) {
        return "(" + tileX + "," + tileY + ")";
    }

    public void add(RenderedImage renderedImage, int tileX, int tileY, Raster tile) {
        tileCache.add(renderedImage, tileX, tileY, tile);
        trace("add", renderedImage, tile, getTilePos(tileX, tileY));
    }

    public void add(RenderedImage renderedImage, int tileX, int tileY, Raster tile, Object o) {
        tileCache.add(renderedImage, tileX, tileY, tile, o);
        trace("add-2", renderedImage, tile, getTilePos(tileX, tileY));
    }

    public void remove(RenderedImage renderedImage, int tileX, int tileY) {
        tileCache.remove(renderedImage, tileX, tileY);
        trace("remove", renderedImage, null, getTilePos(tileX, tileY));
    }

    public Raster getTile(RenderedImage renderedImage, int tileX, int tileY) {
        Raster tile = tileCache.getTile(renderedImage, tileX, tileY);
        trace("getTile", renderedImage, tile, getTilePos(tileX, tileY));
        return tile;
    }

    public Raster[] getTiles(RenderedImage renderedImage) {
        Raster[] tiles = tileCache.getTiles(renderedImage);
        trace("getTiles", renderedImage, null, "n.a.");
        return tiles;
    }

    public void removeTiles(RenderedImage renderedImage) {
        tileCache.removeTiles(renderedImage);
        trace("removeTiles", renderedImage, null, "n.a.");
    }

    public void addTiles(RenderedImage renderedImage, Point[] points, Raster[] rasters, Object o) {
        tileCache.addTiles(renderedImage, points, rasters, o);
        trace("addTiles", renderedImage, null, "n.a.");
    }

    public Raster[] getTiles(RenderedImage renderedImage, Point[] points) {
        Raster[] tiles = tileCache.getTiles(renderedImage, points);
        trace("getTiles", renderedImage, null, "n.a.");
        return tiles;
    }

    public void flush() {
        trace("flush (start)", null, null, "n.a.");
        tileCache.flush();
        trace("flush (end)", null, null, "n.a.");
    }

    public void memoryControl() {
        trace("memoryControl (start)", null, null, "n.a.");
        tileCache.memoryControl();
        trace("memoryControl (end)", null, null, "n.a.");
    }

    /**
     * @deprecated does nothing; returns always 0
     */
    @Deprecated
    public int getTileCapacity() {
        // needs to be implemented, the JAI interface still has this method tb 2023-12-18
        return 0;
//        return tileCache.getTileCapacity();
    }

    /**
     * @deprecated does nothing; no replacement
     */
    @Deprecated
    public void setTileCapacity(int i) {
        // needs to be implemented, the JAI interface still has this method tb 2023-12-18
//        tileCache.setTileCapacity(i);
    }

    public long getMemoryCapacity() {
        return tileCache.getMemoryCapacity();
    }

    public void setMemoryCapacity(long l) {
        tileCache.setMemoryCapacity(l);
    }

    public float getMemoryThreshold() {
        return tileCache.getMemoryThreshold();
    }

    public void setMemoryThreshold(float v) {
        tileCache.setMemoryThreshold(v);
    }

    public Comparator getTileComparator() {
        return tileCache.getTileComparator();
    }

    public void setTileComparator(Comparator comparator) {
        tileCache.setTileComparator(comparator);
    }

    private void trace(String method, RenderedImage image, Raster tile, String tilePos) {
        if (verbose) {
            println("JAI TileCache Diagnostics: ======================================");
            println("  cache   = " + tileCache);
            println("  method  = " + method);
            println("  image   = " + image);
            println("  tilePos = " + tilePos);
            String tileString = "  tile    = " + tile;
            String dimString = "";
            if (tile != null) {
                dimString = " [width = " + tile.getWidth() + " height = " + tile.getHeight() + "]";
            }
            println(tileString + dimString);
            if (cacheDiagnostics != null) {
                println("  cacheTileCount  = " + cacheDiagnostics.getCacheTileCount());
                println("  cacheHitCount   = " + cacheDiagnostics.getCacheHitCount());
                println("  cacheMissCount  = " + cacheDiagnostics.getCacheMissCount());
                println("  cacheMemoryUsed = " + cacheDiagnostics.getCacheMemoryUsed());
            }
        } else {
            println(method + " : " + tilePos + " " + image);
        }
    }

    private void println(String x) {
        System.out.println(x);
    }
}
