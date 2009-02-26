package examples.page;

import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Json;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

@RequestScope
@Page("json")
public class JsonPage {

	@Default
	public Navigation aaa(WebContext context) {
		System.out.println("json default called.");
		Hoge jsonObject = new Hoge();
		return Json.convert(jsonObject);
	}

	public static class Hoge {

		protected String name = "yone";

		protected int age = 12345;

		protected String[] alias = new String[] { "yone098", "yonex" };

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String[] getAlias() {
			return alias;
		}

		public void setAlias(String[] alias) {
			this.alias = alias;
		}

	}
}
