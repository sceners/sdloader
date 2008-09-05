package examples.service.impl;

import examples.service.HelloService;

public class HelloServiceImpl implements HelloService {

	@Override
	public String hello() {
		return "Hello";
	}

}
