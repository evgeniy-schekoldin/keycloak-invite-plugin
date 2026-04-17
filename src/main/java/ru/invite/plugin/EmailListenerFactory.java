package ru.invite.plugin;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPAttributePermissions;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.keycloak.userprofile.UserProfileProvider;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Set;

public class EmailListenerFactory implements EventListenerProviderFactory {

    public static final String ID = "email-listener";
    private static final Logger logger = Logger.getLogger(EmailListener.class);

    //public EmailListenerFactory() {
    //    System.out.println("🔥 FACTORY LOADED");
    //}

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new EmailListener(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                KeycloakModelUtils.runJobInTransaction(factory, session -> {
                    session.realms().getRealmsStream().forEach(realm -> {
                        try {

                            session.getContext().setRealm(realm);
                            UserProfileProvider provider = session.getProvider(UserProfileProvider.class);

                            if (provider == null) {
                                return;
                            }

                            UPConfig config = provider.getConfiguration();

                            if (config == null) {
                                return;
                            }

                            String attrName = "_invite_to";
                            boolean exists = config.getAttributes() != null &&
                                    config.getAttributes().stream().anyMatch(a -> attrName.equals(a.getName()));

                            if (exists) {
                                return;
                            }

                            UPAttribute attribute = new UPAttribute();
                            attribute.setName(attrName);
                            attribute.setDisplayName("Invite to");

                            UPAttributePermissions permissions = new UPAttributePermissions();
                            permissions.setView(Set.of("admin"));
                            permissions.setEdit(Set.of("admin"));
                            attribute.setPermissions(permissions);

                            if (config.getAttributes() == null) {
                                config.setAttributes(new ArrayList<>());
                            }

                            config.getAttributes().add(attribute);
                            provider.setConfiguration(config);

                        } catch (Exception e) {
                            logger.error("Failed to edit realm settings", e);
                        }
                    });
                });
            }
        });
    }

    @Override
    public void close() {

    }
}