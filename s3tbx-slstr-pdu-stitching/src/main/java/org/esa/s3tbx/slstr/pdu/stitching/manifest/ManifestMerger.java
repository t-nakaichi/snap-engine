package org.esa.s3tbx.slstr.pdu.stitching.manifest;

import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import org.esa.s3tbx.slstr.pdu.stitching.PDUStitchingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Tonio Fincke
 */
public class ManifestMerger {

    private static final String[] discerningAttributesNames = {"ID", "name", "grid", "view", "element", "type", "role"};
    private Date creationTime;
    private static DefaultMerger defaultMerger;
    private static final ElementMerger NULL_MERGER = new NullMerger();
    private String productName;

    public Document mergeManifests(File[] manifestFiles, Date creationTime, String productName) throws IOException, PDUStitchingException, ParserConfigurationException {
        this.creationTime = creationTime;
        this.productName = productName;
        List<Node> manifestList = new ArrayList<>();
        for (File manifestFile : manifestFiles) {
            manifestList.add(createXmlDocument(new FileInputStream(manifestFile)));
        }
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        defaultMerger = new DefaultMerger();
        defaultMerger.mergeNodes(manifestList, document, document);
        return document;
    }

//    void setChecksum(Node toParent) throws PDUStitchingException {
    //todo get full path to file
//        Node sibling = toParent;
//        try {
//            while (!sibling.getNodeName().equals("fileLocation")) {
//                sibling = sibling.getPreviousSibling();
//
//            }
//            final NamedNodeMap attributes = sibling.getAttributes();
//            if (attributes != null) {
//                final Node hrefAttribute = attributes.getNamedItem("href");
//                if (hrefAttribute != null) {
//                    final String fileName = hrefAttribute.getNodeValue();
//                    final MessageDigest md5 = MessageDigest.getInstance("MD5");
//                    final DigestInputStream digestInputStream = new DigestInputStream(Files.newInputStream(Paths.get(fileName)), md5);
//                    digestInputStream.read();
//                    toParent.setNodeValue(new String(md5.digest()));
//                }
//            }
//        } catch (NullPointerException | NoSuchAlgorithmException | IOException npe) {
//            throw new PDUStitchingException("Could not create checksum");
//        }
//        toParent.setNodeValue("");
//    }

    private static Document createXmlDocument(InputStream inputStream) throws IOException {
        final String msg = "Cannot create document from manifest XML file.";
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(msg, e);
        }
    }

    private ElementMerger getElementMerger(String elementName) {
        if (elementName.equals("checksum")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("slstr:nadirImageSize") ||
                elementName.equals("slstr:obliqueImageSize")) {
            return new ImageSizesMerger();
        } else if (elementName.equals("sentinel-safe:startTime")) {
            return new StartTimesMerger();
        } else if (elementName.equals("sentinel-safe:stopTime")) {
            return new StopTimesMerger();
        } else if (elementName.equals("slstr:classificationSummary")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("slstr:pixelQualitySummary")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("slstr:missingElements")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("sentinel-safe:footPrint")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("sentinel3:creationTime")) {
            return new CreationTimeMerger(creationTime);
        } else if (elementName.equals("sentinel3:granuleNumber")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("sentinel3:productName")) {
            return new ProductNameMerger(productName);
        } else if (elementName.equals("sentinel3:receivingStartTime")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("sentinel3:receivingStopTime")) {
            //todo implement
            return NULL_MERGER;
        } else if (elementName.equals("slstr:classificationSummary")) {
            //todo implement
            return NULL_MERGER;
        }
        return defaultMerger;
    }


    private class DefaultMerger extends AbstractElementMerger {

        @Override
        public void mergeNodes(List<Node> fromParents, Element toParent, Document toDocument) throws PDUStitchingException {
            mergeNodes(fromParents, (Node) toParent, toDocument);
        }

        public void mergeNodes(List<Node> fromParents, Node toParent, Document toDocument) throws PDUStitchingException {
            NodeList[] childNodeLists = new NodeList[fromParents.size()];
            for (int i = 0; i < childNodeLists.length; i++) {
                childNodeLists[i] = fromParents.get(i).getChildNodes();
            }
            for (int j = 0; j < fromParents.size(); j++) {
                for (int i = 0; i < childNodeLists[j].getLength(); i++) {
                    final Node child = childNodeLists[j].item(i);
                    if (child instanceof TextImpl && child.getTextContent().contains("\n")) {
                        final Node lastChild = toParent.getLastChild();
                        if (!(lastChild instanceof TextImpl)) {
                            final String textContent = child.getTextContent();
                            final Text textNode = toDocument.createTextNode(textContent);
                            toParent.appendChild(textNode);
                        } else if (!lastChild.getTextContent().contains("\n")) {
                            final String textContent = child.getTextContent();
                            final Text textNode = toDocument.createTextNode(textContent);
                            toParent.appendChild(textNode);
                        }
                    } else {
                        if (!hasIdenticalChild(toParent, child)) {
                            final String nodeValue = child.getNodeValue();
                            List<Node> childNodes = collectChildNodes(child, childNodeLists, j);
                            if (child instanceof DeferredTextImpl) {
                                final String textContent = child.getTextContent();
                                final Text textNode = toDocument.createTextNode(textContent);
                                toParent.appendChild(textNode);
                            } else {
                                final Element manifestElement = toDocument.createElement(child.getNodeName());
                                manifestElement.setNodeValue(nodeValue);
                                copyAttributes(childNodes, manifestElement);
                                toParent.appendChild(manifestElement);
                                mergeChildNodes(childNodes, manifestElement, toDocument);
                            }
                        }
                    }
                }
            }
        }

        //package local for testing
        void mergeChildNodes(List<Node> fromParents, Element toParent, Document toDocument) throws PDUStitchingException {
            final ElementMerger elementMerger = getElementMerger(toParent.getNodeName());
            elementMerger.mergeNodes(fromParents, toParent, toDocument);
        }

        private List<Node> collectChildNodes(Node child, NodeList[] childNodeLists, int indexOfCurrentParent)
                throws PDUStitchingException {
            List<Node> itemNodes = new ArrayList<>();
            itemNodes.add(child);
            //todo handle this more gracefully -> own elementmerger?
            if (child.getNodeName().equals("sentinel-safe:orbitReference")) {
                return itemNodes;
            }
            final String nodeValue = child.getNodeValue();
            if (indexOfCurrentParent < childNodeLists.length - 1) {
                for (int k = indexOfCurrentParent + 1; k < childNodeLists.length; k++) {
                    for (int l = 0; l < childNodeLists[k].getLength(); l++) {
                        if (childNodeLists[k].item(l).getNodeName().equals(child.getNodeName())) {
                            boolean discerningAttributesAreDifferent = false;
                            final NamedNodeMap attributes = child.getAttributes();
                            final NamedNodeMap otherAttributes = childNodeLists[k].item(l).getAttributes();
                            if (attributes != null && otherAttributes != null) {
                                for (String name : discerningAttributesNames) {
                                    final Node attributeToBeChecked = attributes.getNamedItem(name);
                                    final Node attribute = otherAttributes.getNamedItem(name);
                                    if (attributeToBeChecked != null && attribute != null &&
                                            !attributeToBeChecked.getNodeValue().equals(attribute.getNodeValue())) {
                                        discerningAttributesAreDifferent = true;
                                    }
                                }
                            }
                            if (!discerningAttributesAreDifferent) {
                                final String otherNodeValue = childNodeLists[k].item(l).getNodeValue();
                                if ((otherNodeValue != null && nodeValue == null) ||
                                        (otherNodeValue == null && nodeValue != null) ||
                                        (otherNodeValue != null && !otherNodeValue.trim().equals(nodeValue.trim()))) {
                                    throw new PDUStitchingException("Different values for node " + child.getParentNode().getNodeName() + ": "
                                                                            + otherNodeValue + ", " + nodeValue);
                                } else {
                                    itemNodes.add(childNodeLists[k].item(l));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return itemNodes;
        }

        private boolean hasIdenticalChild(Node node, Node newNode) {
            if (newNode.getNodeName().equals("sentinel-safe:ephemeris")) {
                return false;
            }
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                final Node nodeToBeChecked = node.getChildNodes().item(i);
                if (nodeToBeChecked.getNodeName().equals(newNode.getNodeName())) {
                    final NamedNodeMap nodeToBeCheckedAttributes = nodeToBeChecked.getAttributes();
                    final NamedNodeMap attributes = newNode.getAttributes();
                    if (nodeToBeCheckedAttributes != null && attributes != null) {
                        for (String name : discerningAttributesNames) {
                            final Node attributeToBeChecked = nodeToBeCheckedAttributes.getNamedItem(name);
                            final Node attribute = attributes.getNamedItem(name);
                            if (attributeToBeChecked != null && attribute != null &&
                                    !attributeToBeChecked.getNodeValue().equals(attribute.getNodeValue())) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }

        private void copyAttributes(List<Node> itemNodes, Element manifestElement) throws PDUStitchingException {
            for (int n = 0; n < itemNodes.size(); n++) {
                final Node currentItem = itemNodes.get(n);
                final NamedNodeMap attributes = currentItem.getAttributes();
                if (attributes != null) {
                    for (int k = 0; k < attributes.getLength(); k++) {
                        final Node attribute = attributes.item(k);
                        final String attributeName = attribute.getNodeName();
                        if (!manifestElement.hasAttribute(attributeName)) {
                            String attributeValue = attribute.getNodeValue();
                            if (n < itemNodes.size() - 1) {
                                for (int m = n + 1; m < itemNodes.size(); m++) {
                                    final NamedNodeMap otherItemAttributes = itemNodes.get(m).getAttributes();
                                    final Node otherAttribute = otherItemAttributes.getNamedItem(attributeName);
                                    if (otherAttribute != null) {
                                        final String otherAttributeValue = otherAttribute.getNodeValue();
                                        if (attributeName.equals("start")) {
                                            final Date startValue = parseDate(attributeValue);
                                            final Date otherStartValue = parseDate(otherAttributeValue);
                                            if (otherStartValue.before(startValue)) {
                                                attributeValue = otherAttributeValue;
                                            }
                                        } else if (attributeName.equals("stop")) {
                                            final Date startValue = parseDate(attributeValue);
                                            final Date otherStartValue = parseDate(otherAttributeValue);
                                            if (otherStartValue.after(startValue)) {
                                                attributeValue = otherAttributeValue;
                                            }
                                        } else if (!otherAttributeValue.equals(attributeValue)) {
                                            throw new PDUStitchingException("Different values for attribute " + attributeName +
                                                                                    " of node " + currentItem.getNodeName());
                                        }
                                    }
                                }
                            }
                            manifestElement.setAttribute(attributeName, attributeValue.trim());
                        }
                    }
                }
            }
        }
    }

    private static class NullMerger implements ElementMerger {

        @Override
        public void mergeNodes(List<Node> fromParents, Element toParent, Document toDocument) throws PDUStitchingException {
        }
    }

}