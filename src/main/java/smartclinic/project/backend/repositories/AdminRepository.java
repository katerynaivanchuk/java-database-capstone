package smartclinic.project.backend.repositories; // Змініть пакет відповідно до вашої структури проєкту

import smartclinic.project.backend.models.Admin; // Змініть імпорт моделі Admin за потреби
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Знаходить адміністратора за його унікальним іменем користувача (username).
     *
     * @param username ім'я користувача для пошуку
     * @return об'єкт Admin або null, якщо користувача не знайдено
     */
    Admin findByUsername(String username);
}