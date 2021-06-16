package com.andrei.licenta.service.anunt;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.repository.anunt.AnuntRepository;
import com.andrei.licenta.service.email.EmailService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
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
    private final EmailService emailService;

    public AnuntServiceImpl(AnuntRepository anuntRepository, EmailService emailService) {
        this.anuntRepository = anuntRepository;
        this.emailService = emailService;
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
        anunt.setIsAccepted(false);
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

    @Override
    public Anunt update(String id, String description, String title,
                        String city, String species, String county, String phoneNumber) throws AnuntNotFoundException {

        Anunt anunt = anuntRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new AnuntNotFoundException("Anuntul nu a fost gasit"));

        anunt.setDescription(description);
        anunt.setPhoneNumber(phoneNumber);
        anunt.setTitle(title);
        anunt.setCity(city);
        anunt.setCounty(county);
        anunt.setSpecies(species);

        anuntRepository.save(anunt);

        return anunt;
    }

    @Override
    public List<Anunt> findAccepted() {
        return this.anuntRepository.findAccepted();
    }

    @Override
    public void acceptAnunt(Long id) throws AnuntNotFoundException, MessagingException {
       int n = this.anuntRepository.acceptAnunt(id);
        Anunt anunt = anuntRepository.findById(id).orElseThrow(
                ()->new AnuntNotFoundException("Anuntul nu a fost gasit"));
       if(n==1){
           emailService.sendEmail(anunt.getUser().getEmail(),"Buna ziua,anuntul dumneavoastra a fost acceptat,");
           System.out.println(anunt.getUser().getEmail());
       }
    }

    @Override
    public void denyAnunt(Long id) throws MessagingException, AnuntNotFoundException, IOException {
        Anunt anunt = anuntRepository.findById(id).orElseThrow(
                ()->new AnuntNotFoundException("Anuntul nu a fost gasit"));
        Path anuntImagePath =
                Paths.get(ANUNT_FOLDER + anunt.getUser().getEmail() + FORWARD_SLASH + anunt.getAnuntId()).
                        toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(anuntImagePath.toAbsolutePath().toString()));
        anuntRepository.deleteById(id);
        emailService.sendEmail(anunt.getUser().getEmail(),"Buna ziua,anuntul dumneavoastra a fost respins \n" +
                "Va rugam sa il mai adaugati o data");
    }

    @Override
    public Long countAnunturiActive() {
        return this.anuntRepository.countAnunturActive();
    }

    @Override
    public Long countAnunturiNoi() {
        return this.anuntRepository.countAnunturiNoi();
    }

    @Override
    public List<Anunt> findAnunturiInAsteptare() {
        return this.anuntRepository.findAnunturiInAstetpare();
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

    @Override
    public void deleteAnunt(Long id ) throws AnuntNotFoundException, IOException, MessagingException {
        Anunt anunt = anuntRepository.findById(id).orElseThrow(
                ()->new AnuntNotFoundException("Anuntul nu a fost gasit"));

        Path anuntImagePath =
                Paths.get(ANUNT_FOLDER + anunt.getUser().getEmail() + FORWARD_SLASH + anunt.getAnuntId()).
                        toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(anuntImagePath.toAbsolutePath().toString()));

        emailService.sendEmail(anunt.getUser().getEmail(),"Buna ziua,anuntul dumneavoastra a fost sters " +
                "de catre administrator.");
        this.anuntRepository.deleteById(id);
    }

    private String setImageUrl(String anuntId, String email) {
        System.out.println(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(ANUNT_IMAGE_PATH + email + FORWARD_SLASH + anuntId
                        + FORWARD_SLASH + "poza" + DOT + JPG_EXTENSION).toUriString());
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(ANUNT_IMAGE_PATH + email + FORWARD_SLASH + anuntId
                        + FORWARD_SLASH + "poza" + DOT + JPG_EXTENSION).toUriString();
    }


    @Override
    public Page<Anunt> findPaged(Pageable pageable) {
        return this.anuntRepository.findAll(pageable);
    }
}
