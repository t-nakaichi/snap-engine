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
package org.esa.snap.core.util;

/**
 * This interface is a container for constants specific for ENVISAT-products.
 *
 * @author Norman Fomferra
 * @deprecated Since most (but not all) of the fields are Envisat-specific, this class lives in a wrong package.
 */
@Deprecated
public interface BeamConstants {


    String MERIS_L1B_RADIANCE_3_BAND_NAME = "radiance_3";
    String MERIS_L1B_RADIANCE_4_BAND_NAME = "radiance_4";
    String MERIS_L1B_RADIANCE_5_BAND_NAME = "radiance_5";
    String MERIS_L1B_RADIANCE_6_BAND_NAME = "radiance_6";
    String MERIS_L1B_RADIANCE_7_BAND_NAME = "radiance_7";
    String MERIS_L1B_RADIANCE_8_BAND_NAME = "radiance_8";
    String MERIS_L1B_RADIANCE_9_BAND_NAME = "radiance_9";
    String MERIS_L1B_RADIANCE_10_BAND_NAME = "radiance_10";
    String MERIS_L1B_RADIANCE_11_BAND_NAME = "radiance_11";
    String MERIS_L1B_RADIANCE_12_BAND_NAME = "radiance_12";
    String MERIS_L1B_RADIANCE_13_BAND_NAME = "radiance_13";
    String MERIS_L1B_RADIANCE_14_BAND_NAME = "radiance_14";
    String MERIS_L1B_RADIANCE_15_BAND_NAME = "radiance_15";
    /**
     * The names of the Meris Level 1 spectral band names.
     */
    String[] MERIS_L1B_SPECTRAL_BAND_NAMES = {
            "radiance_1", // 0
            "radiance_2", // 1
            MERIS_L1B_RADIANCE_3_BAND_NAME, // 2
            MERIS_L1B_RADIANCE_4_BAND_NAME, // 3
            MERIS_L1B_RADIANCE_5_BAND_NAME, // 4
            MERIS_L1B_RADIANCE_6_BAND_NAME, // 5
            MERIS_L1B_RADIANCE_7_BAND_NAME, // 6
            MERIS_L1B_RADIANCE_8_BAND_NAME, // 7
            MERIS_L1B_RADIANCE_9_BAND_NAME, // 8
            MERIS_L1B_RADIANCE_10_BAND_NAME, // 9
            MERIS_L1B_RADIANCE_11_BAND_NAME, // 10
            MERIS_L1B_RADIANCE_12_BAND_NAME, // 11
            MERIS_L1B_RADIANCE_13_BAND_NAME, // 12
            MERIS_L1B_RADIANCE_14_BAND_NAME, // 13
            MERIS_L1B_RADIANCE_15_BAND_NAME // 14
    };
    int MERIS_L1B_NUM_SPECTRAL_BANDS = MERIS_L1B_SPECTRAL_BAND_NAMES.length;

    /**
     * The names of the Meris Level 1 non spectral band names.
     */
    String[] MERIS_L1B_NON_SPECTRAL_BAND_NAMES = new String[]{
            "l1_flags", // 15
            "detector_index" // 16
    };
    int MERIS_L1B_NUM_NON_SPECTRAL_BANDS = MERIS_L1B_NON_SPECTRAL_BAND_NAMES.length;

    /**
     * The names of the Meris Level 1 MDS.
     */
    String[] MERIS_L1B_BAND_NAMES = StringUtils.addArrays(MERIS_L1B_SPECTRAL_BAND_NAMES,
                                                          MERIS_L1B_NON_SPECTRAL_BAND_NAMES);
    int MERIS_L1B_NUM_BAND_NAMES = MERIS_L1B_BAND_NAMES.length;

    String MERIS_L2_REFLEC_1_BAND_NAME = "reflec_1";
    String MERIS_L2_REFLEC_2_BAND_NAME = "reflec_2";
    String MERIS_L2_REFLEC_3_BAND_NAME = "reflec_3";
    String MERIS_L2_REFLEC_4_BAND_NAME = "reflec_4";
    String MERIS_L2_REFLEC_5_BAND_NAME = "reflec_5";
    String MERIS_L2_REFLEC_6_BAND_NAME = "reflec_6";
    String MERIS_L2_REFLEC_7_BAND_NAME = "reflec_7";
    String MERIS_L2_REFLEC_8_BAND_NAME = "reflec_8";
    String MERIS_L2_REFLEC_9_BAND_NAME = "reflec_9";
    String MERIS_L2_REFLEC_10_BAND_NAME = "reflec_10";
    String MERIS_L2_REFLEC_12_BAND_NAME = "reflec_12";
    String MERIS_L2_REFLEC_13_BAND_NAME = "reflec_13";
    String MERIS_L2_REFLEC_14_BAND_NAME = "reflec_14";
    String MERIS_L2_YELLOW_SUBST_BAND_NAME = "yellow_subs";
    /**
     * The names of the Meris Level 2 MDS.
     */
    String[] MERIS_L2_BAND_NAMES = {
            MERIS_L2_REFLEC_1_BAND_NAME, //  0
            MERIS_L2_REFLEC_2_BAND_NAME, //  1
            MERIS_L2_REFLEC_3_BAND_NAME, //  2
            MERIS_L2_REFLEC_4_BAND_NAME, //  3
            MERIS_L2_REFLEC_5_BAND_NAME, //  4
            MERIS_L2_REFLEC_6_BAND_NAME, //  5
            MERIS_L2_REFLEC_7_BAND_NAME, //  6
            MERIS_L2_REFLEC_8_BAND_NAME, //  7
            MERIS_L2_REFLEC_9_BAND_NAME, //  8
            MERIS_L2_REFLEC_10_BAND_NAME, //  9
            MERIS_L2_REFLEC_12_BAND_NAME, // 10
            MERIS_L2_REFLEC_13_BAND_NAME, // 11
            MERIS_L2_REFLEC_14_BAND_NAME, // 12
            "algal_1", // 13
            "algal_2", // 14
            "toa_veg", // 15
            "boa_veg", // 16
            MERIS_L2_YELLOW_SUBST_BAND_NAME, // 17
            "total_susp", // 18
            "rect_refl_red", // 19
            "rect_refl_nir", // 20
            "surf_press", // 21
            "photosyn_rad", // 22
            "aero_alpha", // 23
            "aero_opt_thick_443", // 24
            "aero_opt_thick_550", // 25
            "aero_opt_thick_865", // 26
            "water_vapour", // 27
            "cloud_albedo", // 28
            "cloud_top_press", // 29
            "cloud_opt_thick", // 30
            "cloud_type", // 31
            "l2_flags"                       // 32
    };

    /**
     * The names of the Meris ADS
     */
    String[] MERIS_TIE_POINT_GRID_NAMES = {
            "latitude", //  0
            "longitude", //  1
            "dem_alt", //  2
            "dem_rough", //  3
            "lat_corr", //  4
            "lon_corr", //  5
            "sun_zenith", //  6
            "sun_azimuth", //  7
            "view_zenith", //  8
            "view_azimuth", //  9
            "zonal_wind", // 10
            "merid_wind", // 11
            "atm_press", // 12
            "ozone", // 13
            "rel_hum"       // 14
    };

    /**
     * MERIS sun spectral flux for specific bands as specified in MERIS scaling factor GADS
     */
    float[] MERIS_SOLAR_FLUXES = {
            1714.9084f, //  0
            1872.3961f, //  1
            1926.6102f, //  2
            1930.2483f, //  3
            1804.2762f, //  4
            1651.5836f, //  5
            1531.4067f, //  6
            1475.615f, //  7
            1408.9949f, //  8
            1265.5425f, //  9
            1255.4227f, // 10
            1178.0286f, // 11
            955.07043f, // 12
            914.18945f, // 13
            882.8275f   // 14
    };

    String MERIS_REFLECTANCE_UNIT = "dl";

    String AATSR_L1B_BTEMP_NADIR_1200_BAND_NAME = "btemp_nadir_1200";
    String AATSR_L1B_BTEMP_NADIR_1100_BAND_NAME = "btemp_nadir_1100";
    String AATSR_L1B_BTEMP_NADIR_0370_BAND_NAME = "btemp_nadir_0370";
    String AATSR_L1B_REFLEC_NADIR_1600_BAND_NAME = "reflec_nadir_1600";
    String AATSR_L1B_REFLEC_NADIR_0870_BAND_NAME = "reflec_nadir_0870";
    String AATSR_L1B_REFLEC_NADIR_0670_BAND_NAME = "reflec_nadir_0670";
    String AATSR_L1B_REFLEC_NADIR_0550_BAND_NAME = "reflec_nadir_0550";
    String AATSR_L1B_BTEMP_FWARD_1200_BAND_NAME = "btemp_fward_1200";
    String AATSR_L1B_BTEMP_FWARD_1100_BAND_NAME = "btemp_fward_1100";
    String AATSR_L1B_BTEMP_FWARD_0370_BAND_NAME = "btemp_fward_0370";
    String AATSR_L1B_REFLEC_FWARD_1600_BAND_NAME = "reflec_fward_1600";
    String AATSR_L1B_REFLEC_FWARD_0870_BAND_NAME = "reflec_fward_0870";
    String AATSR_L1B_REFLEC_FWARD_0670_BAND_NAME = "reflec_fward_0670";
    String AATSR_L1B_REFLEC_FWARD_0550_BAND_NAME = "reflec_fward_0550";
    String AATSR_L1B_CONFID_FLAGS_NADIR_BAND_NAME = "confid_flags_nadir";
    String AATSR_L1B_CONFID_FLAGS_FWARD_BAND_NAME = "confid_flags_fward";
    String AATSR_L1B_CLOUD_FLAGS_NADIR_BAND_NAME = "cloud_flags_nadir";
    String AATSR_L1B_CLOUD_FLAGS_FWARD_BAND_NAME = "cloud_flags_fward";
    /**
     * The names of the AATRS Level 1 MDS
     */
    String[] AATSR_L1B_BAND_NAMES = {
            AATSR_L1B_BTEMP_NADIR_1200_BAND_NAME, //  0
            AATSR_L1B_BTEMP_NADIR_1100_BAND_NAME, //  1
            AATSR_L1B_BTEMP_NADIR_0370_BAND_NAME, //  2
            AATSR_L1B_REFLEC_NADIR_1600_BAND_NAME, //  3
            AATSR_L1B_REFLEC_NADIR_0870_BAND_NAME, //  4
            AATSR_L1B_REFLEC_NADIR_0670_BAND_NAME, //  5
            AATSR_L1B_REFLEC_NADIR_0550_BAND_NAME, //  6
            AATSR_L1B_BTEMP_FWARD_1200_BAND_NAME, //  7
            AATSR_L1B_BTEMP_FWARD_1100_BAND_NAME, //  8
            AATSR_L1B_BTEMP_FWARD_0370_BAND_NAME, //  9
            AATSR_L1B_REFLEC_FWARD_1600_BAND_NAME, // 10
            AATSR_L1B_REFLEC_FWARD_0870_BAND_NAME, // 11
            AATSR_L1B_REFLEC_FWARD_0670_BAND_NAME, // 12
            AATSR_L1B_REFLEC_FWARD_0550_BAND_NAME, // 13
            AATSR_L1B_CONFID_FLAGS_NADIR_BAND_NAME, // 14
            AATSR_L1B_CONFID_FLAGS_FWARD_BAND_NAME, // 15
            AATSR_L1B_CLOUD_FLAGS_NADIR_BAND_NAME, // 16
            AATSR_L1B_CLOUD_FLAGS_FWARD_BAND_NAME   // 17
    };

    /**
     * The wavelengths for the seven AATSR spectral channels in nanometers.
     */
    float[] AATSR_WAVELENGTHS = {
            555.0F, // 0
            659.0F, // 1
            865.0F, // 2
            1610.0F, // 3
            3700.0F, // 4
            10850.0F, // 5
            12000.0F  // 6
    };

    /**
     * The bandwidths for the seven AATSR spectral channels in nanometers.
     */
    float[] AATSR_BANDWIDTHS = {
            20.0F, // 0
            20.0F, // 1
            20.0F, // 2
            60.0F, // 3
            390.0F, // 4
            900.0F, // 5
            1000.0F, // 6
    };

    /**
     * The solar fluxes for the seven AATSR spectral channels in mW / (m^2 sr nm)
     */
    float[] AATSR_SOLAR_FLUXES = {
            0.0F, // 0
            0.0F, // 1
            0.0F, // 2
            0.0F, // 3
            0.0F, // 4
            0.0F, // 5
            0.0F, // 6
    };

    String AATSR_LAT_DS_NAME = "latitude";
    String AATSR_LON_DS_NAME = "longitude";
    String AATSR_ALTITUDE_DS_NAME = "altitude";
    String AATSR_SUN_ELEV_NADIR_DS_NAME = "sun_elev_nadir";
    String AATSR_VIEW_ELEV_NADIR_DS_NAME = "view_elev_nadir";
    String AATSR_SUN_AZIMUTH_NADIR_DS_NAME = "sun_azimuth_nadir";
    String AATSR_VIEW_AZIMUTH_NADIR_DS_NAME = "view_azimuth_nadir";
    String AATSR_SUN_ELEV_FWARD_DS_NAME = "sun_elev_fward";
    String AATSR_VIEW_ELEV_FWARD_DS_NAME = "view_elev_fward";
    String AATSR_VIEW_AZIMUTH_FWARD_DS_NAME = "view_azimuth_fward";
    String AATSR_SUN_AZIMUTH_FWARD_DS_NAME = "sun_azimuth_fward";

    /**
     * The names of the AATSR ADS
     */
    String[] AATSR_TIE_POINT_GRID_NAMES = {
            AATSR_LAT_DS_NAME, // 0
            AATSR_LON_DS_NAME, // 1
            "lat_corr_nadir", // 2
            "lon_corr_nadir", // 3
            "lat_corr_fward", // 4
            "lon_corr_fward", // 5
            AATSR_ALTITUDE_DS_NAME, // 6
            AATSR_SUN_ELEV_NADIR_DS_NAME, // 7
            AATSR_VIEW_ELEV_NADIR_DS_NAME, // 8
            AATSR_SUN_AZIMUTH_NADIR_DS_NAME, // 9
            AATSR_VIEW_AZIMUTH_NADIR_DS_NAME, // 10
            AATSR_SUN_ELEV_FWARD_DS_NAME, // 11
            AATSR_VIEW_ELEV_FWARD_DS_NAME, // 12
            AATSR_SUN_AZIMUTH_FWARD_DS_NAME, // 13
            AATSR_VIEW_AZIMUTH_FWARD_DS_NAME    // 14
    };

    /**
     * The names of the AATRS Level 2 MDS
     */
    String[] AATSR_L2_BAND_NAMES = {
            "flags",
            "sst_nadir",
            "sst_comb",
            "cloud_top_temp",
            "cloud_top_height",
            "lst",
            "ndvi"
    };

    String MERIS_GADS_NAME = "Scaling_Factor_GADS";
    String AATSR_L1B_GADS_NAME = "VISIBLE_CALIB_COEFS_GADS";
}
