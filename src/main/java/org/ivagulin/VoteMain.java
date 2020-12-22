package org.ivagulin;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class VoteMain {
	public static void main(String[] args) {
		final VoteCollector vc = new VoteCollector();
		final VoteHandler vs = new VoteHandler(vc);
		Undertow server = Undertow.builder()
			.addHttpListener(8085, "0.0.0.0")
			.setHandler(new HttpHandler() {
					public void handleRequest(HttpServerExchange exchange) throws Exception {
						exchange.dispatch(vs);
					}
				})
			.build();
		server.start();
	}
}
