package com.sap.amd.bcpandicp;

import java.io.Serializable;

public interface Entity<T> extends Serializable
{
	public boolean isEqualTo(T entity);
	public String getName();
}
