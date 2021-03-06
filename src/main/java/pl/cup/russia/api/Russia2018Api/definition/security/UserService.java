package pl.cup.russia.api.Russia2018Api.definition.security;

import pl.cup.russia.api.Russia2018Api.dto.UserBet;
import pl.cup.russia.api.Russia2018Api.model.security.User;

import java.util.List;

public interface UserService {

    User registerNewUserAccount(User user);

    User selectUserByUsername(String username);

    Integer setUserPaidStatusToTrue(String username);

    List<User> getPaidUsers();

    List<User> getNonPaidUsers();

	List<UserBet> getUserBets();
}
