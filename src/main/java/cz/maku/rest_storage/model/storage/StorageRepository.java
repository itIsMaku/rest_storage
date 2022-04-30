package cz.maku.rest_storage.model.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    void deleteByIdentifierAndDataKey(String identifier, String dataKey);

    long countAllByIdentifier(String identifier);

    List<Storage> findAllByIdentifierAndDataKey(String identifier, String dataKey);

    @Transactional
    default Optional<Storage> findByIdentifierAndDataKey(String identifier, String dataKey) {
        return findAllByIdentifierAndDataKey(identifier, dataKey).stream().findFirst();
    }
}
