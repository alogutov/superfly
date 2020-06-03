package com.payneteasy.superfly.model.ui.user;

import com.payneteasy.superfly.api.OTPType;
import com.payneteasy.superfly.model.ui.subsystem.UISubsystemForFilter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * User object to be used in the UI (create/update).
 * 
 * @author Roman Puchkovskiy
 */
public class UIUser implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String surname;
    private String secretQuestion;
    private String secretAnswer;
    private String salt;
    private String  publicKey;
    private String  otpType = OTPType.NONE.code();
    private boolean isOtpOptional;
    private UISubsystemForFilter subsystemForEmail;
    private String organization;

    @Column(name = "user_id")
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "user_name")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "user_password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "user_email")
    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(name = "secret_question")
    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    @Column(name = "secret_answer")
    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    @Column(name="salt")
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Column(name = "public_key")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Column(name = "otp_code")
    public String getOtpType() {
        return otpType;
    }

    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    @Column(name = "is_otp_optional")
    public boolean isOtpOptional() {
        return isOtpOptional;
    }

    public void setOtpOptional(boolean otpOptional) {
        isOtpOptional = otpOptional;
    }

    @Transient
    public UISubsystemForFilter getSubsystemForEmail() {
        return subsystemForEmail;
    }

    public void setSubsystemForEmail(UISubsystemForFilter subsystemForEmail) {
        this.subsystemForEmail = subsystemForEmail;
    }

    @Column(name="user_organization")
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
