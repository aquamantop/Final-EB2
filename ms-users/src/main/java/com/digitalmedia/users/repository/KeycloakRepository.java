package com.digitalmedia.users.repository;

import com.digitalmedia.users.model.Bill;
import com.digitalmedia.users.model.User;
import com.digitalmedia.users.repository.feign.BillsFeignRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class KeycloakRepository implements UserRepository {

    private final Keycloak keycloakClient;
    private final BillsFeignRepository billsFeignRepository;
    private String reino = "facturacion-realm";

    private User toUser(UserRepresentation userRepresentation) {
        return User.builder()
                .id(userRepresentation.getId())
                .userName(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .build();
    }

    @Override
    public User findById(String id){
        UserRepresentation userRepresentation = keycloakClient.realm(reino).users().get(id).toRepresentation();
        User user = toUser(userRepresentation);
        List<Bill> bills = billsFeignRepository.getCustomerBill(id);
        user.setBills(bills);

        return user;
    }

    @Override
    public List<User> addUsers(){
        RealmResource realmResource = keycloakClient.realm(reino);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        // Con permisos
        UserRepresentation usuario = new UserRepresentation();
        usuario.setUsername("franco");
        usuario.setFirstName("franco");
        usuario.setEmail("email@email.com");
        usuario.setGroups(List.of("PROVIDERS"));
        usuario.setEnabled(true);

        // Sin permisos
        UserRepresentation usuarioSP = new UserRepresentation();
        usuarioSP.setUsername("rodrigo");
        usuarioSP.setFirstName("rodrigo");
        usuarioSP.setEmail("emailSP@emailSP.com");
        usuarioSP.setEnabled(true);

        // Establecer password y dejarla como no temporal
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("1234");
        credentialRepresentation.setTemporary(false);

        usuario.setCredentials(List.of(credentialRepresentation));
        usuarioSP.setCredentials(List.of(credentialRepresentation));

        realmResource.users().create(usuario);
        realmResource.users().create(usuarioSP);

        System.out.println("USER CON PERMISOS: " + usuario.getId());
        System.out.println("USER CON SIN PERMISOS: " + usuarioSP.getId());

        UserRepresentation usuario1 = realmResource.users().search("franco", true).get(0);
        UserRepresentation usuario2 = realmResource.users().search("rodrigo", true).get(0);

        List<User> list = List.of(toUser(usuario1), toUser(usuario2));

        return list;
    }

}
