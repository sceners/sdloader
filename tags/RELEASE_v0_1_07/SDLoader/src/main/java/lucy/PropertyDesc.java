package lucy;

import java.lang.reflect.Method;

public interface PropertyDesc extends DescData {

	void setReadMethod(Method readMethod);

	void setPropertyName(String propertyName);

	void setWriteMethod(Method writeMethod);

	void setPropertyType(Class propertyType);

	String getPropertyName();

	Class getPropertyType();

	Method getReadMethod();

	Method getWriteMethod();

	<T> void setValue(T target, Object... value);
	
	<T> T getValue(T target);
}
