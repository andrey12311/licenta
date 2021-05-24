package com.andrei.licenta.service.anunt;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AnuntService {
    List<Anunt> findAll();
    Optional<Anunt> findAnuntById(Long id);
    Anunt save(Anunt anunt);
    Anunt add(User user, LocalDate dateAdded, String description, String title, String city, String species,
              String county, String phoneNumber, MultipartFile image) throws IOException;
    void deleteById(Long id) throws AnuntNotFoundException, IOException;
}
