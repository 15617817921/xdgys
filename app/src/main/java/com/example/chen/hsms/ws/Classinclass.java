package com.example.chen.hsms.ws;

import com.example.chen.hsms.webservice.ParseData;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;


public class Classinclass implements ParseData {

	@Override
	public Object doing(SoapObject result, @SuppressWarnings("rawtypes") Class cla)
			throws NoSuchFieldException, IllegalAccessException,
			InstantiationException, ClassNotFoundException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < result.getPropertyCount(); i++) {
			Object object = (Object)result.getProperty(i);
			//SoapObject result1 = (SoapObject) result.getProperty(i);
			//System.out.println(result.toString());
			//Object obj = useFanSheToData(result1, cla.newInstance());
			list.add(object);
		}
		return list;
	}
/**
 * �����������ֵ
 * @param documentElement
 * @param object
 * @return
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 * @throws InstantiationException
 * @throws ClassNotFoundException
 */
	private Object useFanSheToData(SoapObject documentElement, Object object)
			throws NoSuchFieldException, IllegalAccessException,
			InstantiationException, ClassNotFoundException {
		// ���÷�����ำֵ
		FanShe fanShe = new FanShe();
		// ����������
		List<String> classFileList = new ArrayList<String>();
		// ����������ֵ
		List<Object> classValueList = new ArrayList<Object>();
		// �õ��෴�������������
		Object obj = null;
		// �õ����ݵ�key��value
		for (int i = 0; i < documentElement.getPropertyCount(); i++) {

			PropertyInfo propertyInfo = new PropertyInfo();

			documentElement.getPropertyInfo(i, propertyInfo);

			String propertyName = propertyInfo.getName();
			//System.out.println(propertyName);
			if (documentElement.getProperty(propertyName).toString()
					.startsWith("anyType")) {
				obj = useFanSheToData(
						(SoapObject) documentElement.getProperty(propertyName),
						Class.forName("com.example.entity." + propertyName)
								.newInstance());
				classFileList.add(propertyName);
				classValueList.add(obj);
			} else {
				classFileList.add(propertyName);
				classValueList.add(documentElement.getProperty(propertyName)
						.toString());
			}

		}
		fanShe.method(object, classFileList, classValueList);
		return object;
	}
}
