package com.example.queueup;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class UserInstrumatedTest {
        private User mockUser() {
            return new User("John", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", "UniqueID", false);
        }
        public User generateRandomMockUser() {
            return new User("hi", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", UUID.randomUUID().toString(), false);
        }

    public UserController getMockUserController() {
        UserController userController = UserController.getInstance();
        return userController;
    }

    @Test
        public void testUserCreation() {
            User user = mockUser();
            assertEquals("John", user.getFirstName());
            assertEquals("Doe", user.getLastName());
            assertEquals("johndoe", user.getUsername());
            assertEquals("johnDoe@gmail.com", user.getEmailAddress());
            assertEquals("1234567890", user.getPhoneNumber());
        }
        @Test
        public void testUploadUser() {
            User mockUser = generateRandomMockUser();
            getMockUserController().createUser(mockUser);
        }
        @Test
        public void testUploadMultipleUsers() {
            for (int i = 0; i < 10; ++i) {
                User mockUser = generateRandomMockUser();
                getMockUserController().createUser(mockUser);
            }
        }


}
