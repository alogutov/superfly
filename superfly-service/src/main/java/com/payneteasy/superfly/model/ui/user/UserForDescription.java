package com.payneteasy.superfly.model.ui.user;

import com.payneteasy.superfly.api.OTPType;

import java.io.Serializable;

import javax.persistence.Column;

public class UserForDescription implements Serializable {
    private long userId;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String secretQuestion;
    private String secretAnswer;
    private String publicKey;
    private String organization;
    private String  otpTypeCode = OTPType.NONE.code();
    private OTPType otpType;
    private boolean isOtpOptional;

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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

    @Column(name = "public_key")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Column(name="user_organization")
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public OTPType getOtpType() {
        if (otpType == null) {
            otpType = OTPType.fromCode(otpTypeCode);
        }
        return otpType;
    }

    @Column(name = "otp_code")
    public String getOtpTypeCode() {
        return otpTypeCode;
    }

    public void setOtpTypeCode(String otpTypeCode) {
        this.otpTypeCode = otpTypeCode;
    }

    @Column(name = "is_otp_optional")
    public boolean isOtpOptional() {
        return isOtpOptional;
    }

    public void setOtpOptional(boolean otpOptional) {
        isOtpOptional = otpOptional;
    }

    public boolean otpRequiredAndInit() {
        return getOtpType() != OTPType.NONE;
    }
    public boolean otpRequiredAndNotInit() {
        return getOtpType() != OTPType.NONE;
    }
}
