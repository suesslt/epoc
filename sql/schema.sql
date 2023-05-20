DROP TABLE IF EXISTS abstract_simulation_order CASCADE;

CREATE TABLE abstract_simulation_order (
	id integer NOT NULL,
	dtype varchar(31) NOT NULL,
	execution_month date NOT NULL,
	is_executed boolean NOT NULL,
	adjust_amount decimal(18, 6),
	adjust_currency char(3),
	direction smallint,
	interest_rate decimal(18, 9),
	fixed_cost_amount decimal(18, 6),
	fixed_cost_currency char(3),
	variable_cost_amount decimal(18, 6),
	variable_cost_currency char(3),
	daily_capacity_per_production_line integer,
	labor_cost_amount decimal(18, 6),
	labor_cost_currency char(3),
	production_lines integer,
	time_to_build integer,
	capacity integer,
	inventory_cost_amount decimal(18, 6),
	inventory_cost_currency char(3),
	amount integer,
	unit_price_amount decimal(18, 6),
	unit_price_currency char(3),
	intented_sales integer,
	price_amount decimal(18, 6),
	price_currency char(3),
	intented_product_sale integer,
	market_entry_cost_amount decimal(18, 6),
	market_entry_cost_currency char(3),
	offered_price_amount decimal(18, 6),
	offered_price_currency char(3),
	increase_productivity_amount decimal(18, 6),
	increase_productivity_currency char(3),
	increase_quality_amount decimal(18, 6),
	increase_quality_currency char(3),
	marketing_campaign_amount decimal(18, 6),
	marketing_amount_currency char(3),
	company_id integer NOT NULL,
	market_id integer, -- is necessary?
	market_simulation_id integer,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS account CASCADE;

CREATE TABLE account (
	id integer NOT NULL,
	account_type smallint,
	name varchar(255),
	number varchar(255),
	start_balance numeric(38, 2),
	accounting_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS booking CASCADE;

CREATE TABLE booking (
	id integer NOT NULL,
	amount numeric(38, 2),
	credit_account_id integer NOT NULL,
	debit_account_id integer NOT NULL,
	journal_entry_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS company CASCADE;

CREATE TABLE company (
	id integer NOT NULL,
	marketing_factor float (53) NOT NULL,
	name varchar(255),
	productivity_factor float (53) NOT NULL,
	quality_factor float (53) NOT NULL,
	accounting_id integer,
	simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS company_simulation_step CASCADE;

CREATE TABLE company_simulation_step (
	id integer NOT NULL,
	is_open boolean NOT NULL,
	company_id integer NOT NULL,
	simulation_step_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS distribution_in_market CASCADE;

CREATE TABLE distribution_in_market (
	id integer NOT NULL,
	intented_product_sale integer,
	offered_price_amount decimal(18, 6),
	offered_price_currency char(3),
	company_id integer NOT NULL,
	market_simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS distribution_step CASCADE;

CREATE TABLE distribution_step (
	id integer NOT NULL,
	intented_product_sale integer NOT NULL,
	market_potential_for_product integer NOT NULL,
	offered_price_amount decimal(18, 6),
	offered_price_currency char(3),
	sold_products integer NOT NULL,
	company_simulation_step_id integer NOT NULL,
	distribution_in_market_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS epoc_setting CASCADE;

CREATE TABLE epoc_setting (
	id integer NOT NULL,
	description varchar(255),
	setting_format varchar(255),
	setting_key varchar(255),
	value_text varchar(255),
	settings_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS epoc_settings CASCADE;

CREATE TABLE epoc_settings (
	id integer NOT NULL,
	is_template boolean NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS factory CASCADE;

CREATE TABLE factory (
	id integer NOT NULL,
	daily_capacity_per_production_line integer NOT NULL,
	labour_cost_amount decimal(18, 6),
	labour_cost_currency char(3),
	production_lines integer NOT NULL,
	production_start_month date,
	company_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS financial_accounting CASCADE;

CREATE TABLE financial_accounting (
	id integer NOT NULL,
	base_currency char(3),
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS journal_entry CASCADE;

CREATE TABLE journal_entry (
	id integer NOT NULL,
	booking_date date,
	booking_text varchar(255),
	value_date date,
	accounting_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS login CASCADE;

CREATE TABLE login (
	id integer NOT NULL,
	email varchar(255),
	is_admin boolean NOT NULL,
	login varchar(255) UNIQUE,
	name varchar(255),
	password varchar(255),
	simulation_id integer,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS market CASCADE;

CREATE TABLE market (
	id serial NOT NULL,
	age65older_female integer NOT NULL,
	age65older_male integer NOT NULL,
	age_to14female integer NOT NULL,
	age_to14male integer NOT NULL,
	age_to24female integer NOT NULL,
	age_to24male integer NOT NULL,
	age_to54female integer NOT NULL,
	age_to54male integer NOT NULL,
	age_to64female integer NOT NULL,
	age_to64male integer NOT NULL,
	cost_to_enter_market_amount decimal(18, 6),
	cost_to_enter_market_currency char(3),
	distribution_cost_amount decimal(18, 6),
	distribution_cost_currency varchar(3),
	gdp_ppp_amount decimal(18, 6),
	gdp_ppp_currency varchar(3),
	gdp_growth decimal(19, 2),
	gdp_amount decimal(18, 6),
	gdp_currency varchar(3),
	life_expectancy numeric(38, 2),
	market_size integer NOT NULL,
	name varchar(255),
	unemployment decimal(19, 2),
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS market_simulation CASCADE;

CREATE TABLE market_simulation (
	id integer NOT NULL,
	higher_percent decimal(19, 2),
	higher_price_amount decimal(18, 6),
	higher_price_currency char(3),
	lower_percent decimal(19, 2),
	lower_price_amount decimal(18, 6),
	lower_price_currency char(3),
	product_lifecycle_duration integer NOT NULL,
	start_month date,
	market_id integer NOT NULL,
	simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS message CASCADE;

CREATE TABLE message (
	id integer NOT NULL,
	level smallint,
	message varchar(255),
	relevant_month date,
	company_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS simulation CASCADE;

CREATE TABLE simulation (
	id integer NOT NULL,
	maintenance_amount decimal(18, 6),
	maintenance_currency char(3),
	depreciation_rate decimal(18, 9),
	headquarter_amount decimal(18, 6),
	headquarter_currency char(3),
	interest_rate decimal(18, 9),
	is_finished boolean NOT NULL,
	is_started boolean NOT NULL,
	name varchar(255),
	nr_of_months integer,
	production_cost_amount decimal(18, 6),
	production_cost_currency char(3),
	start_month date,
	owner_id integer NOT NULL,
	settings_id integer,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS simulation_step CASCADE;

CREATE TABLE simulation_step (
	id integer NOT NULL,
	is_open boolean NOT NULL,
	simulation_month date,
	simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS storage CASCADE;

CREATE TABLE storage (
	id integer NOT NULL,
	average_price_amount decimal(18, 6),
	average_price_currency char(3),
	capacity integer NOT NULL,
	inventory_cost_amount decimal(18, 6),
	inventory_cost_currency char(3),
	storage_start_month date,
	stored_products integer NOT NULL,
	stored_raw_materials integer NOT NULL,
	company_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP TABLE IF EXISTS user_in_company_role CASCADE;

CREATE TABLE user_in_company_role (
	id integer NOT NULL,
	is_invitation_required boolean NOT NULL,
	company_id integer NOT NULL,
	user_id integer NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS LOGIN
	ADD CONSTRAINT UK_o7rt0909f6ygply7judc4s8v UNIQUE (LOGIN);

ALTER TABLE abstract_simulation_order
	ADD CONSTRAINT FK_abstractsimulationorder_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE abstract_simulation_order
	ADD CONSTRAINT FK_abstractsimulationorder_market FOREIGN KEY (market_id) REFERENCES market ON DELETE CASCADE;

ALTER TABLE abstract_simulation_order
	ADD CONSTRAINT FK_abstractsimulationorder_marketsimulation FOREIGN KEY (market_simulation_id) REFERENCES market_simulation ON DELETE CASCADE;

ALTER TABLE account
	ADD CONSTRAINT FK_account_financialaccounting FOREIGN KEY (accounting_id) REFERENCES financial_accounting ON DELETE CASCADE;

ALTER TABLE booking
	ADD CONSTRAINT FK_booking_account_credit FOREIGN KEY (credit_account_id) REFERENCES account ON DELETE CASCADE;

ALTER TABLE booking
	ADD CONSTRAINT FK_booking_account_debit FOREIGN KEY (debit_account_id) REFERENCES account ON DELETE CASCADE;

ALTER TABLE booking
	ADD CONSTRAINT FK_booking_journalentry FOREIGN KEY (journal_entry_id) REFERENCES journal_entry ON DELETE CASCADE;

ALTER TABLE company
	ADD CONSTRAINT FK_company_financialaccounting FOREIGN KEY (accounting_id) REFERENCES financial_accounting ON DELETE CASCADE;

ALTER TABLE company
	ADD CONSTRAINT FK_company_simulation FOREIGN KEY (simulation_id) REFERENCES simulation ON DELETE CASCADE;

ALTER TABLE company_simulation_step
	ADD CONSTRAINT FK_companysimulationstep_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE company_simulation_step
	ADD CONSTRAINT FK_companysimulationstep_simulationstep FOREIGN KEY (simulation_step_id) REFERENCES simulation_step ON DELETE CASCADE;

ALTER TABLE distribution_in_market
	ADD CONSTRAINT FK_distributioninmarket_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE distribution_in_market
	ADD CONSTRAINT FK_distributioninmarket_marketsimulation FOREIGN KEY (market_simulation_id) REFERENCES market_simulation ON DELETE CASCADE;

ALTER TABLE distribution_step
	ADD CONSTRAINT FK_distributionstep_companysimulationstep FOREIGN KEY (company_simulation_step_id) REFERENCES company_simulation_step ON DELETE CASCADE;

ALTER TABLE distribution_step
	ADD CONSTRAINT FK_distributionstep_distributioninmarket FOREIGN KEY (distribution_in_market_id) REFERENCES distribution_in_market ON DELETE CASCADE;

ALTER TABLE epoc_setting
	ADD CONSTRAINT FK_epocsetting_epocsettings FOREIGN KEY (settings_id) REFERENCES epoc_settings ON DELETE CASCADE;

ALTER TABLE factory
	ADD CONSTRAINT FK_factory_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE journal_entry
	ADD CONSTRAINT FK_journalentry_financialaccounting FOREIGN KEY (accounting_id) REFERENCES financial_accounting ON DELETE CASCADE;

ALTER TABLE LOGIN
	ADD CONSTRAINT FK_login_simulation FOREIGN KEY (simulation_id) REFERENCES simulation ON DELETE CASCADE;

ALTER TABLE market_simulation
	ADD CONSTRAINT FK_marketsimulation_market FOREIGN KEY (market_id) REFERENCES market ON DELETE CASCADE;

ALTER TABLE market_simulation
	ADD CONSTRAINT FK_marketsimulation_simulation FOREIGN KEY (simulation_id) REFERENCES simulation ON DELETE CASCADE;

ALTER TABLE message
	ADD CONSTRAINT FK_message_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE simulation
	ADD CONSTRAINT FK_simulation_login FOREIGN KEY (owner_id) REFERENCES LOGIN ON DELETE CASCADE;

ALTER TABLE simulation
	ADD CONSTRAINT FK_simulation_epocsettings FOREIGN KEY (settings_id) REFERENCES epoc_settings ON DELETE CASCADE;

ALTER TABLE simulation_step
	ADD CONSTRAINT FK_simulationstep_simulation FOREIGN KEY (simulation_id) REFERENCES simulation ON DELETE CASCADE;

ALTER TABLE storage
	ADD CONSTRAINT FK_storage_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE user_in_company_role
	ADD CONSTRAINT FK_userincompanyrole_company FOREIGN KEY (company_id) REFERENCES company ON DELETE CASCADE;

ALTER TABLE user_in_company_role
	ADD CONSTRAINT FK_userincompanyrole_login FOREIGN KEY (user_id) REFERENCES LOGIN ON DELETE CASCADE;
