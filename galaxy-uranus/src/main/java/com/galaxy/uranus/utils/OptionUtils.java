package com.galaxy.uranus.utils;

import com.galaxy.stone.Symbol;
import com.galaxy.uranus.annotation.AnnotationRegistration;
import com.galaxy.uranus.annotation.OptionAnnotation;
import com.galaxy.uranus.annotation.OptionalArgument;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author 蔡月峰
 * @version 1.0
 * @date Create in 22:31 2019/12/23
 */
public final class OptionUtils {

  private static final int HASH_MAP_INIT_SIZE = 16;

  private OptionUtils() {
    throw new UnsupportedOperationException("unable instantiation class");
  }

  /**
   * 格式化参数值。
   * 格式化方式：参数值不能以--和- 开头，如果以-或--开头则去除。
   *
   * @param val 参数值
   * @return 格式化后参数值
   */
  public static String formatOptionVal(String val) {
    if (val.startsWith(Symbol.DOUBLE_SHORT_RUNG.getValue())) {
      return val.substring(2);
    } else if (val.startsWith(Symbol.SHORT_RUNG.getValue())) {
      return val.substring(1);
    }
    return val;
  }

  public static boolean isJavaProperty(String token) {
    return token.startsWith("D");
  }

  /**
   * 从注解缓存中获取OptionAnnotation注解注释的所有类
   *
   * @return 注解列表
   * @throws IOException            IO异常
   * @throws ClassNotFoundException 类未发现异常
   */
  public static Map<String, List<String>> getPropertiesFromOptionAnnotation()
      throws IOException, ClassNotFoundException {
    Map<String, List<String>> properties = new HashMap<>(HASH_MAP_INIT_SIZE);
    AnnotationRegistration registration = AnnotationRegistration.getInstance();
    Iterator<Map.Entry<Annotation, Class<?>>> iterator = registration.iterator(
        annotation -> annotation instanceof OptionalArgument);
    while (iterator.hasNext()) {
      Map.Entry<Annotation, Class<?>> entry = iterator.next();
      OptionalArgument argument = (OptionalArgument) entry.getKey();
      OptionAnnotation annotation = entry.getValue().getAnnotation(OptionAnnotation.class);
      if (annotation != null) {
        properties.put(annotation.opt(), Arrays.asList(argument.value()));
      }
    }
    return properties;
  }
}
