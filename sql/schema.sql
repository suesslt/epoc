drop sequence if exists abstract_simulation_order_seq;
drop sequence if exists account_seq;
drop sequence if exists booking_seq;
drop sequence if exists company_seq;
drop sequence if exists company_simulation_step_seq;
drop sequence if exists distribution_in_market_seq;
drop sequence if exists distribution_step_seq;
drop sequence if exists epoc_setting_seq;
drop sequence if exists epoc_settings_seq;
drop sequence if exists factory_seq;
drop sequence if exists financial_accounting_seq;
drop sequence if exists journal_entry_seq;
drop sequence if exists login_seq;
drop sequence if exists market_seq;
drop sequence if exists market_simulation_seq;
drop sequence if exists message_seq;
drop sequence if exists simulation_seq;
drop sequence if exists simulation_step_seq;
drop sequence if exists storage_seq;
drop sequence if exists user_in_company_role_seq;

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

drop table if exists abstract_simulation_order CASCADE;
drop table if exists account CASCADE;
drop table if exists booking CASCADE;
drop table if exists company CASCADE;
drop table if exists company_simulation_step CASCADE;
drop table if exists distribution_in_market CASCADE;
drop table if exists distribution_step CASCADE;
drop table if exists epoc_setting CASCADE;
drop table if exists epoc_settings CASCADE;
drop table if exists factory CASCADE;
drop table if exists financial_accounting CASCADE;
drop table if exists journal_entry CASCADE;
drop table if exists login CASCADE;
drop table if exists market CASCADE;
drop table if exists market_simulation CASCADE;
drop table if exists message CASCADE;
drop table if exists simulation CASCADE;
drop table if exists simulation_step CASCADE;
drop table if exists storage CASCADE;
drop table if exists user_in_company_role CASCADE;

create table abstract_simulation_order (
    id integer not null,
    dtype varchar(31) not null,
    execution_month date not null,
    is_executed boolean not null,
    adjust_amount decimal(18,6),
    adjust_currency varchar(3),
    direction smallint,
    interest_rate decimal(18,9),
    fixed_cost_amount decimal(18,6),
    fixed_cost_currency varchar(3),
    variable_cost_amount decimal(18,6),
    variable_cost_currency varchar(3),
    daily_capacity_per_production_line integer,
    labor_cost_amount decimal(18,6),
    labor_cost_currency varchar(3),
    production_lines integer,
    time_to_build integer,
    capacity integer,
    inventory_cost_amount decimal(18,6),
    inventory_cost_currency varchar(3),
    amount integer,
    unit_price_amount decimal(18,6),
    unit_price_currency varchar(3),
    intented_sales integer,
    price_amount decimal(18,6),
    price_currency varchar(3),
    intented_product_sale integer,
    market_entry_cost_amount decimal(18,6),
    market_entry_cost_currency varchar(3),
    offered_price_amount decimal(18,6),
    offered_price_currency varchar(3),
    increase_productivity_amount decimal(18,6),
    increase_productivity_currency varchar(3),
    increase_quality_amount decimal(18,6),
    increase_quality_currency varchar(3),
    marketing_campaign_amount decimal(18,6),
    marketing_amount_currency varchar(3),
    company_id integer not null,
    market_id integer, -- is necessary?
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
    offered_price_amount decimal(18,6),
    offered_price_currency varchar(3),
    company_id integer not null,
    market_simulation_id integer not null,
    primary key (id)
);

create table distribution_step (
   id integer not null,
    intented_product_sale integer not null,
    market_potential_for_product integer not null,
    offered_price_amount decimal(18,6),
    offered_price_currency varchar(3),
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
    labour_cost_amount decimal(18,6),
    labour_cost_currency varchar(3),
    production_lines integer not null,
    production_start_month date,
    company_id integer not null,
    primary key (id)
);

create table financial_accounting (
   id integer not null,
    base_currency varchar(3),
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
    login varchar(255) unique,
    name varchar(255),
    password varchar(255) not null,
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
    cost_to_enter_market_amount decimal(18,6),
    cost_to_enter_market_currency varchar(3),
    distribution_cost_amount decimal(18,6),
    distribution_cost__currency varchar(3),
    gdp_ppp_amount decimal(18,6),
    gdp_ppp_currency varchar(3),
    gdp_growth decimal(19,2),
    gdp_amount decimal(18,6),
    gdp_currency varchar(3),
    life_expectancy numeric(38,2),
    market_size integer not null,
    name varchar(255),
    unemployment decimal(19,2),
    primary key (id)
);

create table market_simulation (
   id integer not null,
    higher_percent decimal(19,2),
    higher_price_amount decimal(18,6),
    higher_price_currency varchar(3),
    lower_percent decimal(19,2),
    lower_price_amount decimal(18,6),
    lower_price_currency varchar(3),
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
    maintenance_amount decimal(18,6),
    maintenance_currency varchar(3),
    depreciation_rate decimal(18,9),
    headquarter_amount decimal(18,6),
    headquarter_currency varchar(3),
    interest_rate decimal(18,9),
    is_finished boolean not null,
    is_started boolean not null,
    name varchar(255),
    nr_of_months integer,
    production_cost_amount decimal(18,6),
    production_cost_currency varchar(3),
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
    average_price_amount decimal(18,6),
    average_price_currency varchar(3),
    capacity integer not null,
    inventory_cost_amount decimal(18,6),
    inventory_cost_currency varchar(3),
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

alter table abstract_simulation_order 
   add constraint FK_abstractsimulationorder_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table abstract_simulation_order 
   add constraint FK_abstractsimulationorder_market
   foreign key (market_id) 
   references market
   on delete cascade;

alter table abstract_simulation_order 
   add constraint FK_abstractsimulationorder_marketsimulation
   foreign key (market_simulation_id) 
   references market_simulation
   on delete cascade;

alter table account 
   add constraint FK_account_financialaccounting
   foreign key (accounting_id) 
   references financial_accounting
   on delete cascade;

alter table booking 
   add constraint FK_booking_account_credit
   foreign key (credit_account_id) 
   references account
   on delete cascade;

alter table booking 
   add constraint FK_booking_account_debit
   foreign key (debit_account_id) 
   references account
   on delete cascade;

alter table booking 
   add constraint FK_booking_journalentry
   foreign key (journal_entry_id) 
   references journal_entry
   on delete cascade;

alter table company 
   add constraint FK_company_financialaccounting
   foreign key (accounting_id) 
   references financial_accounting
   on delete cascade;

alter table company 
   add constraint FK_company_simulation
   foreign key (simulation_id) 
   references simulation
   on delete cascade;

alter table company_simulation_step 
   add constraint FK_companysimulationstep_company
   foreign key (company_id)
   references company
   on delete cascade;

alter table company_simulation_step 
   add constraint FK_companysimulationstep_simulationstep
   foreign key (simulation_step_id)
   references simulation_step
   on delete cascade;

alter table distribution_in_market 
   add constraint FK_distributioninmarket_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table distribution_in_market 
   add constraint FK_distributioninmarket_marketsimulation
   foreign key (market_simulation_id) 
   references market_simulation
   on delete cascade;

alter table distribution_step 
   add constraint FK_distributionstep_companysimulationstep
   foreign key (company_simulation_step_id) 
   references company_simulation_step
   on delete cascade;

alter table distribution_step 
   add constraint FK_distributionstep_distributioninmarket
   foreign key (distribution_in_market_id) 
   references distribution_in_market
   on delete cascade;

alter table epoc_setting 
   add constraint FK_epocsetting_epocsettings
   foreign key (settings_id) 
   references epoc_settings
   on delete cascade;

alter table factory 
   add constraint FK_factory_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table journal_entry 
   add constraint FK_journalentry_financialaccounting
   foreign key (accounting_id) 
   references financial_accounting
   on delete cascade;

alter table login 
   add constraint FK_login_simulation
   foreign key (simulation_id) 
   references simulation
   on delete cascade;

alter table market_simulation 
   add constraint FK_marketsimulation_market
   foreign key (market_id) 
   references market
   on delete cascade;

alter table market_simulation 
   add constraint FK_marketsimulation_simulation
   foreign key (simulation_id) 
   references simulation
   on delete cascade;

alter table message 
   add constraint FK_message_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table simulation 
   add constraint FK_simulation_login
   foreign key (owner_id) 
   references login
   on delete cascade;

alter table simulation 
   add constraint FK_simulation_epocsettings
   foreign key (settings_id) 
   references epoc_settings
   on delete cascade;

alter table simulation_step 
   add constraint FK_simulationstep_simulation
   foreign key (simulation_id) 
   references simulation
   on delete cascade;

alter table storage 
   add constraint FK_storage_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table user_in_company_role 
   add constraint FK_userincompanyrole_company
   foreign key (company_id) 
   references company
   on delete cascade;

alter table user_in_company_role 
   add constraint FK_userincompanyrole_login
   foreign key (user_id) 
   references login
   on delete cascade;