This Keycloak plugin adds the ability to send invitation-like emails to custom address.
It can be used for the onboarding flow of new hires.

After installation, it creates "Invite to" user attribute, registers itself as event listener in realm settings, and
creates Email template in "/opt/keycloak/themes/invite".