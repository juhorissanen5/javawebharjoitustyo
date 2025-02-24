package com.example.javawebharjoitustyo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("about")
@PageTitle("About Us")
public class AboutView extends VerticalLayout {
    
    public AboutView() {
        addClassName("about-view");
        
        H1 title = new H1("About Health Monitoring");
        Paragraph description = new Paragraph("We are dedicated to providing the best health monitoring solutions...");
        
        add(title, description, createFooter());
    }
    
    private Component createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("footer");
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        VerticalLayout leftSection = new VerticalLayout();
        leftSection.add(new Span("© 2025 Health Monitoring System"));
        leftSection.add(new Span("All rights reserved"));

        VerticalLayout centerSection = new VerticalLayout();
        Button contactUs = new Button("Contact Us");
        Button about = new Button("About");
        contactUs.addClickListener(e -> UI.getCurrent().navigate("contact"));
        about.addClickListener(e -> UI.getCurrent().navigate("about"));
        centerSection.add(contactUs, about);

        VerticalLayout rightSection = new VerticalLayout();
        rightSection.add(new Span("Follow us:"));
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