package ru.invite.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.logging.Logger;
import org.keycloak.authentication.actiontoken.execactions.ExecuteActionsActionToken;
import org.keycloak.common.util.Time;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.keycloak.services.resources.LoginActionsService;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailListener implements EventListenerProvider {

    private final KeycloakSession session;
    private static final Logger logger = Logger.getLogger(EmailListener.class);

    public EmailListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(org.keycloak.events.Event event) {

    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {

        if (event.getResourceType() != ResourceType.USER || event.getOperationType() != OperationType.CREATE) {
            return;
        }

        String resourcePath = event.getResourcePath();

        if (resourcePath == null) {
            return;
        }

        String[] split = resourcePath.split("/");

        if (split.length < 2 || !split[0].equals("users")) {
            return;
        }

        String userId = split[1];
        KeycloakContext context = session.getContext();
        RealmModel realm = context.getRealm();
        UserModel user = session.users().getUserById(realm, userId);

        if (user == null) {
            return;
        }

        String inviteEmail = user.getFirstAttribute(EmailListenerFactory.INVITE_ATTR_NAME);

        if (inviteEmail == null || inviteEmail.isEmpty()) {
            return;
        }

        ClientModel client = realm.getClientByClientId("account");

        if (client == null) {
            return;
        }

        ExecuteActionsActionToken token = new ExecuteActionsActionToken(
                userId,
                user.getEmail(),
                Time.currentTime() + 600,
                List.of(UserModel.RequiredAction.UPDATE_PASSWORD.name()),
                null,
                client.getClientId()
        );

        UriBuilder builder = LoginActionsService.actionTokenProcessor(session.getContext().getUri());
        builder.queryParam("key", token.serialize(session, realm, context.getUri()));
        String link = builder.build(realm.getName()).toString();

        sendEmail(inviteEmail, user, link);
    }


    private void sendEmail(String email, UserModel user, String link) {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        StringWriter writer = new StringWriter();

        try {
            cfg.setDirectoryForTemplateLoading(new File("/opt/keycloak/themes/invite"));
            cfg.setDefaultEncoding("UTF-8");

            Template template = cfg.getTemplate("welcome.ftl");
            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("link", link);
            template.process(data, writer);
        } catch (Exception e) {
            logger.error("Failed to render email template", e);
            return;
        }

        EmailSenderProvider sender = session.getProvider(EmailSenderProvider.class);

        String subject = "User created";
        String textBody = "";
        String htmlBody = writer.toString();

        try {
            sender.send(
                    session.getContext().getRealm().getSmtpConfig(),
                    email,
                    subject,
                    textBody,
                    htmlBody
            );
        } catch (Exception e) {
            logger.error("Failed to send invite message", e);
        }
    }

    @Override
    public void close() {

    }

}