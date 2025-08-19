package lt.Edgaras.floristic_backend.service;

import lombok.RequiredArgsConstructor;
import lt.Edgaras.floristic_backend.dto.user.UserRequestDTO;
import lt.Edgaras.floristic_backend.model.User;
import lt.Edgaras.floristic_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UserRequestDTO dto, String username) {
        User user = findUserByUsername(username);

        user.setEmail(dto.email());

        saveUser(user);

        return user;
    }

    public void deleteUser(String username) {
        User user = findUserByUsername(username);
        userRepository.delete(user);
    }
}
