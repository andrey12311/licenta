package com.andrei.licenta.constants;

public class Authority {

    public static final String[] USER_AUTHORITIES = {"anunt:read","anunt:update","anunt:delete","anunt:add"};
    public static final String[] ADMIN_AUTHORITIES = {"user:read","user:update","user:create"
                    ,"user:delete","anunt:read","anunt:update","anunt:delete"};
}
