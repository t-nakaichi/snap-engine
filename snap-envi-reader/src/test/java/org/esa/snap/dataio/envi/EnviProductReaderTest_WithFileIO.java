package org.esa.snap.dataio.envi;

import com.bc.ceres.test.LongTestRunner;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
import java.text.ParseException;

import static org.junit.Assert.*;

@RunWith(LongTestRunner.class)
public class EnviProductReaderTest_WithFileIO {

    private static final String MAP_INFO = "SamerAlbers, 1.0000, 1.0000, -479862.9999, 1288756.5614, 8.0000000000e+03,8.0000000000e+03,WGS-84,units=Meters";
    private static final String PROJECTION_INFO = "9, 6378137.0, 6356752.3, -17.500000, 63.500000,-32.500000, -2.500000, 7.0,0.0,WGS-84, SamerAlbers, units=Meters";
    private static final String INTERLEAVE = "Test Interleave";
    private static final String PRODUCT_NAME = "TestProductName";
    private static final String PRODUCT_TYPE = "unknown";
    private static final int WIDTH = 25;
    private static final int HEIGHT = 25;
    private static final int NUM_BANDS = 6;
    private static final int HEADER_OFFSET = 0;
    private static final String FILE_TYPE = "ENVI Standard";
    private static final int DATA_TYPE = 2;
    private static final String SENSOR_TYPE = "unknown";
    private static final int BYTE_ORDER = 1;
    private static final String WAVELENGTH_UNITS = "unknown";

    private File tempDir;

    @Before
    public void setUp() throws Exception {
        final String ioTempDir = System.getProperty("java.io.tmpdir");
        tempDir = new File(ioTempDir, "tempEnviProductReaderTest");
        if (tempDir.exists()) {
            assertTrue(FileUtils.deleteTree(tempDir));
        }
        assertTrue(tempDir.mkdir());
    }

    @After
    public void tearDown() throws Exception {
        assertTrue(FileUtils.deleteTree(tempDir));
    }

    @Test
    public void testCreateEnviImageFile() throws IOException {
        doTest("envifile.hdr", "envifile.img");
        doTest("envifile.img.hdr", "envifile.img");

        doTest("envifile.hdr", "envifile.IMG");
        doTest("envifile.HDR", "envifile.img");
        doTest("envifile.HDR", "envifile.IMG");

        doTest("envifile.hdr", "envifile.bin");
        doTest("envifile.img.hdr", "envifile.bin");
        doTest("envifile.bin.hdr", "envifile.bin");

        doTest("envifile.hdr", "envifile.bil");
        doTest("envifile.bil.hdr", "envifile.bil");

        doTest("envifile.hdr", "envifile.bsq");
        doTest("envifile.bsq.hdr", "envifile.bsq");
    }

    @Test
    public void testCreateEnviImageFile_mixedCaseExtension() throws IOException {
        doTest("envifile.hdr", "envifile.iMg");
        doTest("envifile.img.hdr", "envifile.ImG");
    }

    @Test
    public void testCreateEnviImageFile_missingImageFile() throws IOException {
        final File hdrFile = new File(tempDir, "envi.hdr");
        assertTrue(hdrFile.createNewFile());

        try {
            EnviProductReader.getEnviImageFile(hdrFile);
            fail("IOException expected");
        } catch (IOException expected) {
           final  String message = expected.getMessage();
            assertTrue(message.contains("No matching ENVI image file found for header file:"));
        }
    }

    private void doTest(String hdrFilename, String imgFilename) throws IOException {
        final File hdrFile = new File(tempDir, hdrFilename);
        assertTrue(hdrFile.createNewFile());

        final File imgFile = new File(tempDir, imgFilename);
        assertTrue(imgFile.createNewFile());

        try {
            assertEquals(imgFile.getCanonicalFile(), EnviProductReader.getEnviImageFile(hdrFile).getCanonicalFile());
        } finally {
            // cleanup
            imgFile.delete();
            hdrFile.delete();
        }
    }

    @Test
    public void testReadProductNodes_WithoutSensingStartStop() throws IOException, ParseException {
        final String sensingStartStop = "";
        final String headerContent = createHeaderFileContent(sensingStartStop);
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final ProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        final Product product = reader.readProductNodes(headerFile, null);

        try {
            assertNotNull(product);
            assertEquals(PRODUCT_NAME, product.getName());
            assertEquals(PRODUCT_TYPE, product.getProductType());
            assertEquals(WIDTH, product.getSceneRasterWidth());
            assertEquals(HEIGHT, product.getSceneRasterHeight());
            assertEquals(6, product.getNumBands());
            // Band names from header replaced by validated names.
            // BEAM can not handle invalid node names, because there is no possibility to compute
            // bandarithmetic in the cases wher nodenames contains illegal characters.
            final Band band1 = product.getBand("data_molly_AVHRR_samer_SA81jul15a_n07_VIg");
            final Band band2 = product.getBand("data_molly_AVHRR_samer_SA81jul15b_n07_VIg");
            final Band band3 = product.getBand("data_molly_AVHRR_samer_SA81aug15a_n07_VIg");
            final Band band4 = product.getBand("data_molly_AVHRR_samer_SA81aug15b_n07_VIg");
            final Band band5 = product.getBand("data_molly_AVHRR_samer_SA81sep15a_n07_VIg");
            final Band band6 = product.getBand("data_molly_AVHRR_samer_SA81sep15b_n07_VIg");
            assertNotNull(band1);
            assertNotNull(band2);
            assertNotNull(band3);
            assertNotNull(band4);
            assertNotNull(band5);
            assertNotNull(band6);
            assertEquals("non formatted band name: _/data/molly/AVHRR/samer/SA81jul15a.n07-VIg", band1.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81jul15b.n07-VIg_", band2.getDescription());
            assertEquals("", band3.getDescription()); //bandname are valid
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81aug15b.n07-VIg", band4.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81sep15a.n07-VIg", band5.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81sep15b.n07-VIg", band6.getDescription());
        } finally {
            product.dispose();
        }
    }

    @Test
    public void testReadProductNodes_WithInvalidSensingStart() throws IOException {
        // valid DATE_FORMAT_PATTERN = "dd-MMM-yyyy HH:mm:ss";
        final String start = Header.SENSING_START;
        final String stop = Header.SENSING_STOP;
        final String sensingStartStop = start + " = 238746, " + stop + " = 17-feb-1999 6:7:8";
        final String headerContent = createHeaderFileContent(sensingStartStop);
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final EnviProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        try {
            reader.readProductNodes(headerFile, null);
        } catch (IOException e) {
            assertTrue(e.getMessage().contains(Header.SENSING_START));
        }
    }

    @Test
    public void testReadProductNodes_WithInvalidSensingStop() throws IOException {
        // valid DATE_FORMAT_PATTERN = "dd-MMM-yyyy HH:mm:ss";
        final String start = Header.SENSING_START;
        final String stop = Header.SENSING_STOP;
        final String sensingStartStop = start + " = 16-jan-1998 5:6:7, " + stop + " = jkhadsf";
        final String headerContent = createHeaderFileContent(sensingStartStop);
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final EnviProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        try {
            reader.readProductNodes(headerFile, null);
        } catch (IOException e) {
            assertTrue(e.getMessage().contains(Header.SENSING_STOP));
        }
    }

    @Test
    public void testReadProductNodes_WithValidSensingStartStop() throws IOException {
        // valid DATE_FORMAT_PATTERN = "dd-MMM-yyyy HH:mm:ss";
        final String start = Header.SENSING_START;
        final String stop = Header.SENSING_STOP;
        final String sensingStartStop = start + " = 16-jan-1998 5:6:7, " + stop + " = 17-feb-1999 6:7:8";
        final String headerContent = createHeaderFileContent(sensingStartStop);
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final EnviProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        final Product product = reader.readProductNodes(headerFile, null);

        try {
            assertNotNull(product);
            assertEquals(PRODUCT_NAME, product.getName());
            assertEquals(PRODUCT_TYPE, product.getProductType());
            assertEquals(WIDTH, product.getSceneRasterWidth());
            assertEquals(HEIGHT, product.getSceneRasterHeight());
            assertEquals("16-JAN-1998 05:06:07.000000", product.getStartTime().format());
            assertEquals("17-FEB-1999 06:07:08.000000", product.getEndTime().format());
            assertEquals(6, product.getNumBands());
            // Band names from header replaced by validated names.
            // BEAM can not handle invalid node names, because there is no possibility to compute
            // bandarithmetic in the cases wher nodenames contains illegal characters.
            final Band band1 = product.getBand("data_molly_AVHRR_samer_SA81jul15a_n07_VIg");
            final Band band2 = product.getBand("data_molly_AVHRR_samer_SA81jul15b_n07_VIg");
            final Band band3 = product.getBand("data_molly_AVHRR_samer_SA81aug15a_n07_VIg");
            final Band band4 = product.getBand("data_molly_AVHRR_samer_SA81aug15b_n07_VIg");
            final Band band5 = product.getBand("data_molly_AVHRR_samer_SA81sep15a_n07_VIg");
            final Band band6 = product.getBand("data_molly_AVHRR_samer_SA81sep15b_n07_VIg");
            assertNotNull(band1);
            assertNotNull(band2);
            assertNotNull(band3);
            assertNotNull(band4);
            assertNotNull(band5);
            assertNotNull(band6);
            assertEquals("non formatted band name: _/data/molly/AVHRR/samer/SA81jul15a.n07-VIg", band1.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81jul15b.n07-VIg_", band2.getDescription());
            assertEquals("", band3.getDescription()); //bandname are valid
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81aug15b.n07-VIg", band4.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81sep15a.n07-VIg", band5.getDescription());
            assertEquals("non formatted band name: /data/molly/AVHRR/samer/SA81sep15b.n07-VIg", band6.getDescription());

            Band[] allBands = product.getBands();
            for (Band band : allBands) {
                assertTrue(band.isNoDataValueUsed());
                assertEquals(42, band.getNoDataValue(), 1e-9);
            }
            assertEquals(100f, band1.getSpectralWavelength(), 1e-5);
            assertEquals(10f, band1.getSpectralBandwidth(), 1e-5);

            assertEquals(1, product.getIndexCodingGroup().getNodeCount());
            IndexCoding indexCoding = product.getIndexCodingGroup().get(0);
            assertEquals("classification", indexCoding.getName());
            assertArrayEquals(new String[]{"classA", "classB"}, indexCoding.getIndexNames());
        } finally {
            product.dispose();
        }
    }

    @Test
    public void testReadProductNodes_WithoutSensingStop() throws IOException {
        // valid DATE_FORMAT_PATTERN = "dd-MMM-yyyy HH:mm:ss";
        final String sensingStartStop = Header.SENSING_START + " = 16-jan-1998 5:6:7";
        final String headerContent = createHeaderFileContent(sensingStartStop);
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final EnviProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        final Product product = reader.readProductNodes(headerFile, null);

        try {
            assertNotNull(product);
            assertEquals("16-JAN-1998 05:06:07.000000", product.getStartTime().format());
            assertEquals(null, product.getEndTime());
        } finally {
            product.dispose();
        }
    }

    @Test
    public void testImageDataFileConsideredInRightOrder() throws IOException {
        File enviImageFile;
        final File headerFile = new File(tempDir, PRODUCT_NAME + ".hdr");

        new File(tempDir, PRODUCT_NAME + ".bsq").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".bsq", FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME + ".bil").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".bil", FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME + ".bip").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".bip", FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME + ".bin").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".bin", FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME + ".img").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".img", FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME).createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(null, FileUtils.getExtension(enviImageFile));

        new File(tempDir, PRODUCT_NAME + ".dat").createNewFile();
        enviImageFile = EnviProductReader.getEnviImageFile(headerFile);
        assertEquals(".dat", FileUtils.getExtension(enviImageFile));
    }

    @Test
    public void testMetadata() throws IOException {
        final String headerContent = createHeaderFileContent("");
        final File headerFile = createHeaderAndImageFile(headerContent, PRODUCT_NAME);

        final EnviProductReaderPlugIn plugIn = new EnviProductReaderPlugIn();
        final ProductReader reader = plugIn.createReaderInstance();
        final Product product = reader.readProductNodes(headerFile, null);

        try {
            assertNotNull(product);
            MetadataElement metadataRoot = product.getMetadataRoot();
            assertEquals(1, metadataRoot.getNumElements());
            MetadataElement headerElem = metadataRoot.getElementAt(0);
            assertNotNull(headerElem);
            assertEquals("Header", headerElem.getName());
            String[] attributeNames = headerElem.getAttributeNames();
            String[] expected = new String[]{
                    "description", "samples", "lines", "bands", "header offset", "file type",
                    "data type", "interleave", "sensor type", "byte order", "data ignore value", "map info",
                    "projection info", "wavelength", "fwhm", "wavelength units", "band names",
                    "classes", "class lookup", "class names"
            };
            assertArrayEquals(expected, attributeNames);
        } finally {
            product.dispose();
        }
    }

    private String createHeaderFileContent(final String sensingStartStop) {
        final StringWriter writer = new StringWriter();
        final PrintWriter pw = new PrintWriter(writer);
        pw.println(EnviConstants.FIRST_LINE);
        pw.println("description = {");
        pw.print("\tENVI File \tCreated [Wed Feb 23 09:36:22 2005]");
        if (sensingStartStop.length() > 0) {
            pw.print("\t" + Header.BEAM_PROPERTIES + " = [" + sensingStartStop + "]");
        }
        pw.println("}");
        pw.println(EnviConstants.HEADER_KEY_SAMPLES + " = " + WIDTH);
        pw.println(EnviConstants.HEADER_KEY_LINES + " = " + HEIGHT);
        pw.println(EnviConstants.HEADER_KEY_BANDS + " = " + NUM_BANDS);
        pw.println(EnviConstants.HEADER_KEY_HEADER_OFFSET + " = " + HEADER_OFFSET);
        pw.println(EnviConstants.HEADER_KEY_FILE_TYPE + " = " + FILE_TYPE);
        pw.println(EnviConstants.HEADER_KEY_DATA_TYPE + " = " + DATA_TYPE);
        pw.println(EnviConstants.HEADER_KEY_INTERLEAVE + " = " + INTERLEAVE);
        pw.println(EnviConstants.HEADER_KEY_SENSOR_TYPE + " = " + SENSOR_TYPE);
        pw.println(EnviConstants.HEADER_KEY_BYTE_ORDER + " = " + BYTE_ORDER);
        pw.println(EnviConstants.HEADER_KEY_DATA_IGNORE_VALUE + " = 42");
        pw.println();
        pw.println(EnviConstants.HEADER_KEY_MAP_INFO + " = {" + MAP_INFO + "}");
        pw.println(EnviConstants.HEADER_KEY_PROJECTION_INFO + " ={" + PROJECTION_INFO + "}");
        pw.println(EnviConstants.HEADER_KEY_WAVELENGTH + " = {100,200,300,400,500,600}");
        pw.println(EnviConstants.HEADER_KEY_FWHM + " = {10,20,30,30,20,10}");
        pw.println(EnviConstants.HEADER_KEY_WAVELENGTH_UNITS + " = " + WAVELENGTH_UNITS);
        pw.println(EnviConstants.HEADER_KEY_BAND_NAMES + " = {");
        pw.println(" _/data/molly/AVHRR/samer/SA81jul15a.n07-VIg,");
        pw.println(" /data/molly/AVHRR/samer/SA81jul15b.n07-VIg_,");
        pw.println(" data_molly_AVHRR_samer_SA81aug15a_n07_VIg,");
        pw.println(" /data/molly/AVHRR/samer/SA81aug15b.n07-VIg,");
        pw.println(" /data/molly/AVHRR/samer/SA81sep15a.n07-VIg,");
        pw.println(" /data/molly/AVHRR/samer/SA81sep15b.n07-VIg }");
        pw.println(EnviConstants.HEADER_KEY_CLASSES + " = 2");
        pw.println(EnviConstants.HEADER_KEY_CLASS_LOOKUP + " = {0,   0,   0, 255,   0,   0}");
        pw.println(EnviConstants.HEADER_KEY_CLASS_NAMES + " = {classA, classB}");
        pw.flush();
        return writer.toString();
    }

    private File createHeaderAndImageFile(final String headerContent, String headerFileName) throws IOException {
        final File headerFile = new File(tempDir, headerFileName + ".hdr");
        final File imageFile = new File(tempDir, headerFileName + ".img");
        assertTrue(headerFile.createNewFile());
        assertTrue(imageFile.createNewFile());

        final FileOutputStream outputStream = new FileOutputStream(headerFile);
        outputStream.write(headerContent.getBytes());
        outputStream.close();
        return headerFile;
    }
}
