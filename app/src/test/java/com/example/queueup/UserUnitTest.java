package com.example.queueup;
import static org.junit.Assert.assertEquals;

import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;

import org.junit.Test;

import java.util.UUID;

public class UserUnitTest {
        private User mockUser() {
            User mockUser = new User("John", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", "UniqueID", false);
            return mockUser;
        }
        public User generateRandomMockUser() {
            User mockUser = new User("hi", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", UUID.randomUUID().toString(), false);
            return mockUser;
        }

    public UserController getMockUserController() {
        UserController userController = UserController.getInstance();
        return userController;
    }

    public void testUserCreation() {
        User user = mockUser();
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("johnDoe@gmail.com", user.getEmailAddress());
        assertEquals("1234567890", user.getPhoneNumber());
    }
    public void testUploadUser() {
        User mockUser = generateRandomMockUser();
        getMockUserController().createUser(mockUser);
    }



}
