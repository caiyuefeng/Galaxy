package com.galaxy.boot;

import com.galaxy.earth.exception.GalaxyException;

/**
 * 不支持文件格式异常。
 * @author 蔡月峰
 * @version 1.0
 * @date 2020/1/21 16:06
 **/
public class UnSupportFileFormatException extends GalaxyException {
  public UnSupportFileFormatException(final String message, final Throwable e) {
    super(message, e);
  }

  public UnSupportFileFormatException(final String message) {
    super(message);
  }
}
