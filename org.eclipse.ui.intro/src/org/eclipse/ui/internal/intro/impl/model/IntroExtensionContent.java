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

package org.eclipse.ui.internal.intro.impl.model;

import java.util.*;

import org.eclipse.ui.internal.intro.impl.model.loader.*;
import org.osgi.framework.*;
import org.w3c.dom.*;

/**
 * An intro container extension. If the content attribute is defined, then it is
 * assumed that we have XHTML content in an external file. Load content from
 * external DOM. No need to worry about caching here because this is a transient
 * model class. It is used and then disregarded from the model.
 */
public class IntroExtensionContent extends AbstractIntroElement {

    protected static final String TAG_CONTAINER_EXTENSION = "extensionContent"; //$NON-NLS-1$

    protected static final String ATT_PATH = "path"; //$NON-NLS-1$
    private static final String ATT_STYLE = "style"; //$NON-NLS-1$
    private static final String ATT_ALT_STYLE = "alt-style"; //$NON-NLS-1$
    private static final String ATT_CONTENT = "content"; //$NON-NLS-1$

    private String path;
    private String style;
    private String altStyle;
    private String content;

    private Element element;

    IntroExtensionContent(Element element, Bundle bundle) {
        super(element, bundle);
        path = getAttribute(element, ATT_PATH);
        style = getAttribute(element, ATT_STYLE);
        altStyle = getAttribute(element, ATT_ALT_STYLE);
        content = getAttribute(element, ATT_CONTENT);
        this.element = element;

        // Resolve.
        style = BundleUtil.getResolvedBundleLocation(style, bundle);
        altStyle = BundleUtil.getResolvedBundleLocation(altStyle, bundle);
        // if content is not null we have XHTML extension.
        content = BundleUtil.getResolvedBundleLocation(content, bundle);
    }

    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.intro.impl.model.IntroElement#getType()
     */
    public int getType() {
        return AbstractIntroElement.CONTAINER_EXTENSION;
    }

    protected Element[] getChildren() {
        Vector children = new Vector();
        NodeList nodeList = element.getChildNodes();
        Vector vector = new Vector();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                vector.add(node);
        }
        Element[] filteredElements = new Element[vector.size()];
        vector.copyInto(filteredElements);
        // free DOM model for memory performance.
        this.element = null;
        return filteredElements;
    }

    public boolean isXHTMLContent() {
        return content != null ? true : false;
    }

    public Document getDocument() {
        if (isXHTMLContent()) {
            IntroContentParser parser = new IntroContentParser(content);
            Document dom = parser.getDocument();
            if (dom == null)
                // bad xml. Parser would have logged fact.
                return null;
            // parser content should be XHTML because defining content here
            // means that we want XHTML extension.
            if (parser.hasXHTMLContent())
                return dom;

        }
        return null;
    }

    /**
     * @return Returns the altStyle.
     */
    protected String getAltStyle() {
        return altStyle;
    }

    /**
     * @return Returns the style.
     */
    protected String getStyle() {
        return style;
    }

    /**
     * @return Returns the content.
     */
    public String getContent() {
        return content;
    }
}