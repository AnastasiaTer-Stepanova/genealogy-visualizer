package genealogy.visualizer.service.impl;

import genealogy.visualizer.entity.User;
import genealogy.visualizer.repository.UserRepository;
import genealogy.visualizer.service.UserDAO;

public class UserDAOImpl implements UserDAO {

    private final UserRepository userRepository;

    public UserDAOImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}