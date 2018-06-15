package com.example.chen.hsms.webservice;

import org.ksoap2.serialization.SoapObject;

public interface ParseData {
	public abstract Object doing(SoapObject result, @SuppressWarnings("rawtypes") Class cla)
			throws NoSuchFieldException, IllegalAccessException,
			InstantiationException, ClassNotFoundException;
}
