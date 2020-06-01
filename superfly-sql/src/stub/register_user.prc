drop procedure if exists register_user;
delimiter $$
create procedure register_user(i_user_name         varchar(32),
                               i_user_password     varchar(128),
                               i_user_email        varchar(255),
                               i_subsystem_name    varchar(32),
                               i_principal_list    text,
                               i_name 		       varchar(32),   
                               i_surname	       varchar(32),
                               i_secret_question   varchar(255),
                               i_secret_answer     varchar(255),
                               i_salt              varchar(128),
                               i_is_password_temp  varchar(1),
                               i_hotp_salt		   varchar(128),
                               i_public_key		   text, 			 
                               i_user_organization varchar(255),
                               i_otp_code          varchar(16),
                               out o_user_id       int(10)
                              )
 main_sql:
  begin
    declare v_completed varchar(1);
    declare v_otp_otp_type_id   int(10);

    select otp_type_id into v_otp_otp_type_id
      from otp_types where otp_code=i_otp_code;

	select user_id, completed from users where user_name = i_user_name into o_user_id, v_completed;
	
	if o_user_id is not null then
        if v_completed = 'N' then
            -- removing old uncompleted user
            delete from user_roles where user_user_id = o_user_id;
            delete from user_history where user_user_id = o_user_id;
            delete from users where user_id = o_user_id;
            set o_user_id = null;
        else
            select 'duplicate' status, 'User already exists' error_message;
            leave main_sql;
		end if;
	end if;
  
    insert into users(user_name, user_password
                      , email
                      , is_account_locked, `name`, surname, secret_question ,secret_answer
                      , is_password_temp,salt, hotp_salt, create_date, public_key, completed
                      , user_organization
                      , otp_otp_type_id )
         values (i_user_name, i_user_password, i_user_email, 'N', i_name, i_surname, i_secret_question,
                 i_secret_answer, i_is_password_temp,i_salt, i_hotp_salt, now(), i_public_key, 'N',
                 i_user_organization,v_otp_otp_type_id);

    set o_user_id   = last_insert_id();

    insert into user_history (user_user_id,user_password,salt,number_history,start_date,end_date)
         values (o_user_id, i_user_password, i_salt, 1, now(), '2999-12-31');

    
    if i_principal_list is not null then
	    insert into user_roles(user_user_id, role_role_id)
	      select o_user_id, r.role_id
	        from subsystems ss
	             join roles r
	               on r.ssys_ssys_id = ss.ssys_id
	             left join user_roles ur
	               on (r.role_id = ur.role_role_id and ur.user_user_id = o_user_id)
	       where     instr(concat(',', i_principal_list, ','), concat(',', r.principal_name, ','))
	             and ur.urol_id is null
	             and ss.subsystem_name = i_subsystem_name;
	end if;

    select 'OK' status, null error_message;
  end
$$
delimiter ;
call save_routine_information('register_user', concat_ws(',', 'status varchar', 'error_message varchar'));