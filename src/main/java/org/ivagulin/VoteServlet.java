package org.ivagulin;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivagulin.rest.Vote;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/v1/poll")
public class VoteServlet extends HttpServlet {
	private static final long serialVersionUID = 1525755577336095361L;

	@EJB
	private VoteCollectorBean vc;
	
	private ObjectMapper om;
	
	public VoteServlet() {
		om = new ObjectMapper();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Vote v = om.readValue(req.getInputStream(), Vote.class);
		vc.submitVote(v);
	}
}
