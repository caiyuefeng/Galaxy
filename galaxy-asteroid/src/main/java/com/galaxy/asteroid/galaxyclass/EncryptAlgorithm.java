package com.galaxy.asteroid.galaxyclass;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *  加密算法接口
 * @date 2019/3/12 9:03
 **/
public interface EncryptAlgorithm {

    /**
     * 加密
     *
     * @param bytes 待加密字节组
     * @return 加密后字节组
     */
    byte[] encrypt(byte[] bytes);

    /**
     * 解密
     *
     * @param bytes 待解密字节组
     * @return 解密后字节组
     */
    byte[] decrypt(byte[] bytes);
}
