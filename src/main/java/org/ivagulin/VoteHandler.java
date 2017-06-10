package org.ivagulin;

import org.ivagulin.rest.User;
import org.ivagulin.rest.Vote;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class VoteHandler implements HttpHandler {
	private final VoteCollector vc;
	
	private ObjectMapper om;
	
	public VoteHandler(VoteCollector initialVC) {
		om = new ObjectMapper();
		vc = initialVC;
	}

	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.startBlocking();
		if(exchange.getRequestPath().equals("/api/v1/poll") ){
			Vote vote = om.readValue(exchange.getInputStream(), Vote.class);
			vc.submitVote(vote);
		}
		else if (exchange.getRequestPath().equals("/api/v1/register")) {
			User user = om.readValue(exchange.getInputStream(), User.class);
			vc.registerUser(user);			
		}
	}
}
