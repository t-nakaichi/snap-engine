package org.esa.snap.core.gpf.internal;

import javax.media.jai.TileCache;
import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A tile cache implementation, that
 *
 * @author marcoz
 */
public class SingleTileCache implements TileCache {

    private static class LocalTileCache {
        private int tileX = -1;
        private int tileY = -1;
        private Raster raster;
    }

    private final ThreadLocal<LocalTileCache> localTileCache;

    public SingleTileCache() {
        localTileCache = new ThreadLocal<LocalTileCache>() {
            @Override
            protected LocalTileCache initialValue() {
                return new LocalTileCache();
            }
        };
    }

    @Override
    public void add(RenderedImage owner, int tileX, int tileY, Raster data) {
        add(owner, tileX, tileY, data, null);
    }

    @Override
    public void add(RenderedImage owner, int tileX, int tileY, Raster data, Object tileCacheMetric) {
        LocalTileCache localTileCache = this.localTileCache.get();
//        if (localTileCache.tileX != tileX || localTileCache.tileY != tileY) {
//            System.out.println("SingleTileCache.add NEW tileX = " + tileX + " tileY = " + tileY + " " + owner);
//        }
        localTileCache.tileX = tileX;
        localTileCache.tileY = tileY;
        localTileCache.raster = data;
    }

    @Override
    public void remove(RenderedImage owner, int tileX, int tileY) {
        LocalTileCache localTileCache = this.localTileCache.get();
        if (localTileCache.tileX == tileX && localTileCache.tileY == tileY) {
            localTileCache.raster = null;
        }
    }

    @Override
    public Raster getTile(RenderedImage owner, int tileX, int tileY) {
        LocalTileCache localTileCache = this.localTileCache.get();
        if (localTileCache.tileX == tileX && localTileCache.tileY == tileY) {
            return localTileCache.raster;
        }
//        System.out.println("SingleTileCache.getTile NULL tileX = " + tileX+ " tileY = " + tileY + " " + owner);
        return null;
    }

    @Override
    public Raster[] getTiles(RenderedImage owner) {
        Raster raster = localTileCache.get().raster;
        if (raster != null) {
            return new Raster[]{raster};
        } else {
            return new Raster[0];
        }
    }

    @Override
    public void removeTiles(RenderedImage owner) {
        flush();
    }

    @Override
    public void addTiles(RenderedImage owner, Point[] tileIndices, Raster[] tiles, Object tileCacheMetric) {
        throw new UnsupportedOperationException("org.esa.snap.core.gpf.internal.SingleTileCache.addTiles not supported");

    }

    @Override
    public Raster[] getTiles(RenderedImage owner, Point[] tileIndices) {
        throw new UnsupportedOperationException("org.esa.snap.core.gpf.internal.SingleTileCache.getTiles(java.awt.image.RenderedImage, java.awt.Point[]) not supported");
    }

    @Override
    public void flush() {
        LocalTileCache localTileCache = this.localTileCache.get();
        localTileCache.raster = null;
        localTileCache.tileX = -1;
        localTileCache.tileY = -1;
    }

    @Override
    public void memoryControl() {
        // no-op
    }

    @Override
    public void setTileCapacity(int tileCapacity) {
        // no-op
    }

    @Override
    public int getTileCapacity() {
        return 0;
    }

    @Override
    public void setMemoryCapacity(long memoryCapacity) {
        // no-op
    }

    @Override
    public long getMemoryCapacity() {
        return 1;
    }

    @Override
    public void setMemoryThreshold(float memoryThreshold) {
        // no-op
    }

    @Override
    public float getMemoryThreshold() {
        return 0;
    }

    @Override
    public void setTileComparator(Comparator comparator) {
        // no-op
    }

    @Override
    public Comparator getTileComparator() {
        return null;
    }
}
