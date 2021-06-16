package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.AnuntNotFoundException;
import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.UserNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.service.anunt.AnuntService;
import com.andrei.licenta.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/admin-api")
public class AdminController {
    private final UserService userService;
    private final AnuntService anuntService;

    public AdminController(UserService userService, AnuntService anuntService) {
        this.userService = userService;
        this.anuntService = anuntService;
    }

    @PreAuthorize("hasAnyAuthority('user:read')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.findAll();

        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) throws UserNotFoundException {
        User user = userService.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("Utilizatorul nu a fost gasit"));
        return new ResponseEntity<>(user, OK);
    }


//    @GetMapping("/toate-anunturile")
//    @PreAuthorize("hasAnyAuthority('user:read')")
//    public ResponseEntity<List<Anunt>> getAnunturi() {
//        List<Anunt> anunturi = this.anuntService.findAll();
//
//        return new ResponseEntity<>(anunturi, OK);
//    }

    @GetMapping("/toate-anunturile")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<List<Anunt>> getAnunturi(@RequestParam("page")Integer page) {
        Pageable firstPageWithTwoElemensts = PageRequest.of(page,5, Sort.by("dateAdded").descending());
        Page<Anunt> myPage = anuntService.findPaged(firstPageWithTwoElemensts);

        List<Anunt> anunturi = new ArrayList<>();
        for(Anunt a : myPage.getContent()){
            if(a.getIsAccepted()){
                anunturi.add(a);
            }
        }

        return new ResponseEntity<>(anunturi, OK);
    }

    @GetMapping("anunturi-asteptare")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<List<Anunt>> getAnunturiInAsteptare() {

        List<Anunt> anunturiInAstaptere = this.anuntService.findAnunturiInAsteptare();

        return new ResponseEntity<>(anunturiInAstaptere, OK);
    }


    @PreAuthorize("hasAnyAuthority('user:read')")
    @PatchMapping("/accept")
    public ResponseEntity<Anunt> acceptAnunt(@RequestBody Anunt anunt)
            throws AnuntNotFoundException, MessagingException {
        anuntService.acceptAnunt(anunt.getId());

        return new ResponseEntity<>(anunt, OK);
    }

    @DeleteMapping("/deny/{id}")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<HttpResponse> denyAnunt(@PathVariable("id") Long id)
            throws MessagingException, AnuntNotFoundException, IOException {
        anuntService.denyAnunt(id);

        return createHttpResponse(OK, "Anuntul a fost sters");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<HttpResponse> delete(@PathVariable("id") Long id)
            throws MessagingException, AnuntNotFoundException, IOException {
        anuntService.deleteAnunt(id);

        return createHttpResponse(OK, "Anuntul a fost sters");
    }

    @GetMapping("/count-anunturi")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public Long countAnunturi() {
        return this.anuntService.countAnunturiActive();
    }

    @GetMapping("/count-anunturi-noi")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public Long countAnunturiNoi() {
        return this.anuntService.countAnunturiNoi();
    }


    @GetMapping("/count-users")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public Long countUsers() {
        return this.userService.countUsers();
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    @PreAuthorize("hasAuthority('anunt:update')")
    @PostMapping("/update-user")
    public ResponseEntity<User> update(@RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("email") String email,
                                        @RequestParam("isNonLocked") String isNonLocked,
                                        @RequestParam("id") String id)
            throws UserNotFoundException, MessagingException, EmailExistsException {

        System.out.println(isNonLocked);
        String goodBoolean = isNonLocked.replace("\"", "");//scot ghilimelele din string ca sa functioneze parse
        Boolean nonLocked = Boolean.parseBoolean(goodBoolean);
        System.out.println(nonLocked);
        User user = userService.update(id,firstName,lastName,email,nonLocked);

        return new ResponseEntity<>(user, OK);
    }
}
