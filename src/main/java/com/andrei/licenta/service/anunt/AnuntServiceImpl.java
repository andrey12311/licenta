package com.andrei.licenta.service.anunt;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.repository.anunt.AnuntRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.andrei.licenta.constants.FileConstant.*;


@Service
public class AnuntServiceImpl implements AnuntService {
    private final AnuntRepository anuntRepository;

    public AnuntServiceImpl(AnuntRepository anuntRepository) {
        this.anuntRepository = anuntRepository;
    }

    @Override
    public List<Anunt> findAll() {
        return this.anuntRepository.findAll();
    }

    @Override
    public Optional<Anunt> findAnuntById(Long id) {
        return this.anuntRepository.findById(id);
    }

    @Override
    public Anunt save(Anunt anunt) {
        return this.anuntRepository.save(anunt);
    }

    @Override
    public Anunt add(User user, LocalDate dateAdded, String description,
                     String title, String city, String species, String county, String phoneNumber, MultipartFile image)
            throws IOException {

        Anunt anunt = new Anunt();
        String anuntId = UUID.randomUUID().toString();
        anunt.setCity(city);
        anunt.setCounty(county);
        anunt.setDateAdded(dateAdded);
        anunt.setDescription(description);
        anunt.setTitle(title);
        anunt.setSpecies(species);
        anunt.setPhoneNumber(phoneNumber);
        anunt.setUser(user);
        anunt.setAnuntId(anuntId);
        saveImage(anunt, image);
        return anunt;
    }

    @Override
    public void deleteById(Long id) throws AnuntNotFoundException, IOException {
        Anunt anunt = findAnuntById(id).orElseThrow(() -> new AnuntNotFoundException("Anuntul n-a fost gasit"));
        Path anuntImagePath =
                Paths.get(ANUNT_FOLDER + anunt.getUser().getEmail() + FORWARD_SLASH + anunt.getAnuntId()).
                        toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(anuntImagePath.toAbsolutePath().toString()));
        anuntRepository.deleteById(id);
    }

    private void saveImage(Anunt anunt, MultipartFile image) throws IOException {
        if (image != null) {
            // /desktop/licenta/poze_anunturi/anuntId
            Path anuntFolder = Paths.get(ANUNT_FOLDER + anunt.getUser().getEmail() +
                    FORWARD_SLASH + anunt.getAnuntId()).toAbsolutePath().normalize();
            if (!Files.exists(anuntFolder)) {
                Files.createDirectories(anuntFolder);
                Files.copy(image.getInputStream(), anuntFolder.resolve("poza" + DOT + JPG_EXTENSION));
                anunt.setImage(setImageUrl(anunt.getAnuntId(), anunt.getUser().getEmail()));
                anuntRepository.save(anunt);
            }
        }
    }

    private String setImageUrl(String anuntId, String email) {
        System.out.println(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(ANUNT_IMAGE_PATH + email+FORWARD_SLASH + anuntId
                        + FORWARD_SLASH + "poza" + DOT + JPG_EXTENSION).toUriString());
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(ANUNT_IMAGE_PATH + email+FORWARD_SLASH + anuntId
                        + FORWARD_SLASH + "poza" + DOT + JPG_EXTENSION).toUriString();
    }
}
