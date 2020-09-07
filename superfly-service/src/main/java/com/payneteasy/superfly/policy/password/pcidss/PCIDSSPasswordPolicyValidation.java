package com.payneteasy.superfly.policy.password.pcidss;

import com.payneteasy.superfly.policy.impl.AbstractPolicyValidation;
import com.payneteasy.superfly.policy.password.PasswordCheckContext;

/**
 * Kuccyp
 * Date: 07.10.2010
 * Time: 11:23:17
 * (C) 2010
 * Skype: kuccyp
 */

//TODO make get parameters from spring for example 
public class PCIDSSPasswordPolicyValidation extends AbstractPolicyValidation<PasswordCheckContext>{
    private final static int MIN_LEN=7;
    private final static int HISTORY_DEPTH=4;

    @Override
    protected void init() {
        addPolicy(new PasswordMinLen(MIN_LEN));
        addPolicy(new PasswordComplex());
        addPolicy(new PasswordAlreadyExist(HISTORY_DEPTH));
    }
}
