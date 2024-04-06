package com.zhao.auth.ldap;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class PersonAttributesMapper implements AttributesMapper<LdapPerson> {
    @Override
    public LdapPerson mapFromAttributes(Attributes attributes) throws NamingException {
        LdapPerson ldapPerson = new LdapPerson();
        // 从 LDAP 属性中提取值并映射到 Java 对象的字段或属性
        ldapPerson.setUid((String) attributes.get("uid").get());
        ldapPerson.setCn((String) attributes.get("cn").get());
        // 返回映射后的 Java 对象
        return ldapPerson;
    }
}
