/***************************************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.help.internal.xhtml;

import org.eclipse.help.internal.util.StringUtil;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;


public abstract class AbstractUAElement {

	private Bundle bundle;

	AbstractUAElement() {
	}


	/**
	 * Constructor used when model elements are being loaded from an xml content file. Bundle is
	 * propagated down the model to enable resolving resources relative to the base of the bundle.
	 * 
	 * @param element
	 * @param pd
	 */
	AbstractUAElement(Element element, Bundle bundle) {
		this.bundle = bundle;
	}



	/**
	 * DOM getAttribute retruns an empty string (not null) if attribute is not defined. Override
	 * this behavior.
	 * 
	 * @param element
	 * @param att
	 * @return
	 */
	protected String getAttribute(Element element, String att) {
		if (element.hasAttribute(att))
			return element.getAttribute(att);
		return null;
	}

	/**
	 * Util method to parse a comma separated list of values
	 * 
	 * @param element
	 * @param att
	 * @return
	 */
	protected String[] getAttributeList(Element element, String att) {
		if (element.hasAttribute(att))
			return StringUtil.split(element.getAttribute(att), ","); //$NON-NLS-1$
		return null;
	}


	/**
	 * Returns the plugin descriptor of the plugin from which this element was loaded. In the case
	 * of extension, returns the plugin descriptor of the plugin defining the extension.
	 * 
	 * @return
	 */
	public Bundle getBundle() {
		return bundle;
	}




}
