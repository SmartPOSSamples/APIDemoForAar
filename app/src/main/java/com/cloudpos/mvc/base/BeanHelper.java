//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cloudpos.mvc.base;

import com.cloudpos.mvc.common.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BeanHelper {
    private Class<?> mClass;
    private Object mObject;
    private Method[] declaredMethods;

    public BeanHelper(Class<?> clazz) {
        this.mClass = clazz;
    }

    public BeanHelper(Object obj) {
        this.mObject = obj;
        this.mClass = this.mObject.getClass();
    }

    public Method getMethod(String methodName, Class<?>... classes) throws NoSuchMethodException {
        this.declaredMethods = this.mClass.getDeclaredMethods();
        Method result = null;
        int matchLevel = -1;
        boolean isFirst = true;
        Method[] var9;
        int var8 = (var9 = this.declaredMethods).length;

        for(int var7 = 0; var7 < var8; ++var7) {
            Method method = var9[var7];
            String name = method.getName();
            if (name.equals(methodName)) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == classes.length) {
                    int tempMatchLevel = this.matchLevel(paramTypes, classes);
                    if (tempMatchLevel >= 0) {
                        if (isFirst && matchLevel < tempMatchLevel) {
                            isFirst = false;
                            matchLevel = tempMatchLevel;
                        } else {
                            if (matchLevel >= tempMatchLevel) {
                                continue;
                            }

                            matchLevel = tempMatchLevel;
                        }

                        result = method;
                    }
                }
            }
        }

        if (result == null) {
            throw new NoSuchMethodException(methodName + " " + Arrays.asList(classes).toString());
        } else {
            return result;
        }
    }

    public Class<?> getClosestClass(Class<?> clazz) {
        return clazz.getSuperclass();
    }

    public int matchLevel(Class<?>[] paramTypes, Class<?>[] transferParamTypes) {
        int matchLevel = -1;

        for(int m = 0; m < paramTypes.length; ++m) {
            Class<?> paramType = paramTypes[m];
            Class<?> tParamType = transferParamTypes[m];
            if (paramType.equals(tParamType)) {
                ++matchLevel;
            } else {
                List<Class<?>> superClasses = getAllSuperClass(tParamType);

                for(int n = 1; n <= superClasses.size(); ++n) {
                    Class<?> superClass = (Class)superClasses.get(n - 1);
                    if (superClass != null && !superClass.equals(paramType)) {
                        break;
                    }

                    matchLevel += n;
                }
            }
        }

        return matchLevel;
    }

    public Object invoke(String methodName, Object... args) throws Exception {
        Method method = this.getMethod(methodName, getClassTypes(args));
        Object result = method.invoke(this, args);
        return result;
    }

    public static Class<?>[] getClassTypes(Object... args) {
        if (args == null) {
            return null;
        } else {
            Class[] classes = new Class[args.length];

            for(int i = 0; i < args.length; ++i) {
                classes[i] = args[i].getClass();
            }

            return classes;
        }
    }

    public static List<Class<?>> getAllSuperClass(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList();
        Class cla = clazz;

        do {
            cla = cla.getSuperclass();
            Logger.debug("class: " + clazz + ", super class: " + cla);
            if (cla != null) {
                classes.add(cla);
            }
        } while((cla == null || !cla.equals(Object.class)) && cla != null);

        return classes;
    }
}

