package org.esa.beam.visat.toolviews.layermanager.layersrc.product;


import com.bc.ceres.binding.ValueContainer;
import com.bc.ceres.glayer.Layer;
import com.bc.ceres.glayer.LayerType;
import com.bc.ceres.glayer.support.ImageLayer;
import com.jidesoft.tree.AbstractTreeModel;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductManager;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.product.ProductSceneView;
import org.esa.beam.glayer.RasterImageLayerType;
import org.esa.beam.visat.toolviews.layermanager.layersrc.AbstractLayerSourceAssistantPage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class ProductLayerAssistantPage extends AbstractLayerSourceAssistantPage {

    private JTree tree;

    ProductLayerAssistantPage() {
        super("Select Band / Tie-Point Grid");
    }

    @Override
    public Component createPageComponent() {
        ProductTreeModel model = createTreeModel(getContext().getAppContext());
        tree = new JTree(model);
        tree.setEditable(false);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setCellRenderer(new ProductNodeTreeCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(new ProductNodeSelectionListener());

        List<CompatibleNodeList> nodeLists = model.compatibleNodeLists;
        for (CompatibleNodeList nodeList : nodeLists) {
            tree.expandPath(new TreePath(new Object[]{nodeLists, nodeList}));
        }

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));
        panel.add(new JLabel("Compatible bands and tie-point grids:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tree), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public boolean validatePage() {
        TreePath path = tree.getSelectionPath();
        return path != null && path.getLastPathComponent() instanceof RasterDataNode;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public boolean canFinish() {
        return true;
    }

    @Override
    public boolean performFinish() {
        final RasterDataNode rasterDataNode = (RasterDataNode) tree.getSelectionPath().getLastPathComponent();

        LayerType type = LayerType.getLayerType(RasterImageLayerType.class.getName());
        ValueContainer configuration = type.createLayerConfig(getContext().getLayerContext());
        configuration.setValue(RasterImageLayerType.PROPERTY_NAME_RASTER, rasterDataNode);
        final GeoCoding geoCoding = rasterDataNode.getGeoCoding();
        AffineTransform i2mTransform = new AffineTransform();
        if (geoCoding != null) {
            i2mTransform = geoCoding.getImageToModelTransform();
        }
        configuration.setValue(ImageLayer.PROPERTY_NAME_IMAGE_TO_MODEL_TRANSFORM, i2mTransform);
        configuration.setValue(ImageLayer.PROPERTY_NAME_BORDER_SHOWN, false);
        configuration.setValue(ImageLayer.PROPERTY_NAME_BORDER_COLOR, ImageLayer.DEFAULT_BORDER_COLOR);
        configuration.setValue(ImageLayer.PROPERTY_NAME_BORDER_WIDTH, ImageLayer.DEFAULT_BORDER_WIDTH);
        final ImageLayer imageLayer = (ImageLayer) type.createLayer(getContext().getLayerContext(),
                                                                    configuration);
        imageLayer.setName(rasterDataNode.getDisplayName());

        ProductSceneView sceneView = getContext().getAppContext().getSelectedProductSceneView();
        Layer rootLayer = sceneView.getRootLayer();
        rootLayer.getChildren().add(sceneView.getFirstImageLayerIndex(), imageLayer);

        final LayerDataHandler layerDataHandler = new LayerDataHandler(rasterDataNode, imageLayer);
        rasterDataNode.getProduct().addProductNodeListener(layerDataHandler);
        rootLayer.addListener(layerDataHandler);

        return true;
    }

    private static class CompatibleNodeList {

        private final String name;
        private final List<RasterDataNode> rasterDataNodes;

        CompatibleNodeList(String name, List<RasterDataNode> rasterDataNodes) {
            this.name = name;
            this.rasterDataNodes = rasterDataNodes;
        }
    }

    private ProductTreeModel createTreeModel(AppContext ctx) {
        Product selectedProduct = ctx.getSelectedProductSceneView().getProduct();
        RasterDataNode raster = ctx.getSelectedProductSceneView().getRaster();
        GeoCoding geoCoding = raster.getGeoCoding();
        CoordinateReferenceSystem modelCRS = geoCoding != null ? geoCoding.getModelCRS() : null;

        ArrayList<CompatibleNodeList> compatibleNodeLists = new ArrayList<CompatibleNodeList>(3);

        List<RasterDataNode> compatibleNodes = new ArrayList<RasterDataNode>();
        compatibleNodes.addAll(Arrays.asList(selectedProduct.getBands()));
        compatibleNodes.addAll(Arrays.asList(selectedProduct.getTiePointGrids()));
        if (!compatibleNodes.isEmpty()) {
            compatibleNodeLists.add(new CompatibleNodeList(selectedProduct.getDisplayName(), compatibleNodes));
        }

        if (modelCRS != null) {
            final ProductManager productManager = ctx.getProductManager();
            final Product[] products = productManager.getProducts();
            for (Product product : products) {
                if (product == selectedProduct) {
                    continue;
                }
                compatibleNodes = new ArrayList<RasterDataNode>();
                collectCompatibleRasterDataNodes(product.getBands(), modelCRS, compatibleNodes);
                collectCompatibleRasterDataNodes(product.getTiePointGrids(), modelCRS, compatibleNodes);
                if (!compatibleNodes.isEmpty()) {
                    compatibleNodeLists.add(new CompatibleNodeList(product.getDisplayName(), compatibleNodes));
                }
            }
        }
        return new ProductTreeModel(compatibleNodeLists);
    }

    private void collectCompatibleRasterDataNodes(RasterDataNode[] bands, CoordinateReferenceSystem crs,
                                                  Collection<RasterDataNode> rasterDataNodes) {
        for (RasterDataNode node : bands) {
            GeoCoding geoCoding = node.getGeoCoding();
            if (geoCoding != null && node.getGeoCoding().getModelCRS().equals(crs)) {
                rasterDataNodes.add(node);
            }
        }
    }

    private static class ProductNodeTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof CompatibleNodeList) {
                label.setText(MessageFormat.format("<html><b>{0}</b></html>", ((CompatibleNodeList) value).name));
            } else if (value instanceof Band) {
                label.setText(MessageFormat.format("<html><b>{0}</b></html>", ((Band) value).getName()));
            } else if (value instanceof TiePointGrid) {
                label.setText(MessageFormat.format("<html><b>{0}</b> (Tie-point grid)</html>",
                                                   ((TiePointGrid) value).getName()));
            }
            return label;
        }
    }

    private class ProductNodeSelectionListener implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            getContext().updateState();
        }
    }

    private static class ProductTreeModel extends AbstractTreeModel {

        private final List<CompatibleNodeList> compatibleNodeLists;

        private ProductTreeModel(List<CompatibleNodeList> compatibleNodeLists) {
            this.compatibleNodeLists = compatibleNodeLists;
        }

        @Override
        public Object getRoot() {
            return compatibleNodeLists;
        }

        @Override
        public Object getChild(Object parent, int index) {
            if (parent == compatibleNodeLists) {
                return compatibleNodeLists.get(index);
            } else if (parent instanceof CompatibleNodeList) {
                return ((CompatibleNodeList) parent).rasterDataNodes.get(index);
            }
            return null;
        }

        @Override
        public int getChildCount(Object parent) {
            if (parent == compatibleNodeLists) {
                return compatibleNodeLists.size();
            } else if (parent instanceof CompatibleNodeList) {
                return ((CompatibleNodeList) parent).rasterDataNodes.size();
            }
            return 0;
        }

        @Override
        public boolean isLeaf(Object node) {
            return node instanceof RasterDataNode;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent == compatibleNodeLists) {
                return compatibleNodeLists.indexOf(child);
            } else if (parent instanceof CompatibleNodeList) {
                return ((CompatibleNodeList) parent).rasterDataNodes.indexOf(child);
            }
            return -1;
        }
    }
}