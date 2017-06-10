package org.ivagulin;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.ivagulin.rest.User;
import org.ivagulin.rest.Vote;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class VoteCollector  {
	static public final String KEY_USER_VOTES = "user_votes";
	static public final String KEY_LANGUAGE_VOTES = "language_votes";
	static public final String KEY_EMAIL2USER = "email2user";
	
	private final Jedis jc;
	private final Timer t;
	private final Pattern emailPattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}");
	
	private ConcurrentMap<Integer, AtomicLong> languageVotes = new ConcurrentHashMap<Integer, AtomicLong>();
	private ConcurrentMap<String, AtomicLong> emailVotes = new ConcurrentHashMap<String, AtomicLong>();	
	
	public VoteCollector(){
		jc = new Jedis("localhost", 6379);
		
		for(int i=0; i<10; i++)
			languageVotes.put(i, new AtomicLong(0));
		
		for(String email: jc.hkeys(KEY_EMAIL2USER))
			emailVotes.putIfAbsent(email, new AtomicLong(0));
		
		t = new Timer(true);
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				flushVotes();
			}
		}, 60*1000, 60*1000);
	}
	
	public void registerUser(User user){
		if(!emailPattern.matcher(user.getEmail()).matches()){
			throw new IllegalArgumentException("User email don't match pattern "+emailPattern.pattern());
		}
		Long setRV = jc.hsetnx(KEY_EMAIL2USER, user.getEmail(), user.getName());
		if(setRV.intValue() == 0){
			throw new IllegalArgumentException("User already exists");
		}
		emailVotes.putIfAbsent(user.getEmail(), new AtomicLong(0));
	}
	
	public void submitVote(Vote vote){
		AtomicLong emailCounter = emailVotes.get(vote.getEmail());
		if(emailCounter == null)
			throw new IllegalArgumentException("User with email "+vote.getEmail() + " doesn not exists");
		emailCounter.incrementAndGet();
		
		AtomicLong languageCounter = languageVotes.get(vote.getLanguage());
		if(languageCounter == null)
			throw new IllegalArgumentException("Language "+String.valueOf(vote.getLanguage())+" doesn not exists");
		languageCounter.incrementAndGet();
	}
	
	public void flushVotes(){
		Pipeline p = jc.pipelined();
		for(String email: emailVotes.keySet()){
			long curVal = emailVotes.get(email).getAndSet(0);
			p.zincrby(KEY_USER_VOTES, curVal, email);
		}
		for(int lang: languageVotes.keySet()){
			long curVal = languageVotes.get(lang).getAndSet(0);
			p.zincrby(KEY_LANGUAGE_VOTES, curVal, String.valueOf(lang));
		}
		p.sync();		
	}
}
