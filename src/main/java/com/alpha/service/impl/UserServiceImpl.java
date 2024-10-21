package com.alpha.service.impl;

import java.util.*;
import com.alpha.dao.UserDao;
import com.alpha.model.Role;
import com.alpha.model.User;
import com.alpha.model.UserDto;
import com.alpha.service.RoleService;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userDao.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public User findOne(String username) {
        return userDao.findByUsername(username).orElse(null);
    }

    @Override
    public User save(UserDto user) {
        User nUser = user.getUserFromDto();
        nUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();

        List<String> adminDomains = Arrays.asList("admin.edu", "admin.com", "admin.in", "admin.net");
        String emailDomain = nUser.getEmail().split("@")[1];

        if (adminDomains.contains(emailDomain)) {
            Role adminRole = roleService.findByName("ADMIN");
            roleSet.add(adminRole);
        } else {
            Role userRole = roleService.findByName("USER");
            roleSet.add(userRole);
        }
        nUser.setRoles(roleSet);
        return userDao.save(nUser);
    }

    @Override
    public User getAdminDashboard() {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findByUsername(currentUsername).orElse(null);
    }

    @Override
    public User getUserProfile() {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findByUsername(currentUsername).orElse(null);
    }

    @Override
    public String deleteUser(String username) {
        userDao.deleteByUsername(username);
        return "User deleted successfully";
    }
    @Override
    public User findByUsername(String username){
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findByUsername(currentUsername).orElse(null);
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return userDao.findById(id)
                .orElseThrow(() -> new Exception("User not found with ID: " + id));
    }

    @Override
    public User updateUserProfile(UserDto user) {
        User existingUser = userDao.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        return userDao.save(existingUser);
    }
}
