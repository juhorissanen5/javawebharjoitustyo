package com.example.javawebharjoitustyo;

import java.util.Locale;
import java.util.ResourceBundle;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("contact")
@PageTitle("Contact Us")
public class ContactView extends VerticalLayout {
    private ResourceBundle messages;
    private H1 title;
    private TextField name;
    private EmailField email;
    private TextArea message;
    private Button submit;
    private Span copyright;
    private Span rights;
    private Button contactUs;
    private Button about;
    private Span followUs;

    public ContactView() {
        addClassName("contact-view");
        messages = ResourceBundle.getBundle("messages", new Locale("en"));
        
        // Language selector
        ComboBox<String> languageSelect = new ComboBox<>();
        languageSelect.setItems("English", "Suomi");
        languageSelect.setValue("English");
        languageSelect.addValueChangeListener(event -> {
            String language = event.getValue();
            Locale locale = language.equals("Suomi") ? new Locale("fi") : new Locale("en");
            messages = ResourceBundle.getBundle("messages", locale);
            updateTexts();
        });

        setupComponents();
        updateTexts();
        
        FormLayout form = new FormLayout();
        form.add(name, email, message, submit);
        
        add(languageSelect, title, form, createFooter());
    }

    private void setupComponents() {
        title = new H1();
        name = new TextField();
        email = new EmailField();
        message = new TextArea();
        submit = new Button();
        copyright = new Span();
        rights = new Span();
        contactUs = new Button();
        about = new Button();
        followUs = new Span();
    }

    private void updateTexts() {
        title.setText(messages.getString("contact.title"));
        name.setLabel(messages.getString("contact.name"));
        email.setLabel(messages.getString("contact.email"));
        message.setLabel(messages.getString("contact.message"));
        submit.setText(messages.getString("contact.send"));
        copyright.setText(messages.getString("footer.copyright"));
        rights.setText(messages.getString("footer.rights"));
        contactUs.setText(messages.getString("footer.contact"));
        about.setText(messages.getString("footer.about"));
        followUs.setText(messages.getString("footer.followus"));
    }

    private Component createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("footer");
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        VerticalLayout leftSection = new VerticalLayout();
        leftSection.add(copyright, rights);

        VerticalLayout centerSection = new VerticalLayout();
        contactUs.addClickListener(e -> UI.getCurrent().navigate("contact"));
        about.addClickListener(e -> UI.getCurrent().navigate("about"));
        centerSection.add(contactUs, about);

        VerticalLayout rightSection = new VerticalLayout();
        rightSection.add(followUs);
        HorizontalLayout socialLinks = new HorizontalLayout();
        socialLinks.add(
            new Anchor("https://twitter.com", "Twitter"),
            new Anchor("https://facebook.com", "Facebook")
        );
        rightSection.add(socialLinks);

        footer.add(leftSection, centerSection, rightSection);
        return footer;
    }
}