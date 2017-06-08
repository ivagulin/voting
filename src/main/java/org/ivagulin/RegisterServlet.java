package org.ivagulin;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ivagulin.rest.User;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/v1/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = -3831553551849420965L;

	@EJB
	private VoteCollectorBean vc;
	
	private ObjectMapper om;
	
	public RegisterServlet() {
		om = new ObjectMapper();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User u = om.readValue(req.getInputStream(), User.class);
		vc.registerUser(u);
	}
}
