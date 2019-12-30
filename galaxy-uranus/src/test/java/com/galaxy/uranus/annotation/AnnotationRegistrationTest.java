package com.galaxy.uranus.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 20:38 2019/12/30
 * @Modified By:
 */
public class AnnotationRegistrationTest {


	@Test
	public void testOne() {
		AnnotationRegistration registration = AnnotationRegistration.getInstance();
		List<Class<?>> classes = new ArrayList<>(registration.getAnnotationClass().values());
		Assert.assertEquals(3, classes.size());
		Assert.assertEquals("com.galaxy.uranus.examples.ValueBindFunc",classes.get(0).getTypeName());
		Assert.assertEquals("com.galaxy.uranus.examples.ValueBindFunc",classes.get(1).getTypeName());
		Assert.assertEquals("com.galaxy.uranus.examples.TypeBindFunc",classes.get(2).getTypeName());

	}
}