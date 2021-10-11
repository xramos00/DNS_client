package allTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)


@SuiteClasses({
	IpTest.class,
	PunycodeTest.class,
	Domain.class
})
public class AllTests {
	
}
