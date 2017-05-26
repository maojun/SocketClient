package com.gable.socket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloClient {
	@RequestMapping("/helloClient")
	@ResponseBody
	public String helloClient() {
		return "helloClient";
	}
}
