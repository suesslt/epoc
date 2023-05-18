create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
create sequence abstract_simulation_order_seq start with 1 increment by 50;
create sequence account_seq start with 1 increment by 50;
create sequence booking_seq start with 1 increment by 50;
create sequence company_seq start with 1 increment by 50;
create sequence company_simulation_step_seq start with 1 increment by 50;
create sequence distribution_in_market_seq start with 1 increment by 50;
create sequence distribution_step_seq start with 1 increment by 50;
create sequence epoc_setting_seq start with 1 increment by 50;
create sequence epoc_settings_seq start with 1 increment by 50;
create sequence factory_seq start with 1 increment by 50;
create sequence financial_accounting_seq start with 1 increment by 50;
create sequence journal_entry_seq start with 1 increment by 50;
create sequence login_seq start with 1 increment by 50;
create sequence market_seq start with 1 increment by 50;
create sequence market_simulation_seq start with 1 increment by 50;
create sequence message_seq start with 1 increment by 50;
create sequence simulation_seq start with 1 increment by 50;
create sequence simulation_step_seq start with 1 increment by 50;
create sequence storage_seq start with 1 increment by 50;
create sequence user_in_company_role_seq start with 1 increment by 50;

    create table abstract_simulation_order (
       dtype varchar(31) not null,
        id integer not null,
        execution_month date,
        is_executed boolean not null,
        adjust_amount numeric(38,2),
        adjust_currency varchar(255),
        direction smallint,
        interest_rate decimal(19,2),
        fixed_cost_amount numeric(38,2),
        fixed_cost_currency varchar(255),
        variable_cost_amount numeric(38,2),
        variable_cost_currency varchar(255),
        daily_capacity_per_production_line integer,
        labor_cost_amount numeric(38,2),
        labor_cost_currency varchar(255),
        production_lines integer,
        time_to_build integer,
        capacity integer,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        amount integer,
        unit_price_amount numeric(38,2),
        unit_price_currency varchar(255),
        intented_sales integer,
        price_amount numeric(38,2),
        price_currency varchar(255),
        intented_product_sale integer,
        market_entry_cost_amount numeric(38,2),
        market_entry_cost_currency varchar(255),
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        increase_productivity_amount numeric(38,2),
        increase_productivity_currency varchar(255),
        increase_quality_amount numeric(38,2),
        increase_quality_currency varchar(255),
        marketing_campaign_amount numeric(38,2),
        marketing_amount_currency varchar(255),
        company_id integer not null,
        market_id integer,
        market_simulation_id integer,
        primary key (id)
    );

    create table account (
       id integer not null,
        account_type smallint,
        name varchar(255),
        number varchar(255),
        start_balance numeric(38,2),
        accounting_id integer not null,
        primary key (id)
    );

    create table booking (
       id integer not null,
        amount numeric(38,2),
        credit_account_id integer not null,
        debit_account_id integer not null,
        journal_entry_id integer not null,
        primary key (id)
    );

    create table company (
       id integer not null,
        marketing_factor float(53) not null,
        name varchar(255),
        productivity_factor float(53) not null,
        quality_factor float(53) not null,
        accounting_id integer,
        simulation_id integer not null,
        primary key (id)
    );

    create table company_simulation_step (
       id integer not null,
        is_open boolean not null,
        company_id integer not null,
        simulation_step_id integer not null,
        primary key (id)
    );

    create table distribution_in_market (
       id integer not null,
        intented_product_sale integer,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        company_id integer not null,
        market_simulation_id integer not null,
        primary key (id)
    );

    create table distribution_step (
       id integer not null,
        intented_product_sale integer not null,
        market_potential_for_product integer not null,
        offered_price_amount numeric(38,2),
        offered_price_currency varchar(255),
        sold_products integer not null,
        company_simulation_step_id integer not null,
        distribution_in_market_id integer not null,
        primary key (id)
    );

    create table epoc_setting (
       id integer not null,
        description varchar(255),
        setting_format varchar(255),
        setting_key varchar(255),
        value_text varchar(255),
        settings_id integer not null,
        primary key (id)
    );

    create table epoc_settings (
       id integer not null,
        is_template boolean not null,
        primary key (id)
    );

    create table factory (
       id integer not null,
        daily_capacity_per_production_line integer not null,
        labour_cost_amount numeric(38,2),
        labour_cost_currency varchar(255),
        production_lines integer not null,
        production_start_month date,
        company_id integer not null,
        primary key (id)
    );

    create table financial_accounting (
       id integer not null,
        base_currency varchar(255),
        primary key (id)
    );

    create table journal_entry (
       id integer not null,
        booking_date date,
        booking_text varchar(255),
        value_date date,
        accounting_id integer not null,
        primary key (id)
    );

    create table login (
       id integer not null,
        email varchar(255),
        is_admin boolean not null,
        login varchar(255),
        name varchar(255),
        password varchar(255),
        simulation_id integer,
        primary key (id)
    );

    create table market (
       id integer not null,
        age65older_female integer not null,
        age65older_male integer not null,
        age_to14female integer not null,
        age_to14male integer not null,
        age_to24female integer not null,
        age_to24male integer not null,
        age_to54female integer not null,
        age_to54male integer not null,
        age_to64female integer not null,
        age_to64male integer not null,
        cost_to_enter_market_amount numeric(38,2),
        cost_to_enter_market_currency varchar(255),
        distribution_cost_amount numeric(38,2),
        distribution_cost_currency varchar(255),
        gdp_ppp_amount numeric(38,2),
        gdp_ppp_currency varchar(255),
        gdp_growth decimal(19,2),
        gdp_amount numeric(38,2),
        gdp_currency varchar(255),
        life_expectancy numeric(38,2),
        market_size integer not null,
        name varchar(255),
        unemployment decimal(19,2),
        primary key (id)
    );

    create table market_simulation (
       id integer not null,
        higher_percent decimal(19,2),
        higher_price_amount numeric(38,2),
        higher_price_currency varchar(255),
        lower_percent decimal(19,2),
        lower_price_amount numeric(38,2),
        lower_price_currency varchar(255),
        product_lifecycle_duration integer not null,
        start_month date,
        market_id integer not null,
        simulation_id integer not null,
        primary key (id)
    );

    create table message (
       id integer not null,
        level smallint,
        message varchar(255),
        relevant_month date,
        company_id integer not null,
        primary key (id)
    );

    create table simulation (
       id integer not null,
        maintenance_amount numeric(38,2),
        maintenance_currency varchar(255),
        depreciation_rate decimal(19,2),
        headquarter_amount numeric(38,2),
        headquarter_currency varchar(255),
        interest_rate decimal(19,2),
        is_finished boolean not null,
        is_started boolean not null,
        name varchar(255),
        nr_of_months integer,
        production_cost_amount numeric(38,2),
        production_cost_currency varchar(255),
        start_month date,
        owner_id integer not null,
        settings_id integer,
        primary key (id)
    );

    create table simulation_step (
       id integer not null,
        is_open boolean not null,
        simulation_month date,
        simulation_id integer not null,
        primary key (id)
    );

    create table storage (
       id integer not null,
        average_price_amount numeric(38,2),
        average_price_currency varchar(255),
        capacity integer not null,
        inventory_cost_amount numeric(38,2),
        inventory_cost_currency varchar(255),
        storage_start_month date,
        stored_products integer not null,
        stored_raw_materials integer not null,
        company_id integer not null,
        primary key (id)
    );

    create table user_in_company_role (
       id integer not null,
        is_invitation_required boolean not null,
        company_id integer not null,
        user_id integer not null,
        primary key (id)
    );

    alter table if exists login 
       add constraint UK_o7rt0909f6ygply7judc4s8v unique (login);

    alter table if exists abstract_simulation_order 
       add constraint FKggurd5x5rp6af1yeuctf1rd7g 
       foreign key (company_id) 
       references company;

    alter table if exists abstract_simulation_order 
       add constraint FKlfp48pla34w7exu2xefu9ogtl 
       foreign key (market_id) 
       references market;

    alter table if exists abstract_simulation_order 
       add constraint FKmvnofhgxo3oiwua1qlmwh0pfo 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists account 
       add constraint FKp2hmulxrcs3e5qmgqb8yn6klq 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists booking 
       add constraint FKcecfuabna76xy8f3rt9hb8uf0 
       foreign key (credit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FKpq88crk6f0gubu8790trxm3y4 
       foreign key (debit_account_id) 
       references account;

    alter table if exists booking 
       add constraint FK2eh7lv0s24gqo6j5j2jkgvgrt 
       foreign key (journal_entry_id) 
       references journal_entry;

    alter table if exists company 
       add constraint FK27v81voduq28sgrnestbxr84w 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists company 
       add constraint FK7vb9iipp9aqx0j6rc1ubq21td 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists company_simulation_step 
       add constraint FKfq6f7qjnmbgd24n7qee9gi6xu 
       foreign key (company_id) 
       references company;

    alter table if exists company_simulation_step 
       add constraint FK2x78ooj9inybagexs0qbhuss 
       foreign key (simulation_step_id) 
       references simulation_step;

    alter table if exists distribution_in_market 
       add constraint FKgydgxjshi24nawcfa2ghae80v 
       foreign key (company_id) 
       references company;

    alter table if exists distribution_in_market 
       add constraint FKk8oo82dvum7ypmati1mglq5c 
       foreign key (market_simulation_id) 
       references market_simulation;

    alter table if exists distribution_step 
       add constraint FKe30wjiv8f6s3q2jbnscscwfxn 
       foreign key (company_simulation_step_id) 
       references company_simulation_step;

    alter table if exists distribution_step 
       add constraint FKf0jlt7hq3561ockcbiypkkut4 
       foreign key (distribution_in_market_id) 
       references distribution_in_market;

    alter table if exists epoc_setting 
       add constraint FKlhemf3nm0c1gwlmxfelnp03x5 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists factory 
       add constraint FK6rwskkig4uio4h1rjamxmwgwp 
       foreign key (company_id) 
       references company;

    alter table if exists journal_entry 
       add constraint FK22njsv7d20mq0mq22udsmsrr6 
       foreign key (accounting_id) 
       references financial_accounting;

    alter table if exists login 
       add constraint FK5iynqetyhjc3es6401al2wgb6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists market_simulation 
       add constraint FKb4qqjqiyh6uctf0fie9jw9f8i 
       foreign key (market_id) 
       references market;

    alter table if exists market_simulation 
       add constraint FKrxxnjw46qusreljfkqre7b46d 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists message 
       add constraint FKcwsjctg2caabo17bol1qv7iff 
       foreign key (company_id) 
       references company;

    alter table if exists simulation 
       add constraint FKbbbbchb5yhiydrw8jfuahr1e0 
       foreign key (owner_id) 
       references login;

    alter table if exists simulation 
       add constraint FK9ttl2a2pdmrigxwd6obc1wia0 
       foreign key (settings_id) 
       references epoc_settings;

    alter table if exists simulation_step 
       add constraint FKm35j9aox3dfmj2fv6euq7y6f6 
       foreign key (simulation_id) 
       references simulation;

    alter table if exists storage 
       add constraint FKc8ru95f0db582pur7p8sock98 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FKqt8wygtgiei74p8jiwir5laue 
       foreign key (company_id) 
       references company;

    alter table if exists user_in_company_role 
       add constraint FK6dh864nau8nesq7dd9k8lotbt 
       foreign key (user_id) 
       references login;
