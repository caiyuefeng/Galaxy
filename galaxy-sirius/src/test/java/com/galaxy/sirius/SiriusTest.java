package com.galaxy.sirius;

import com.galaxy.sirius.sync.SyncExecutor;
import org.junit.Test;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 22:17 2019/8/11
 * @Modified By:
 */
public class SiriusTest {

    public void testMain(int a, int b) throws ClassNotFoundException {
        SyncExecutor executor = SyncExecutor.getInstance();
        Class<?>[] classes = new Class[2];
        classes[0] = int.class;
        classes[1] = int.class;
        Object[] objects = new Object[2];
        objects[0] = a;
        objects[1] = b;
        executor.executor(this, "runSync", classes, objects);
    }

    public static void main(String[] args) {
        AsmUtils.printClassStruct(SiriusTest.class);
    }
}