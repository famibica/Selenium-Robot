package com.sap.amd.utils.annotations;

public @interface Request
{
	String[] properties() default {};
}
