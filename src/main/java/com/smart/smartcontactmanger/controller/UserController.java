package com.smart.smartcontactmanger.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanger.DAO.ContactRepository;
import com.smart.smartcontactmanger.DAO.UserRepository;
import com.smart.smartcontactmanger.entity.Contact;
import com.smart.smartcontactmanger.entity.User;
import com.smart.smartcontactmanger.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    //method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model m,Principal principal){
        String userName = principal.getName();
        System.out.println("Username: "+userName);
        User user = userRepository.getUserByUserName(userName);
        System.out.println(user);
        m.addAttribute("user", user);
    }
    @RequestMapping("/index")
    public String dashboard(Model model,Principal principal){
        model.addAttribute("title", "User Dashboard");

        return "normal/user_dashboard";
    }
    //open add form handler
    @GetMapping("add-contact")
    public String addContact(Model model){
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add-contact";
    }
    //processign add contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult result,
        @RequestParam("image")MultipartFile file,Principal principal,Model model,HttpSession session){
        try {
            String name = principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            //processing and uploading file
            if(file.isEmpty()){
                System.out.println("file is empty");
                contact.setImage("/img/contact.png");
            if(result.hasErrors()){
                model.addAttribute("contact", contact);
                return "normal/add-contact";
            }
            }else{
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }         
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("Data "+contact);
            System.out.println("added to data base");
            // message success
            session.setAttribute("message", new Message("Your contact is Added", "success"));
        } catch (Exception e) {
            System.out.println("ERROR"+e.getMessage());
            e.printStackTrace();
            // message error
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }
        return "normal/add-contact";
    }

    //perpage = 5[n]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page, Model model,HttpSession session,Principal principal){
        
        //first method to get all contact
        // String username = principal.getName();
        // User user = this.userRepository.getUserByUserName(username);
        // List<Contact> contacts = user.getContacts();
        model.addAttribute("title", "View Contacts");
        //second method to get all contacts
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page,5);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show-contacts";
    }
    @RequestMapping("/{cId}/contact/")
    public String showContactDetails(@PathVariable("cId") Integer cId,
    Model model,Principal principal){
        System.out.println("cId"+cId);
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("title",contact.getName());
            model.addAttribute("contact",contact);
        }
        return "normal/contact_details";
    }
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,
    Model model,Principal principal,HttpSession session){
        Optional<Contact> contactoptional = this.contactRepository.findById(cId);
        Contact contact = contactoptional.get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId()){
            contact.setUser(null);
            this.contactRepository.delete(contact);
            session.setAttribute("message", new Message("Contact Deleted Succesfully", "success"));
        }
        return "redirect:/user/show-contacts/0";
    }
    @PostMapping("/update-contact/{cId}")
    public String updateContact(@PathVariable("cId")Integer cId,Model model){
        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "normal/update-contact";
    }
    @PostMapping("/process-update")
    public String processUpdateContact(@ModelAttribute("contact") Contact contact,
    @RequestParam("image") MultipartFile file,Model model,HttpSession session,Principal principal){
        try {
            //old contact details
            Contact oldContactDetail= this.contactRepository.findById(contact.getcId()).get();
            if(!file.isEmpty()){
                //delete old photo 
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1=new File(deleteFile, oldContactDetail.getImage());
                file1.delete();

                //upate new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            }else{
                contact.setImage(oldContactDetail.getImage());
            }
            User user=this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your contact is updated", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("COntact Name"+contact.getName());
        System.out.println("COntact id"+contact.getcId());
        return "redirect:/user/show-contacts/0";
    }
    
    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title", "Profile");
        return "normal/profile";
    }
}
