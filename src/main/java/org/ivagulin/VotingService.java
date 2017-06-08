package org.ivagulin;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.ivagulin.rest.User;
import org.ivagulin.rest.Vote;

@Path("/v1")
public class VotingService {
	@EJB
	private VoteCollectorBean vc;
	
	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(User user){
		vc.registerUser(user);
	}
	
	@Path("/vote")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void vote(Vote vote){
		vc.submitVote(vote);
	}
}
