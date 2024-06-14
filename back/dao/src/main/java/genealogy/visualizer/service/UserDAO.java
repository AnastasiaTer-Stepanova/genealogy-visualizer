package genealogy.visualizer.service;

import genealogy.visualizer.entity.User;

public interface UserDAO {

    User findByLogin(String login);

    void save(User user);

}
