/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ua.tests.help.remote;

import org.eclipse.core.runtime.Platform;
import org.eclipse.help.internal.server.JettyHelpServer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class JettyTestServer extends JettyHelpServer {
	
	protected String getOtherInfo() {
		return "org.eclipse.ua.tests";
	}
	
	protected int getPortParameter() {
		return AUTO_SELECT_JETTY_PORT;
	}
	
	/*
	 * Ensures that the bundle with the specified name and the highest available
	 * version is started and reads the port number
	 */
	protected void checkBundle() throws InvalidSyntaxException, BundleException {
		Bundle bundle = Platform.getBundle("org.eclipse.equinox.http.registry"); //$NON-NLS-1$if (bundle != null) {
		if (bundle.getState() == Bundle.RESOLVED) {
			bundle.start(Bundle.START_TRANSIENT);
		}
		if (port == -1) {
			// Jetty selected a port number for us
			ServiceReference[] reference = bundle.getBundleContext().getServiceReferences("org.osgi.service.http.HttpService", "(other.info=" + getOtherInfo() + ')'); //$NON-NLS-1$ //$NON-NLS-2$
			Object assignedPort = reference[reference.length - 1].getProperty("http.port"); //$NON-NLS-1$
			port = Integer.parseInt((String)assignedPort);
		}
	}
}
