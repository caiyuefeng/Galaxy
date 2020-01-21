import com.galaxy.uranus.annotation.AnnotationRegistration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 20:38 2019/12/30
 *
 */
public class AnnotationRegistrationTest {


	@Test
	public void testOne() throws IOException, ClassNotFoundException {
		AnnotationRegistration registration = AnnotationRegistration.getInstance();
		List<Class<?>> classes = new ArrayList<>(registration.getAnnotationClass().values());
		System.out.println(classes);
		Assert.assertEquals(3, classes.size());
		Assert.assertEquals("com.galaxy.uranus.examples.ValueBindFunc",classes.get(0).getTypeName());
		Assert.assertEquals("com.galaxy.uranus.examples.TypeBindFunc",classes.get(1).getTypeName());
		Assert.assertEquals("com.galaxy.uranus.examples.ValueBindFunc",classes.get(2).getTypeName());
	}
}