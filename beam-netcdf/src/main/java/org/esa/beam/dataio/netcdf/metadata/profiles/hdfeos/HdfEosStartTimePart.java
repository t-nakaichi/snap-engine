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

package org.esa.beam.dataio.netcdf.metadata.profiles.hdfeos;

import org.esa.beam.dataio.netcdf.metadata.ProfilePart;
import org.esa.beam.dataio.netcdf.metadata.ProfileReadContext;
import org.esa.beam.dataio.netcdf.metadata.ProfileWriteContext;
import org.esa.beam.dataio.netcdf.util.ReaderUtils;
import org.esa.beam.framework.datamodel.Product;
import org.jdom.Element;

import java.io.IOException;


public class HdfEosStartTimePart extends ProfilePart {

    @Override
    public void read(ProfileReadContext ctx, Product p) throws IOException {
        Element element = (Element) ctx.getProperty(HdfEosUtils.CORE_METADATA);
        if (element != null) {
            String date = HdfEosUtils.getValue(element, "INVENTORYMETADATA", "MASTERGROUP", "RANGEDATETIME",
                                               "RANGEBEGINNINGDATE", "VALUE");
            String time = HdfEosUtils.getValue(element, "INVENTORYMETADATA", "MASTERGROUP", "RANGEDATETIME",
                                               "RANGEBEGINNINGTIME", "VALUE");
            if (date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
                p.setStartTime(ReaderUtils.parseDateTime(date + " " + time));
            }
        }
    }

    @Override
    public void define(ProfileWriteContext ctx, Product p) throws IOException {
        throw new IllegalStateException();
    }
}
