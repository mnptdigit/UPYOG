package org.egov.finance.voucher.util;

import java.util.Optional;

import org.egov.finance.voucher.entity.User;
import org.egov.finance.voucher.enumeration.UserType;
import org.egov.finance.voucher.model.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

	/**
	 * Checks if the user is authenticated anonymously using the provided
	 * Authentication.
	 */
	public static boolean userAnonymouslyAuthenticated(Optional<Authentication> authentication) {
		return authentication.isPresent() && authentication.get().getPrincipal() instanceof String;
	}

	/**
	 * Checks if the current user is anonymously authenticated.
	 */
	public static boolean userAnonymouslyAuthenticated() {
		Optional<Authentication> authentication = getCurrentAuthentication();
		return userAnonymouslyAuthenticated(authentication);
	}

	/**
	 * Gets the current Spring Security Authentication object.
	 */
	public static Optional<Authentication> getCurrentAuthentication() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
	}

	/**
	 * Returns the currently authenticated user from the security context.
	 */
	public User getCurrentUser() {
//		Optional<Authentication> authentication = getCurrentAuthentication();
//		if (authentication.isEmpty() || userAnonymouslyAuthenticated(authentication)) {
//			return null;
//		}
//		Object principal = authentication.get().getPrincipal();
//		if (principal instanceof CurrentUser currentUser) {
//			return currentUser.getUser();
//		}
//		return null;

		Optional<Authentication> authentication = getCurrentAuthentication();
		/*
		 * return !authentication.isPresent() ||
		 * userAnonymouslyAuthenticated(authentication) ?
		 * userService.getUserByUsername(ANONYMOUS_USERNAME) :
		 * userService.getUserById(((CurrentUser)
		 * authentication.get().getPrincipal()).getUserId());
		 */
		return ((CurrentUser) authentication.get().getPrincipal()).getUser();
	}

	/**
	 * Determines the user type of the current user.
	 */
	public UserType currentUserType() {
		Optional<Authentication> authentication = getCurrentAuthentication();
		if (authentication.isPresent() && !userAnonymouslyAuthenticated(authentication)) {
			Object principal = authentication.get().getPrincipal();
			if (principal instanceof CurrentUser currentUser) {
				return currentUser.getUserType();
			}
		}
		return UserType.SYSTEM;
	}

	/**
	 * Checks if the current user is a citizen.
	 */
	public boolean currentUserIsCitizen() {
		return currentUserType() == UserType.CITIZEN;
	}

	/**
	 * Checks if the current user is an employee.
	 */
	public boolean currentUserIsEmployee() {
		return currentUserType() == UserType.EMPLOYEE;
	}

	/**
	 * Checks if the current user is anonymous.
	 */
	public static boolean currentUserIsAnonymous() {
		return userAnonymouslyAuthenticated();
	}
}
