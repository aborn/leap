package org.popkit.core.beans;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.annotation.MobileRequest;
import org.popkit.core.context.LeapContext;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


import java.lang.reflect.Field;
import java.net.URLDecoder;


@Component
public class DefaultRequestBuilder {

    public static final String RESPONSE_ENCODING = "utf-8";

    public static boolean isModel(Class clazz) {
        return !Object.class.equals(clazz);
    }

    private static String getDecodedValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        String decodedValue = null;
        try {
            decodedValue = URLDecoder.decode(value, RESPONSE_ENCODING);
        } catch (Exception e) {
            // log here
        }

        return StringUtils.defaultString(decodedValue, value);
    }

    public <T> T instantiate(LeapContext context, Class<T> clazz) {
        if (clazz == null) {
            // here should throw exception
        }

        try {
            Object model = BeanUtils.instantiate(clazz);
            Class nextClazz = clazz;
            boolean emptyCheck;
            boolean decoded;
            while (isModel(nextClazz)) {
                Field[] fields = nextClazz.getDeclaredFields();
                for (int i = 0; i < fields.length; ++i) {
                    emptyCheck = false;
                    decoded = true;
                    Field field = fields[i];
                    MobileRequest.Param param = field.getAnnotation(MobileRequest.Param.class);
                    String name;
                    if (param != null) {
                        name = param.name();
                        emptyCheck = param.required();
                        decoded = param.decoded();
                    } else {
                        name = field.getName();
                    }

                    // 获取参数的value值
                    String value = context.getParameter(name);
                    if (decoded) {
                        value = getDecodedValue(value);
                    }

                    if (emptyCheck && StringUtils.isEmpty(value)) {
                        //
                    }
                    if (value != null) {
                        try {
                            field.setAccessible(true);
                            ReflectionUtils.setField(field, model, value);
                        } catch (Throwable t) {
                            // log here
                        }
                    }
                }
                nextClazz = nextClazz.getSuperclass();
            }
            return (T) model;
        } catch (BeanInstantiationException e) {
            // throw exception
        }
        return null;
    }
}
