package com.javaspringboot.javaspringbootcore.app.email.service;


import com.javaspringboot.javaspringbootcore.app.email.model.Mail;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;

public interface MailService
{
    public void sendSignUpEmail(User user, String code);

}