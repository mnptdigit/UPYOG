package org.egov.finance.voucher.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.egov.finance.voucher.entity.User;
import org.egov.finance.voucher.enumeration.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CurrentUser implements UserDetails {
	
	private static final long serialVersionUID = -8756608845278722035L;
    private final User user;
    private final List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    public CurrentUser() {
		this.user = new User();
	}
    public CurrentUser(User user) {
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            this.user = user;
            user.getRoles()
                    .forEach(role -> this.authorities.add(new SimpleGrantedAuthority(role.getName())));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return this.user.getPwdExpiryDate().isAfterNow();
//    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.user.isAccountLocked();
    }

//    @Override
//    public boolean isCredentialsNonExpired() {
//        return this.user.getPwdExpiryDate().isAfterNow();
//    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public UserType getUserType() {
        return this.user.getType();
    }

    public User getUser() {
        return this.user;
    }
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

}
