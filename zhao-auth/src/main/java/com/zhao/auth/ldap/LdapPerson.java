package com.zhao.auth.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Data
public class LdapPerson {
    @Id
    @JsonIgnore
    private Name id;

    @DnAttribute(value = "uid")
    private String uid;

    @Attribute(name = "userPassword")
    private Object password;

    @Attribute(name = "cn")
    private String cn;

    @Attribute(name = "sn")
    private String sn;

    @Attribute(name = "mobile")
    private String mobile;

    @Attribute(name = "mail")
    private String mail;

    @Attribute(name = "businessCategory")
    private String businessCategory;

    @Attribute(name = "departmentNumber")
    private String departmentNumber;
}