package com.smartcontactmanager.smart_contact_manager_backend.DemoUser;

import com.smartcontactmanager.smart_contact_manager_backend.Models.Contact;
import com.smartcontactmanager.smart_contact_manager_backend.Models.SocialLink;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Providers.LoginProviders;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.ContactRepository;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DemoUser implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Override
    public void run(String... args) {
        User user = userRepository.findByEmail("user@gmail.com").orElseGet(() -> {
            User demoUser = User.builder()
                    .userId("1")
                    .name("DemoUser")
                    .email("user@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber("")
                    .enabled(true)
                    .emailVerified(true)
                    .provider(LoginProviders.SELF)
                    .profilePic("https://imgs.search.brave.com/eLZU3CJWS_tIS6Aqmobqw-EZqg9uNyDMK5gsdt2sndw/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly80a3dh/bGxwYXBlcnMuY29t/L2ltYWdlcy93YWxs/cy90aHVtYnNfMnQv/NjAzNy5qcGc")
                    .build();
            return userRepository.save(demoUser);
        });

        if (contactRepository.countByUser(user) == 0) {
            List<Contact> contacts = List.of(
                    createContact(
                            user,
                            "Ava Thompson",
                            "ava.thompson@example.com",
                            "9135551042",
                            "221B Baker Street, London",
                            "Product designer working on consumer apps.",
                            true,
                            "https://www.linkedin.com/company/google/posts/?feedView=all",
                            List.of(
                                    new LinkSeed("GitHub", "https://github.com/google"),
                                    new LinkSeed("Portfolio", "https://dribbble.com")
                            )
                    ),
                    createContact(
                            user,
                            "Noah Martinez",
                            "noah.martinez@example.com",
                            "6465558831",
                            "14 W 26th St, New York",
                            "Backend engineer focused on distributed systems.",
                            false,
                            "https://www.linkedin.com/company/amazon/posts/?feedView=all",
                            List.of(
                                    new LinkSeed("GitHub", "https://github.com/torvalds"),
                                    new LinkSeed("X", "https://x.com/github")
                            )
                    ),
                    createContact(
                            user,
                            "Emma Wilson",
                            "emma.wilson@example.com",
                            "4085552704",
                            "1 Infinite Loop, Cupertino",
                            "Marketing lead with strong B2B growth experience.",
                            true,
                            "https://www.linkedin.com/company/google/",
                            List.of(
                                    new LinkSeed("Website", "https://openai.com"),
                                    new LinkSeed("YouTube", "https://www.youtube.com/@Google")
                            )
                    ),
                    createContact(
                            user,
                            "Liam Johnson",
                            "liam.johnson@example.com",
                            "3035556197",
                            "1600 Amphitheatre Pkwy, Mountain View",
                            "Data analyst passionate about dashboards and KPIs.",
                            false,
                            "https://www.linkedin.com/company/microsoft/",
                            List.of(
                                    new LinkSeed("GitHub", "https://github.com/microsoft"),
                                    new LinkSeed("Blog", "https://www.microsoft.com/en-us/microsoft-365/blog/")
                            )
                    ),
                    createContact(
                            user,
                            "Sophia Clark",
                            "sophia.clark@example.com",
                            "5125557320",
                            "500 Terry A Francois Blvd, San Francisco",
                            "Startup founder building fintech products.",
                            true,
                            "https://www.linkedin.com/company/stripe/",
                            List.of(
                                    new LinkSeed("Website", "https://stripe.com"),
                                    new LinkSeed("X", "https://x.com/stripe")
                            )
                    )
            );
            contactRepository.saveAll(contacts);
        }
    }

    private Contact createContact(
            User user,
            String name,
            String email,
            String phoneNumber,
            String address,
            String description,
            boolean favourite,
            String linkedInLink,
            List<LinkSeed> links
    ) {
        Contact contact = Contact.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .address(address)
                .description(description)
                .favourite(favourite)
                .linkedInLink(linkedInLink)
                .user(user)
                .build();

        List<SocialLink> socialLinks = new ArrayList<>();
        for (LinkSeed seed : links) {
            socialLinks.add(new SocialLink(null, seed.title(), seed.url(), contact));
        }
        contact.setSocialLinks(socialLinks);
        return contact;
    }

    private record LinkSeed(String title, String url) {}
}
