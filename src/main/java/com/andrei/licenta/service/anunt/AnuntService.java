package com.andrei.licenta.service.anunt;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnuntService {
    List<Anunt> findAll();

    Optional<Anunt> findAnuntById(Long id);

    Anunt add(User user, LocalDate dateAdded, String description, String title, String city, String species,
              String county, String phoneNumber, MultipartFile image) throws IOException;

    void deleteById(Long id) throws AnuntNotFoundException, IOException;

    Anunt update(String id, String description, String title, String city, String species,
                 String county, String phoneNumber) throws AnuntNotFoundException;

    List<Anunt> findAccepted();

    void acceptAnunt(Long id) throws AnuntNotFoundException, MessagingException;

    void denyAnunt(Long id) throws MessagingException, AnuntNotFoundException, IOException;

    Long countAnunturiActive();

    Long countAnunturiNoi();

    List<Anunt> findAnunturiInAsteptare();

    void deleteAnunt(Long id) throws AnuntNotFoundException, IOException, MessagingException;

    Page<Anunt> findPaged(Pageable pageable);

}
