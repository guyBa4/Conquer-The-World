
package Application.DataAccessLayer.Repositories;

        import Application.Entities.questions.Answer;
        import Application.Entities.users.MobileUser;
        import Application.Entities.users.User;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.stereotype.Repository;

        import java.util.List;
        import java.util.UUID;

@Repository
public interface MobileUserRepository extends JpaRepository<MobileUser, UUID> {

        List<MobileUser> findByUserNameContaining(String keyword);
}
