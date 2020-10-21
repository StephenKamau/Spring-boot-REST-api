package engine.user;

import engine.security.MyUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserDetailRepository userDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserData userData = userDetailRepository.findByEmail(email).isPresent() ? userDetailRepository.findByEmail(email).get() : null;
        if (userData == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserPrincipal(userData);
    }
}
