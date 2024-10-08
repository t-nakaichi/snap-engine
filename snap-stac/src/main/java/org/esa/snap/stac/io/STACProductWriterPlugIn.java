/*
 * Copyright (C) 2024 by SkyWatch Space Applications Inc. http://www.skywatch.com
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
package org.esa.snap.stac.io;

import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * The PlugIn for the SpatioTemporal Asset Catalog(STAC) Item Product format.
 */
public class STACProductWriterPlugIn implements ProductWriterPlugIn {

    private static final String[] EXTENSIONS = new String[] {STACProductReaderPlugIn.METADATA_EXT};

    /**
     * Constructs a new product writer plug-in instance.
     */
    public STACProductWriterPlugIn() {
    }

    @Override
    public EncodeQualification getEncodeQualification(Product product) {
        GeoCoding geoCoding = product.getSceneGeoCoding();
        if (geoCoding == null) {
            return new EncodeQualification(EncodeQualification.Preservation.PARTIAL,
                    "The product is not geo-coded. A usual TIFF file will be written instead.");
        } else if (!(geoCoding instanceof CrsGeoCoding)) {
            return new EncodeQualification(EncodeQualification.Preservation.PARTIAL,
                    "The product is geo-coded but seems not rectified. Geo-coding information may not be properly preserved.");
        } else if (product.isMultiSize()) {
            return new EncodeQualification(EncodeQualification.Preservation.UNABLE,
                    "Cannot write multisize products. Consider resampling the product first.");
        } else {
            return new EncodeQualification(EncodeQualification.Preservation.FULL);
        }
    }

    /**
     * Gets the default file extensions associated with each of the format names returned by the <code>{@link
     * #getFormatNames}</code> method. <p>The string array returned shall always have the same lenhth as the array
     * returned by the <code>{@link #getFormatNames}</code> method. <p>The extensions returned in the string array shall
     * always include a leading colon ('.') character, e.g. <code>".hdf"</code>
     *
     * @return the default file extensions for this product I/O plug-in, never <code>null</code>
     */
    public String[] getDefaultFileExtensions() {
        return EXTENSIONS;
    }

    /**
     * Returns an array containing the classes that represent valid output types for this GeoTIFF product writer.
     * <p> Intances of the classes returned in this array are valid objects for the <code>writeProductNodes</code>
     * method of the <code>AbstractProductWriter</code> interface (the method will not throw an
     * <code>InvalidArgumentException</code> in this case).
     *
     * @return an array containing valid output types, never <code>null</code>
     *
     * @see AbstractProductWriter#writeProductNodes
     */
    public Class[] getOutputTypes() {
        return new Class[]{
                String.class,
                File.class,
        };
    }

    /**
     * Gets a short description of this plug-in. If the given locale is set to <code>null</code> the default locale is
     * used.
     * <p> In a GUI, the description returned could be used as tool-tip text.
     *
     * @param name the local for the given description string, if <code>null</code> the default locale is used
     *
     * @return a textual description of this product reader/writer
     */
    public String getDescription(Locale name) {
        return STACProductReaderPlugIn.DESCRIPTION;
    }

    /**
     * Returns a string array containing the single entry <code>&quot;GeoTIFF&quot;</code>.
     */
    @Override
    public String[] getFormatNames() {
        return new String[]{STACProductReaderPlugIn.FORMAT};
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions(), getDescription(null));
    }

    /**
     * Creates an instance of the actual GeoTIFF product writer class.
     *
     * @return a new instance of the <code>GeoTIFFProductWriter</code> class
     */
    @Override
    public ProductWriter createWriterInstance() {
        return new STACProductWriter(this);
    }
}
