package cn.deru.backend.util;

import cn.deru.backend.annotation.Sensitive;
import cn.deru.backend.annotation.SensitiveType;

import java.lang.reflect.Field;

public class SensitiveUtil {

    public static String desensitizeToString(Object obj) {
        if (obj == null) return "null";

        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
            return obj.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append("(");

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            if (i > 0) sb.append(", ");
            sb.append(field.getName()).append("=");

            try {
                Object value = field.get(obj);
                Sensitive annotation = field.getAnnotation(Sensitive.class);

                if (annotation != null && value instanceof String) {
                    sb.append(mask((String) value, annotation.type()));
                } else {
                    sb.append(value);
                }
            } catch (IllegalAccessException e) {
                sb.append("???");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    private static String mask(String value, SensitiveType type) {
        if (value == null || value.isEmpty()) return value;

        switch (type) {
            case PASSWORD:
                return "******";
            case PHONE:
                return value.length() > 7 ? value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "****";
            case EMAIL:
                int at = value.indexOf('@');
                return at > 1 ? value.charAt(0) + "***" + value.substring(at) : "***";
            case ID_CARD:
                return value.length() > 7 ? value.substring(0, 3) + "***********" + value.substring(value.length() - 4) : "****";
            case NAME:
                return value.length() > 1 ? value.charAt(0) + "*" : "*";
            default:
                return "****";
        }
    }
}
