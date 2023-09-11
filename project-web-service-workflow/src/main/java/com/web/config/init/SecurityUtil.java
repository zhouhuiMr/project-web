package com.web.config.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.web.common.result.ResultEnum;
import com.web.common.user.entity.WebUser;
import com.web.service.handler.auth.UserUtil;
import com.web.service.handler.error.CustomException;

@Component
public class SecurityUtil {
	
    public void logInAs(String username) {
    	WebUser webUser = UserUtil.getUser();
        if (webUser == null) {
            throw new CustomException(ResultEnum.NO_USER_INFO.getMessage());
        }
        UserDetails user = createUser(username);
        
        SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {
			private static final long serialVersionUID = 1L;

			@Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return user.getPassword();
            }

            @Override
            public Object getDetails() {
                return user;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) {
            	throw new UnsupportedOperationException();
            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        }));
    }
    
    /**
     * 创建用户信息
     * @param userName 用户名称
     * @return UserDetails 用户信息
     *
     * @author zhouhui
     * @since 2023.04.01
     */
    private UserDetails createUser(String userName) {
    	List<SimpleGrantedAuthority> authList = new ArrayList<>();
        SimpleGrantedAuthority activitiUser = new SimpleGrantedAuthority("ROLE_ACTIVITI_USER");
        SimpleGrantedAuthority activitiAdmin = new SimpleGrantedAuthority("ROLE_ACTIVITI_ADMIN");
        authList.add(activitiUser);
        authList.add(activitiAdmin);
        
        return new User(userName, "", authList);
    }
}
