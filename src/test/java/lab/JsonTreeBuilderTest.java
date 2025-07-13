package lab;

import org.testng.annotations.Test;

import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.builder.JsonTreeBuilder;

public class JsonTreeBuilderTest {

	@Test
	public void testComplicatedPropertyKey() {
		String json = """
				{  "lsm6dsv_0 Accelerometer Non-wakeup": {
						"values": [ 0.2620140910148620611111111111111111111111111111111111111111111111111111111111123,
									0.012562318705022335,
									9.8380994232129]
				}}
				""";
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject value = (JsonObject) builder.build(json);
		System.out.println(value);
	}
}
