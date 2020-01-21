package com.galaxy.boot;

import com.galaxy.earth.exception.GalaxyException;

/**
 * 不支持协议异常。
 * @author 蔡月峰
 * @date 2020/1/21 16:05
 * @version 1.0
 **/
public class UnSupportProtocolException extends GalaxyException {
  public UnSupportProtocolException(final String message, final Throwable e) {
    super(message, e);
  }

  public UnSupportProtocolException(final String message) {
    super(message);
  }
}
