package com.alpha.controller;

import com.alpha.config.TokenProvider;
import com.alpha.model.LoginUser;
import com.alpha.model.User;
import com.alpha.model.UserDto;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private UserService userService;

    /**
     * Generates a token for the given user credentials.
     *
     * @param loginUser The user's login credentials.
     * @return A response entity containing the generated token.
     * @throws AuthenticationException if authentication fails.
     */

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);

        // Extract role(s) from authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Retrieve user ID
        String username = loginUser.getUsername();
        User user = userService.findOne(username); // Fetch user details
        Long userId = user != null ? user.getId() : null; // Assuming getId() returns the user ID

        // Return token, role, and user ID
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);
        response.put("userId", userId); // Add user ID to the response

        return ResponseEntity.ok(response);
    }



    /**
     * Saves a new user.
     *
     * @param user The user to be saved.
     * @return The saved user.
     */
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public User saveUser(@RequestBody UserDto user){
        return userService.save(user);
    }

    /**
     * Returns a message that can only be accessed by users with the 'ADMIN' role.
     *
     * @return A message that can only be accessed by admins.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/adminping", method = RequestMethod.GET)
    public String adminPing(){
        return "Only Admins Can Read This";
    }

    /**
     * Returns a message that can be accessed by any user.
     *
     * @return A message that can be accessed by any user.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value="/userping", method = RequestMethod.GET)
    public String userPing(){
        return "Any User Can Read This";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/find/all", method = RequestMethod.GET)
    public List<User> getAllList(){
        return userService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/find/by/username", method = RequestMethod.GET)
    public User getAllList(@RequestParam String username){
        return userService.findOne(username);
    }

    // Additional Admin-specific endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/get/admin/dashboard", method = RequestMethod.GET)
    public User getAdminDashboard(){
        return userService.getUserProfile();
    }

    // Additional User-specific endpoint
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value="/get/user/profile", method = RequestMethod.GET)
    public User getUserProfile(){
        return userService.getUserProfile();
    }

    // Additional Admin-specific endpoint to delete user
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/delete/user", method = RequestMethod.DELETE)
    public String deleteUser(@RequestParam String username){
        userService.deleteUser(username);
        return "User deleted successfully";
    }

    // Additional User-specific endpoint to update user profile
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value="/update/user/profile", method = RequestMethod.PUT)
    public User updateUserProfile(@RequestBody UserDto user){
        return userService.updateUserProfile(user);
    }
}