/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.intro.impl.model.loader;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.eclipse.ui.internal.intro.impl.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 *  
 */
public class IntroContentParser {

    private static String TAG_INTRO_CONTENT = "introContent"; //$NON-NLS-1$
    private static String TAG_HTML = "html"; //$NON-NLS-1$
    private static String XHTML1_TRANSITIONAL = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"; //$NON-NLS-1$
    private static String XHTML1_STRICT = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"; //$NON-NLS-1$
    private static String XHTML1_FRAMESET = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd"; //$NON-NLS-1$

    /*
     * Load XHTML dtds from intro plugin location.
     */
    private static Hashtable dtdMap = new Hashtable();
    {
        // INTRO: hack for now. DTDs should be picked up from intro plugin.
        String dtdBaseLocation = System.getProperty("dtdBaseLocation"); //$NON-NLS-1$
        if (dtdBaseLocation == null)
            dtdBaseLocation = ""; //$NON-NLS-1$
        String dtdLocation = dtdBaseLocation
                + "/xhtml1-20020801/DTD/xhtml1-transitional.dtd"; //$NON-NLS-1$
        //  String dtdLocation = BundleUtil.getResolvedBundleLocation(
        //        "xhtml1-20020801/DTD/xhtml1-transitional.dtd",
        //      IIntroConstants.PLUGIN_ID);
        dtdMap.put(XHTML1_TRANSITIONAL, dtdLocation);

        dtdLocation = dtdBaseLocation
                + "/xhtml1-20020801/DTD/xhtml1-strict.dtd"; //$NON-NLS-1$
        //    dtdLocation = BundleUtil.getResolvedBundleLocation(
        //          "xhtml1-20020801/DTD/xhtml1-strict.dtd",
        //          IIntroConstants.PLUGIN_ID);
        dtdMap.put(XHTML1_STRICT, dtdLocation);

        dtdLocation = dtdBaseLocation
                + "/xhtml1-20020801/DTD/xhtml1-frameset.dtd"; //$NON-NLS-1$
        //  dtdLocation = BundleUtil.getResolvedBundleLocation(
        //           "xhtml1-20020801/DTD/xhtml1-frameset.dtd",
        //          IIntroConstants.PLUGIN_ID);
        dtdMap.put(XHTML1_FRAMESET, dtdLocation);
    }


    private Document document;
    private boolean hasXHTMLContent;

    /**
     * Creates a config parser assuming that the passed content represents a URL
     * to the content file.
     */
    public IntroContentParser(String content) {
        try {
            document = parse(content);
            if (document != null) {
                // xml file is loaded. It can be either XHTML or intro XML.
                Element rootElement = document.getDocumentElement();
                // DocumentType docType = document.getDoctype();
                if (rootElement.getTagName().equals(TAG_INTRO_CONTENT)) {
                    // intro xml file.
                    hasXHTMLContent = false;
                }
                // rely on root element to detect if we have an XHTML file and
                // not on doctype. We need to support xhtml files with no
                // doctype.
                else if (rootElement.getTagName().equals(TAG_HTML)) {
                    // an XHTML content file.
                    hasXHTMLContent = true;
                } else
                    // not XML nor XHTML.
                    document = null;

            }
        } catch (Exception e) {
            Log.error("Could not load Intro content file: " + content, e); //$NON-NLS-1$
        }
    }


    private Document parse(String fileURI) {
        Document document = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            docFactory.setValidating(false);
            docFactory.setNamespaceAware(true);
            docFactory.setExpandEntityReferences(false);
            DocumentBuilder parser = docFactory.newDocumentBuilder();

            parser.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    if (systemId.equals(XHTML1_TRANSITIONAL)
                            || systemId.equals(XHTML1_STRICT)
                            || systemId.equals(XHTML1_FRAMESET))
                        return new InputSource((String) dtdMap.get(systemId));

                    else
                        return null;
                }
            });
            document = parser.parse(fileURI);
            return document;

        } catch (SAXParseException spe) {
            StringBuffer buffer = new StringBuffer("IntroParser error in line "); //$NON-NLS-1$
            buffer.append(spe.getLineNumber());
            buffer.append(", uri "); //$NON-NLS-1$
            buffer.append(spe.getSystemId());
            buffer.append("\n"); //$NON-NLS-1$
            buffer.append(spe.getMessage());

            // Use the contained exception.
            Exception x = spe;
            if (spe.getException() != null)
                x = spe.getException();
            Log.error(buffer.toString(), x);

        } catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            Log.error(x.getMessage(), x);

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            Log.error(pce.getMessage(), pce);

        } catch (IOException ioe) {
            Log.error(ioe.getMessage(), ioe);
        }
        return null;
    }


    /**
     * Returned the DOM representing the intro xml content file. May return null
     * if parsing the file failed.
     * 
     * @return Returns the document.
     */
    public Document getDocument() {
        return document;
    }

    public boolean hasXHTMLContent() {
        return hasXHTMLContent;
    }


    public static String convertToString(Document document) {
        try {
            // identity xslt.
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);

            StringWriter stringBuffer = new StringWriter();
            StreamResult result = new StreamResult(stringBuffer);

            // setup properties, for doctype.
            DocumentType docType = document.getDoctype();
            if (docType != null) {
                String value = docType.getSystemId();
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, value);
                value = document.getDoctype().getPublicId();
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, value);
                transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                        "yes"); //$NON-NLS-1$
                //transformer.setOutputProperty(OutputKeys.MEDIA_TYPE,
                //        "text/html");
                //transformer
                //       .setOutputProperty(OutputKeys.ENCODING, "iso-8859-1");
            }
            transformer.transform(source, result);
            return stringBuffer.toString();

        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            Log.error("Transformer Config error: " + tce.getMessage(), null); //$NON-NLS-1$
            // Use the contained exception, if any
            Throwable x = tce;
            if (tce.getException() != null)
                x = tce.getException();
            Log.error("Transformer Stack trace: ", x); //$NON-NLS-1$

        } catch (TransformerException te) {
            // Error generated by the parser
            Log.error("Transformer error: " + te.getMessage(), te); //$NON-NLS-1$
            // Use the contained exception, if any
            Throwable x = te;
            if (te.getException() != null)
                x = te.getException();
            Log.error("Transformer Stack trace: ", x); //$NON-NLS-1$

        }
        return null;

    }



}