package com.example.chen.hsms.ws;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FanShe {
	/**
	 * �õ��ǿյ�����ֵ�б�
	 * 
	 * @param list
	 *            ����ֵ�б�
	 * @return
	 */
	public List<String> getPropertyVal(List<String> list) {

		List<String> propertyValues = new ArrayList<String>();
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				propertyValues.add(list.get(i));
			}
		}
		return propertyValues;
	}

	/**
	 * �õ�cla��������б�
	 * 
	 * @param cla
	 *            Ҫ�������
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getPropertyNames(Class cla) {
		List<String> list = new ArrayList<String>();
		Field[] fs = cla.getDeclaredFields();
		// fs=cla.getFields();��������Ļ���ֻ���public ���е�
		for (Field f : fs) {
			list.add(f.getName());
		}
		return list;
	}

	/**
	 * �õ��������б����Ӧ������ֵ�б�
	 * 
	 * @param shuxingList
	 *            �����б�
	 * @param obj
	 *            ʵ����
	 * @return �����Զ�Ӧ������ֵ�б�
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getValueList(List<String> shuxingList, Object obj) {
		List valueList = null;
		try {

			Class cla = obj.getClass();
			valueList = new ArrayList();
			for (int i = 0; i < shuxingList.size(); i++) {
				Field f = cla.getDeclaredField(shuxingList.get(i).toString());
				f.setAccessible(true);// ���������ܸ�˽�е�ֵ
				// �õ�����ֵ
				Object str = f.get(obj);
				valueList.add(str);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valueList;
	}

	/**
	 * ��obj��������б�һ������ֵ
	 * 
	 * @param obj
	 *            Ҫ�������
	 * @param propertyNames
	 *            ��������б�
	 * @param propertyVales
	 *            ���������Ӧ������ֵ�б�
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public void method(Object obj, List<String> propertyNames,
			List propertyVales) {
		try {
			Class cla = obj.getClass();
			for (int i = 0, len = propertyNames.size(); i < len; i++) {
				// Log.i("propertyNames"+i, propertyNames.get(i)+"");
				Field f = cla.getDeclaredField(propertyNames.get(i).toString());
				f.setAccessible(true);// ���������ܸ�˽�е�ֵ
				f.set(obj, propertyVales.get(i));
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] GetFiled(Object object) {
		Class<? extends Object> cla = object.getClass();
		Field[] fs = cla.getDeclaredFields();
		String[] fields = new String[fs.length];
		for (int i = 0; i < fs.length; i++) {
			// Log.i("propertyNames"+i, propertyNames.get(i)+"");
			fields[i] = fs[i].getName();
		}
		return fields;

	}

	/**
	 * ��obj��ĵ������Ը�ֵ
	 * 
	 * @param obj
	 *            Ҫ�������
	 * @param shuXing
	 *            Ҫ��ֵ������
	 * @param value
	 *            Ҫ�����Ը����ֵ
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void method(Object obj, String shuXing, Object value)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		@SuppressWarnings("rawtypes")
		Class cla = obj.getClass();
		Field f = cla.getDeclaredField(shuXing);
		// ���������ܸ�˽�е�ֵ
		f.setAccessible(true);
		// Ϊ���Ը�ֵ
		f.set(obj, value);
	}
}
