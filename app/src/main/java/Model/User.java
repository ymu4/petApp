package Model;

public class User {
        private String userId;
        private String username;
        private String email;
        private String password;
        private String confirmPassword;
        private String phoneNumber;
        private  String profile_image;
        // You can add more attributes as needed

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        public User() {
        }

        // Constructor
        public User(String userId, String username, String email, String password,String confirmPassword, String phoneNumber, String profile_image) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.profile_image = profile_image;
            this.confirmPassword = confirmPassword;
            // Initialize more attributes as needed
        }

        // Getters and setters for attributes

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        // Add more getters and setters for additional attributes if needed
    }


