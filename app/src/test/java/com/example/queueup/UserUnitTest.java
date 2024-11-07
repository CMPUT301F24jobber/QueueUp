package com.example.queueup;
import static org.junit.Assert.assertEquals;

import com.example.queueup.models.User;

import org.junit.Test;
public class UserUnitTest {
        private User mockUser() {
            User mockUser = new User("John", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", "UniqueID");
            return mockUser;
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
}
