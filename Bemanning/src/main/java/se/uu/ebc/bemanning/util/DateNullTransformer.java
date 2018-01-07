package se.uu.ebc.bemanning.util;

import flexjson.ObjectBinder;
import flexjson.transformer.DateTransformer;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;

public class DateNullTransformer extends DateTransformer {

    private Logger logger = Logger.getLogger(DateNullTransformer.class.getName());

	@Override
	public void transform(Object object) {
		
		logger.debug("MTh transform, object "+ReflectionToStringBuilder.toString(object, ToStringStyle.MULTI_LINE_STYLE));
		
		if (object == null) {
			getContext().writeQuoted("");
		} else {
			super.transform(object);
		}
	}


	@Override
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass){
		logger.debug("MTh instantiate, context "+ReflectionToStringBuilder.toString(context, ToStringStyle.MULTI_LINE_STYLE));
		logger.debug("MTh instantiate, object "+ReflectionToStringBuilder.toString(value, ToStringStyle.MULTI_LINE_STYLE));
		logger.debug("MTh instantiate, type "+ReflectionToStringBuilder.toString(targetType, ToStringStyle.MULTI_LINE_STYLE));
		logger.debug("MTh instantiate, class "+ReflectionToStringBuilder.toString(targetClass, ToStringStyle.MULTI_LINE_STYLE));
		
		if (value == null || ((String)value).equals("")) {
			return null;
		} else {
			return super.instantiate(context, value, targetType, targetClass);
		}
	
	}
	
	
	
	public DateNullTransformer(String format) {
		super(format);
	}
}
    