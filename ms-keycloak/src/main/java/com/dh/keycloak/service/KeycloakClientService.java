package com.dh.keycloak.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

@Service
public class KeycloakClientService {

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealm(String reino){
        // Creamos el reino
        RealmsResource realmsResource = keycloak.realms();
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(reino);
        realm.setEnabled(true);
        keycloak.realms().create(realm);
        System.out.println("Reino creado exitosamente: " + reino);

        // Seteamos roles a nivel Reino
        RolesResource rolesResource = realmsResource.realm(reino).roles();
        RoleRepresentation rolAdmin = new RoleRepresentation();
        rolAdmin.setName("app_admin");
        RoleRepresentation rolUser = new RoleRepresentation();
        rolUser.setName("app_user");
        List<RoleRepresentation> rolesReino = List.of(rolAdmin, rolUser);
        for (RoleRepresentation rol : rolesReino) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(rol.getName());
            rolesResource.create(roleRepresentation);
        }
    }

    public void createClient(String reino, String clientId, String clientSecret, List<String> roles) {
        // Verificamos que se haya creado el reino y creamos los clientes
        RealmsResource realmsResource =  keycloak.realms();
        RealmRepresentation realmRepresentation = realmsResource.realm(reino).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, reino)) {
            RealmResource realmResource = keycloak.realm(reino);
            ClientsResource clientsResource = realmResource.clients();

            // Creamos cliente
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId(clientId);
            client.setSecret(clientSecret);
            client.setServiceAccountsEnabled(true);
            client.setDirectAccessGrantsEnabled(true);
            client.setEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response response = clientsResource.create(client);
            if (response.getStatus() == 201) {
                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
                String createdClientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("ClientId: " + createdClientId + ". Roles: " + roles);

                // Obtenemos id de clientes
                ClientResource clientResource = clientsResource.get(createdClientId);
                // Seteamos roles a nivel Cliente
                for (String rol : roles) {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(rol);
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setContainerId(createdClientId);

                    clientResource.roles().create(roleRepresentation);
                }
                System.out.println("Cliente creado: " + clientId);
            } else {
                System.out.println("Error: " + response);
            }

        }
    }

    public void createGatewayClient(String reino, String clientId, String clientSecret, List<String> roles) {
        // Verificamos que se haya creado el reino y creamos los clientes
        RealmsResource realmsResource =  keycloak.realms();
        RealmRepresentation realmRepresentation = realmsResource.realm(reino).toRepresentation();
        String realmName = realmRepresentation.getRealm();
        if(Objects.equals(realmName, reino)) {
            RealmResource realmResource = keycloak.realm(reino);
            ClientsResource gwResource = realmResource.clients();

            // Creamos gateway
            ClientRepresentation gateway = new ClientRepresentation();
            String url = "http://localhost:9090";
            gateway.setClientId(clientId);
            gateway.setSecret(clientSecret);
            gateway.setRootUrl(url);
            gateway.setWebOrigins(List.of("/*"));
            gateway.setRedirectUris(List.of(url+"/*"));
            gateway.setAdminUrl(url);
            gateway.setEnabled(true);
            gateway.setServiceAccountsEnabled(true);
            gateway.setDirectAccessGrantsEnabled(true);

            // Verificamos que se hayan creado los clientes y creamos los roles a nivel Cliente
            Response responseGW = gwResource.create(gateway);
            if (responseGW.getStatus() == 201) {
                // Obtiene el ultimo segmento de la url, en este caso seria el clientId
                String createdClientIdGW = responseGW.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                System.out.println("GW: " + createdClientIdGW + ". Roles: " + roles);

                // Obtenemos id de clientes
                ClientResource clientResourceGW = gwResource.get(createdClientIdGW);
                // Seteamos roles a nivel Cliente
                for (String rol : roles) {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(rol);
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setContainerId(createdClientIdGW);

                    clientResourceGW.roles().create(roleRepresentation);
                }
                System.out.println("Cliente creado: " + clientId);
            } else {
                System.out.println("Error: " + responseGW);
            }
        }
    }

    public void createGroup(String reino, String grupo) {
        // Verificamos que se haya creado el reino y creamos el grupo
        RealmsResource realmsResource = keycloak.realms();
        RealmResource realm = realmsResource.realm(reino);

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(grupo);

        Response response = realm.groups().add(groupRepresentation);
        if (response.getStatus() == 201) {
            System.out.println("Grupo creado: " + grupo + ". Response= " + response);
        } else {
            System.out.println("Error: " + response);
        }
    }

    public void addGroupsToToken(String reino, String scope) {
        RealmResource realmResource = keycloak.realm(reino);
        List<ClientScopeRepresentation> scopes = realmResource.clientScopes().findAll();
        ClientScopeRepresentation clientScope = scopes.stream()
                .filter(cs -> cs.getName().equals(scope))
                .findFirst()
                .orElse(null);

        String id = clientScope.getId();

        ProtocolMapperRepresentation groupMembership = new ProtocolMapperRepresentation();
        groupMembership.setName("group");
        groupMembership.setProtocol("openid-connect");
        groupMembership.setProtocolMapper("oidc-group-membership-mapper");
        groupMembership.getConfig().put("full.path", "false");
        groupMembership.getConfig().put("access.token.claim", "true");
        groupMembership.getConfig().put("id.token.claim", "true");
        groupMembership.getConfig().put("userinfo.token.claim", "true");
        groupMembership.getConfig().put("claim.name", "groups");

        ClientScopeResource clientScopeResource = realmResource.clientScopes().get(id);
        clientScopeResource.getProtocolMappers().createMapper(groupMembership);

        ClientScopeRepresentation updatedClientScope = clientScopeResource.toRepresentation();
        clientScopeResource.update(updatedClientScope);

        System.out.println("GRUPOS AÑADIDOS A TOKEN");
    }

}
