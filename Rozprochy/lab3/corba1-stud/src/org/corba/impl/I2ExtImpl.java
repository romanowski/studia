package org.corba.impl;

import org.corba.generated.I2ExtPOA;
import org.corba.generated.I2POA;

public class I2ExtImpl extends I2ExtPOA
{

	@Override
	public int opExt() {
		System.out.println("I2Ext::opExt");
		return 123000;
	}

	@Override
	public int op() {
		return 1;
	}

}
