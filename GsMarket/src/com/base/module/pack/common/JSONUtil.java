package com.base.module.pack.common;

import com.base.module.pack.annotation.JSONObj;
import com.base.module.pack.annotation.JSONValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Copyright (c) 2009-2011 by Grandstream Networks
 * User: opensmile
 * Date: 13-9-6
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */


public class JSONUtil {

    private interface JSONSYMBOL {
        String BRACKET_LEFT = "[";
        String BRACKET_RIGHT = "]";
        String BRACE_LEFT = "{";
        String BRACE_RIGHT = "}";
        String QUOTATION = "\"";
        String COLON = ":";
        String COMMA = ",";
    }

    public static String ObjToJson(Object obj) {

        StringBuilder sb = new StringBuilder("");
        sb.append(JSONSYMBOL.BRACE_LEFT);
        JsonObjTypeProcess(sb, obj, null);
        sb.append(JSONSYMBOL.BRACE_RIGHT);
        return sb.toString();
    }

    private static void JsonObjTypeProcess(StringBuilder sb, Object obj, String objname) {
        if (obj == null) {
            return;
        }
        Object[] objs = null;
        if (obj instanceof Collection) {
            objs = ((Collection) obj).toArray();
        } else if (obj instanceof Map) {
            objs = ((Map) obj).values().toArray();
        } else if (obj.getClass().isArray()) {
            objs = (Object[]) obj;
        } else {
            objs = new Object[]{obj};
        }

        if (objname == null) {
            JSONObj jsonObjAnnotation = objs[0].getClass().getAnnotation(JSONObj.class);
            if (jsonObjAnnotation != null) {
                objname = jsonObjAnnotation.name().equals("") ? objs[0].getClass().getSimpleName() : jsonObjAnnotation.name();
            } else {
                objname = "Object";
            }
        }
        sb.append(JSONSYMBOL.QUOTATION + objname + JSONSYMBOL.QUOTATION + JSONSYMBOL.COLON);
        sb.append(JSONSYMBOL.BRACKET_LEFT);

        for (int x = 0; x < objs.length; x++) {
            ParseObjToJson(sb, objs[x]);
            if (x == objs.length - 1) {
                continue;
            }
            sb.append(JSONSYMBOL.COMMA);
        }
        sb.append(JSONSYMBOL.BRACKET_RIGHT);
    }


    private static void ParseObjToJson(StringBuilder sb, Object obj) {
        sb.append(JSONSYMBOL.BRACE_LEFT);

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (int x = 0; x < declaredFields.length; x++) {
            Field declaredField = declaredFields[x];

            JSONValue jsonValue = declaredField.getAnnotation(JSONValue.class);
            if (jsonValue == null) {
                continue;
            }
            String fieldJSonName = jsonValue.name().equals("") ? declaredField.getName() : jsonValue.name();
            String getFieldMethodName = FieldToGetMethodName(declaredField.getName());
            try {

                Method method = obj.getClass().getMethod(getFieldMethodName);
                Object invoke = method.invoke(obj, new Object[]{});
                if (invoke == null) {
                    continue;
                }
                if (sb.lastIndexOf(JSONSYMBOL.BRACE_LEFT) != sb.length() - 1) {
                    sb.append(JSONSYMBOL.COMMA);
                }

                if (!Utils.isPrimitiveType(invoke) && !(invoke instanceof String)) {
                    JsonObjTypeProcess(sb, invoke, fieldJSonName);
                } else {
                    sb.append(JSONSYMBOL.QUOTATION + fieldJSonName + JSONSYMBOL.QUOTATION);
                    sb.append(JSONSYMBOL.COLON);
                    sb.append(JSONSYMBOL.QUOTATION + invoke.toString() + JSONSYMBOL.QUOTATION);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        sb.append(JSONSYMBOL.BRACE_RIGHT);
    }


    private static String FieldToGetMethodName(String str) {
        return "get" + Utils.FirstCharUpperCase(str);
    }
}