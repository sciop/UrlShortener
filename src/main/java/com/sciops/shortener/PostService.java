package com.sciops.shortener;

import java.util.Calendar;
import java.util.Random;

import com.sciops.shortener.persistency.UrlMapping;
import com.sciops.shortener.persistency.UrlMappingRepository;
import com.sciops.shortener.web.model.UrlMappingRequest;

public class PostService {

	public static UrlMapping processNewBinding(UrlMappingRequest request, UrlMappingRepository repo) {
		
		if(!validateRequest(request)) return null;
		if(repo.findByInput(request.getSuggestedKey()) != null) return null;
		UrlMapping mapping;
		if(request.getSuggestedKey() == null || request.getSuggestedKey().length() == 0) {
			mapping = new UrlMapping(newRandomUrl(repo), request.getValue(), request.getExpiration(), 
									 request.isSingleUse());
		}
		else mapping = new UrlMapping(request.getSuggestedKey(), request.getValue(), 
												request.getExpiration(), request.isSingleUse());
		if(repo.save(mapping) == null) return null;
		return mapping;
	}

	private static boolean validateRequest(UrlMappingRequest request) {
		
		for(int i = 0; i < request.getSuggestedKey().length(); i++) {
			char c = request.getSuggestedKey().charAt(i);
			if(!(Character.isDigit(c) || Character.isLetter(c) || c == '-' || c == '_' || c == '~'))
				return false;
		}
		
		for(int i = 0; i < request.getValue().length(); i++) {
			char c = request.getValue().charAt(i);
			if(!(Character.isDigit(c) || Character.isLetter(c) || c == '-' || c == '_' || c == '~' || 
				 c == '.' || c == ':' || c == '/' || c == '?' || c == '#' || c == '[' || c == ']' || 
				 c == '@' || c == '!' || c == '$' || c == '&' || c == '\'' || c == '(' || c == ')' || 
				 c == '*' || c == '+' || c == ',' || c == ';' || c == '%' || c == '='))
				return false;
		}
		
		if(request.getExpiration() != 0 && request.getExpiration() < Calendar.getInstance().getTimeInMillis())
			return false;
		
		return true;
	}

	private static String newRandomUrl(UrlMappingRepository repo) {
		String ans = "";
		Random random = new Random();
		do {
			ans += randomIntToChar(random.nextInt(65));
		} while(ans.length() < 5 || repo.findByInput(ans) != null);
		return ans;
	}

	private static char randomIntToChar(int i) {
		if(i < 26) return (char) ('A' + i);
		if(i < 52) return (char) ('a' + i - 26);
		if(i < 62) return (char) ('0' + i - 52);
		if(i == 62) return '-';
		if(i == 63) return '_';
		return '~';
	}
}
