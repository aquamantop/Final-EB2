package com.digitalmedia.users.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public String id;

    public String userName;

    public String password;

    public String email;

    public String firstName;

    public List<Bill> bills;

}
