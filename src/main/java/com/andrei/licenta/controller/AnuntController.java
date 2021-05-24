package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.service.anunt.AnuntService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static com.andrei.licenta.constants.FileConstant.ANUNT_FOLDER;
import static com.andrei.licenta.constants.FileConstant.FORWARD_SLASH;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/anunturi-api")
public class AnuntController {

    AnuntService anuntService;

    public AnuntController(AnuntService anuntService) {
        this.anuntService = anuntService;
    }

    @GetMapping("/anunturi")
    public ResponseEntity<List<Anunt>> findAll() {
        List<Anunt> anunturi = anuntService.findAll();

        return new ResponseEntity<>(anunturi, OK);
    }

    @GetMapping("/anunturi/{id}")
    public ResponseEntity<Anunt> findOne(@PathVariable("id") Long id) throws AnuntNotFoundException {
        Anunt anunt = anuntService.findAnuntById(id)
                .orElseThrow(() -> new AnuntNotFoundException("Anuntul nu mai este valabil"));
        return new ResponseEntity<>(anunt, OK);
    }

    @PreAuthorize("hasAuthority('anunt:add')")
    @PostMapping("/add")
    public ResponseEntity<Anunt> add(@RequestParam("user") User user,
                                     @RequestParam("description") String description,
                                     @RequestParam("title") String title,
                                     @RequestParam("city") String city,
                                     @RequestParam("species") String species,
                                     @RequestParam("county") String county,
                                     @RequestParam("phoneNumber") String phoneNumber,
                                     @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Anunt anunt = anuntService.add(user, LocalDate.now(), description, title, city,
                species, county, phoneNumber, image);
        return new ResponseEntity<>(anunt, OK);
    }

    @GetMapping(value = "/image/{email}/{anuntId}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("email") String email,
                           @PathVariable("fileName") String fileName, @PathVariable("anuntId") String anuntId) throws IOException {
        return Files.readAllBytes(Paths.get((ANUNT_FOLDER) + email + FORWARD_SLASH + anuntId + FORWARD_SLASH + fileName));
    }

    @PreAuthorize("hasAuthority('anunt:delete')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteAnunt(@PathVariable("id") Long id) throws AnuntNotFoundException, IOException {
        Anunt anunt = anuntService.findAnuntById(id).orElseThrow(() -> new AnuntNotFoundException("Anuntul nu exista"));
        anuntService.deleteById(id);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK.value(),HttpStatus.OK,HttpStatus.OK.getReasonPhrase()
        ,"Anuntul a fost sters");

        return new ResponseEntity<>(httpResponse,OK);
    }
}
