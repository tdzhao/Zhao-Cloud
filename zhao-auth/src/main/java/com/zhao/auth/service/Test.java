package com.zhao.auth.service;

import com.zhao.auth.ldap.LdapPerson;
import com.zhao.auth.ldap.PersonAttributesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {


    private static String urls = "ldap://127.0.0.1:389/";
    private static String base= "dc=maxcrc,dc=com";
    private static String username= "cn=Manager,dc=maxcrc,dc=com";
    private static String password= "123456";

    private static LdapTemplate ldapTemplate ;
    public static LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        Map<String, Object> config = new HashMap<>();
        // 处理乱码
        config.put("java.naming.ldap.attributes.binary", "objectGUID");

        contextSource.setUrl(urls);
        contextSource.setBase(base);
        contextSource.setUserDn(username);
        contextSource.setPassword(password);
        // 不使用已经创建好连接
        //  如果使用已经创建连接，去验证，总是失败的
        contextSource.setPooled(true);
        contextSource.setBaseEnvironmentProperties(config);
        // 验证必填参数都设置好
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    public static LdapTemplate ldapTemplate() {
        if (ldapTemplate == null) {
            ldapTemplate = new LdapTemplate(contextSource());
        }
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }

    public static void main(String[] args){
        ldapTemplate();
        String user = "ldap_user_1";
        String pd = "ldap_user_1";
        LdapQuery query = LdapQueryBuilder.query().where("uid").is(user);
        ldapTemplate.authenticate(query, pd);
       ldapTemplate.search(query, new PersonAttributesMapper()).forEach(System.out::println);
    }
}
