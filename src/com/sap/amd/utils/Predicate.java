package com.sap.amd.utils;

public interface Predicate<V>
{	
	Boolean test(V...variables);
}
