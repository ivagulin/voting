package org.ivagulin;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.ivagulin.rest.User;
import org.ivagulin.rest.Vote;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.sortedset.ZAddParams;

@Stateless
@Path("/v1")
@PermitAll
public class VotingService {
	Jedis jc;
	
	static public final String KEY_USER_VOTES = "user_votes";
	static public final String KEY_LANGUAGE_VOTES = "language_votes";
	static public final String KEY_EMAIL2USER = "email2user:";
	
	@PostConstruct
	public void init(){
		jc = new Jedis("localhost", 6379);
	}
	
	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(User user){
		Pipeline p = jc.pipelined();
		p.zadd(KEY_USER_VOTES, Collections.singletonMap(user.getEmail(), 0d), ZAddParams.zAddParams().nx());
		Response<Long> setRV = p.hsetnx(KEY_EMAIL2USER, user.getEmail(), user.getName());
		p.sync();
		if(setRV.get() == 0){
			throw new IllegalArgumentException("user already exists");
		}
	}
	
	@Path("/vote")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void vote(Vote vote){
//		if(jc.hget(KEY_EMAIL2USER, vote.getEmail()) == null)
//			throw new IllegalArgumentException("User with email "+vote.getEmail()+"not exists");
		Pipeline p = jc.pipelined();
		p.zincrby(KEY_USER_VOTES, 1, vote.getEmail());
		p.zincrby(KEY_LANGUAGE_VOTES, 1, String.valueOf(vote.getLanguage()));
		p.sync();
	}
}
