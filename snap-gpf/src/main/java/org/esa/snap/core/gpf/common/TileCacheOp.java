package org.esa.snap.core.gpf.common;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorContext;
import org.esa.snap.core.gpf.internal.OperatorImage;
import org.esa.snap.core.image.RasterDataNodeOpImage;
import org.esa.snap.core.image.VirtualBandOpImage;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.TileCache;
import java.awt.image.RenderedImage;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * An operator that add the images of it source product to the global tile cache.
 *
 * @author Marco Zuehlke
 * @since SNAP 5
 */
@OperatorMetadata(alias = "TileCache",
        authors = "Marco Zuehlke",
        version = "1.0",
        copyright = "(c) 2016 by Brockmann Consult",
        description = "Ads the image tot the global tile-cache.")
public class TileCacheOp extends Operator {

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;
    @TargetProduct
    private Product targetProduct;

    @Override
    public void initialize() throws OperatorException {
        Logger logger = getLogger();
        TileCache tileCache = JAI.getDefaultInstance().getTileCache();
        Band[] bands = sourceProduct.getBands();
        logger.info("");
        logger.info("#bands="+bands.length);
        for (Band band : bands) {
            String bandName = band.getName();
            if (OperatorContext.isRegularBand(band)) {
                MultiLevelImage multiLevelImage = band.getSourceImage();
                if (multiLevelImage instanceof DefaultMultiLevelImage) {
                    DefaultMultiLevelImage defaultMultiLevelImage = (DefaultMultiLevelImage) multiLevelImage;
                    RenderedImage renderedImage = defaultMultiLevelImage.getSource().getImage(0);
                    if (renderedImage instanceof OpImage) {
                        OpImage opImage = (OpImage) renderedImage;
                        String imageName = renderedImage.getClass().getSimpleName() + " " + getImageComment(renderedImage);
                        logger.info(String.format("setting global tile-cache for %s (image:%s)", bandName, imageName));
                        opImage.setTileCache(tileCache);
                        continue;
                    }
                }
            }
            logger.info("not setting global tile cache for " + bandName);
        }
        targetProduct = sourceProduct;
        logger.info("");
    }

    private static String getImageComment(RenderedImage image) {
        if (image instanceof RasterDataNodeOpImage) {
            RasterDataNodeOpImage rdnoi = (RasterDataNodeOpImage) image;
            return rdnoi.getRasterDataNode().getName();
        } else if (image instanceof VirtualBandOpImage) {
            VirtualBandOpImage vboi = (VirtualBandOpImage) image;
            return "";//vboi.getExpression();
        } else if (image instanceof OperatorImage) {
            final String s = image.toString();
            final int p1 = s.indexOf('[');
            final int p2 = s.indexOf(',', p1 + 1);
            if (p1 > 0 && p2 > p1) {
                return s.substring(p1 + 1, p2);
            }
            return s;
        } else {
            return "";
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(TileCacheOp.class);
        }
    }

}
