package com.example.javawebharjoitustyo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
public class AppShell implements AppShellConfigurator {
}