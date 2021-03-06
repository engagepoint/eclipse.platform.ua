/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ua.tests.cheatsheet.execution;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit tests for the classes which execute commands and actions from a cheatsheet
 */
public class AllExecutionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"org.eclipse.ua.tests.cheatsheet.AllExecutionTests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestVariableSubstitution.class);
		suite.addTestSuite(TestActionExecution.class);
		suite.addTestSuite(TestCommandExecution.class);
		//$JUnit-END$
		return suite;
	}

}
