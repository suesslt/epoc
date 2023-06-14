DROP SEQUENCE IF EXISTS abstract_simulation_order_seq;

CREATE SEQUENCE abstract_simulation_order_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS abstract_simulation_order CASCADE;

CREATE TABLE abstract_simulation_order (
	id bigint NOT NULL,
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
	market_id integer,
	market_simulation_id integer,
	PRIMARY KEY (id)
);

CREATE UNIQUE INDEX ix_abstract_simulation_order_type_month_company ON abstract_simulation_order (dtype, execution_month, company_id);

DROP SEQUENCE IF EXISTS account_seq;

CREATE SEQUENCE account_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS account CASCADE;

CREATE TABLE account (
	id bigint NOT NULL,
	account_type smallint,
	name varchar(255),
	number varchar(255),
	start_balance numeric(38, 2),
	accounting_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS booking_seq;

CREATE SEQUENCE booking_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS booking CASCADE;

CREATE TABLE booking (
	id bigint NOT NULL,
	amount numeric(38, 2),
	credit_account_id integer NOT NULL,
	debit_account_id integer NOT NULL,
	journal_entry_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS company_seq;

CREATE SEQUENCE company_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS company CASCADE;

CREATE TABLE company (
	id bigint NOT NULL,
	marketing_factor float (53) NOT NULL,
	name varchar(255),
	productivity_factor float (53) NOT NULL,
	quality_factor float (53) NOT NULL,
	accounting_id integer,
	simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS company_simulation_step_seq;

CREATE SEQUENCE company_simulation_step_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS company_simulation_step CASCADE;

CREATE TABLE company_simulation_step (
	id bigint NOT NULL,
	is_open boolean NOT NULL,
	company_id integer NOT NULL,
	simulation_step_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS distribution_in_market_seq;

CREATE SEQUENCE distribution_in_market_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS distribution_in_market CASCADE;

CREATE TABLE distribution_in_market (
	id bigint NOT NULL,
	intented_product_sale integer,
	offered_price_amount decimal(18, 6),
	offered_price_currency char(3),
	company_id integer NOT NULL,
	market_simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS distribution_step_seq;

CREATE SEQUENCE distribution_step_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS distribution_step CASCADE;

CREATE TABLE distribution_step (
	id bigint NOT NULL,
	intented_product_sale integer NOT NULL,
	market_potential_for_product integer NOT NULL,
	offered_price_amount decimal(18, 6),
	offered_price_currency char(3),
	sold_products integer NOT NULL,
	company_simulation_step_id integer NOT NULL,
	distribution_in_market_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS epoc_setting_seq;

CREATE SEQUENCE epoc_setting_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS epoc_setting CASCADE;

CREATE TABLE epoc_setting (
	id bigint NOT NULL,
	description varchar(255),
	setting_format varchar(255),
	setting_key varchar(255),
	value_text varchar(255),
	settings_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS epoc_settings_seq;

CREATE SEQUENCE epoc_settings_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS epoc_settings CASCADE;

CREATE TABLE epoc_settings (
	id bigint NOT NULL,
	is_template boolean NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS factory_seq;

CREATE SEQUENCE factory_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS factory CASCADE;

CREATE TABLE factory (
	id bigint NOT NULL,
	daily_capacity_per_production_line integer NOT NULL,
	labour_cost_amount decimal(18, 6),
	labour_cost_currency char(3),
	production_lines integer NOT NULL,
	production_start_month date,
	company_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS financial_accounting_seq;

CREATE SEQUENCE financial_accounting_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS financial_accounting CASCADE;

CREATE TABLE financial_accounting (
	id bigint NOT NULL,
	base_currency char(3),
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS journal_entry_seq;

CREATE SEQUENCE journal_entry_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS journal_entry CASCADE;

CREATE TABLE journal_entry (
	id bigint NOT NULL,
	booking_date date,
	booking_text varchar(255),
	value_date date,
	accounting_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS LOGIN_seq;

CREATE SEQUENCE LOGIN_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS LOGIN CASCADE;

CREATE TABLE LOGIN (
	id bigint NOT NULL,
	email varchar(255),
	phone varchar(255),
	is_admin boolean NOT NULL,
	username varchar(255) UNIQUE,
	first_name varchar(255),
	last_name varchar(255),
	password varchar(255),
	simulation_id integer,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS market_seq;

CREATE SEQUENCE market_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS market CASCADE;

CREATE TABLE market (
	id bigint NOT NULL,
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
	labor_force integer NOT NULL,
	name varchar(255),
	unemployment decimal(19, 2),
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS market_simulation_seq;

CREATE SEQUENCE market_simulation_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS market_simulation CASCADE;

CREATE TABLE market_simulation (
	id bigint NOT NULL,
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

DROP SEQUENCE IF EXISTS message_seq;

CREATE SEQUENCE message_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS message CASCADE;

CREATE TABLE message (
	id bigint NOT NULL,
	level smallint,
	message varchar(255),
	relevant_month date,
	company_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS simulation_seq;

CREATE SEQUENCE simulation_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS simulation CASCADE;

CREATE TABLE simulation (
	id bigint NOT NULL,
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

DROP SEQUENCE IF EXISTS simulation_step_seq;

CREATE SEQUENCE simulation_step_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS simulation_step CASCADE;

CREATE TABLE simulation_step (
	id bigint NOT NULL,
	is_open boolean NOT NULL,
	simulation_month date,
	simulation_id integer NOT NULL,
	PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS storage_seq;

CREATE SEQUENCE storage_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS storage CASCADE;

CREATE TABLE storage (
	id bigint NOT NULL,
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

DROP SEQUENCE IF EXISTS user_in_company_role_seq;

CREATE SEQUENCE user_in_company_role_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS user_in_company_role CASCADE;

CREATE TABLE user_in_company_role (
	id bigint NOT NULL,
	is_invitation_required boolean NOT NULL,
	company_id integer NOT NULL,
	user_id bigint NOT NULL,
	PRIMARY KEY (id)
);

CREATE UNIQUE INDEX ix_user_in_company_role_companyid_userId ON user_in_company_role (company_id, user_id);

DROP SEQUENCE IF EXISTS user_token_seq;

CREATE SEQUENCE user_token_seq
	START 1
	INCREMENT 50;

DROP TABLE IF EXISTS user_token CASCADE;

CREATE TABLE user_token (
	id bigint NOT NULL,
	token varchar(120),
	user_id bigint NOT NULL,
	expiry_date timestamp,
	PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS LOGIN
	ADD CONSTRAINT UK_login_username UNIQUE (username);

ALTER TABLE IF EXISTS LOGIN
	ADD CONSTRAINT UK_login_email UNIQUE (email);

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

ALTER TABLE user_token
	ADD CONSTRAINT FK_usertoken_login FOREIGN KEY (user_id) REFERENCES LOGIN ON DELETE CASCADE;

INSERT INTO login (id, username, first_name, last_name, email, is_admin, PASSWORD)
	VALUES (1, 'user', 'John', 'Normal', 'john.normal@bluesky.ch', FALSE, '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe');

INSERT INTO login (id, username, first_name, last_name, email, is_admin, PASSWORD)
	VALUES (2, 'admin', 'Emma', 'Powerful', 'emma.powerful@bluesky.ch', TRUE, '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.');

INSERT INTO epoc_settings (id, is_template)
	VALUES (1, TRUE);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (1, 'SET0001', 'Money', 'CHF 1000000', 'Fixed cost to construct factory', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (2, 'SET0002', 'Money', 'CHF 100000', 'Cost per production line', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (3, 'SET0003', 'Money', 'CHF 1000000', 'Fixed cost to construct storage building', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (4, 'SET0004', 'Money', 'CHF 1000', 'Variable cost per storage slot', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (5, 'SET0005', 'Integer', '4', 'Daily capacity per production line', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (6, 'SET0006', 'Integer', '12', 'Months required to construct a factory', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (7, 'SET0007', 'Integer', '12', 'Months required to construct a storage building', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (8, 'SET0008', 'Integer', '12', 'Length of password in characters', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (9, 'SET0009', 'YearMonth', '2000-01', 'Default simulation start month', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (10, 'SET0010', 'String', 'VIRGIN', 'Simulation Type', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (11, 'SET0011', 'Money', 'CHF 500000', 'Labor cost per production line and year', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (12, 'SET0012', 'Money', 'CHF 10000', 'Maintenance cost per year and building', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (13, 'SET0013', 'Percent', '5%', 'Debt interest rate', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (14, 'SET0014', 'Money', 'CHF 35', 'Raw material purchase price per unit', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (15, 'SET0015', 'Percent', '20%', 'Demand higher percent', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (16, 'SET0016', 'Money', 'CHF 80', 'Demand higher price', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (17, 'SET0017', 'Percent', '80%', 'Demand lower percent', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (18, 'SET0018', 'Money', 'CHF 20', 'Demand lower price', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (19, 'SET0019', 'Integer', '100', 'Product lifecycle duration', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (20, 'SET0020', 'Money', 'CHF 500000', 'Market entry cost', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (21, 'SET0022', 'Currency', 'CHF', 'Base Currency', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (22, 'SET0023', 'Percent', '15%', 'Depreciation rate', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (23, 'SET0024', 'Money', 'CHF 500000', 'Inventory management staff cost per 1000 Units', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (24, 'SET0025', 'Money', 'CHF 1000000', 'Headquarter staff cost per annum', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (25, 'SET0026', 'Money', 'CHF 30', 'Manufacturing cost per unit (used for accounting)', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (26, 'SET0027', 'Integer', '1', 'Number of months per step', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (27, 'SET0028', 'Money', 'CHF 200000', 'Price per percent point quality increase', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (28, 'SET0029', 'Money', 'CHF 500000', 'Price per marketing campaign', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (29, 'SET0030', 'Money', 'CHF 100000', 'Price per productivity point', 1);

INSERT INTO epoc_setting (id, setting_key, setting_format, value_text, description, settings_id)
	VALUES (30, 'SET0031', 'Percent', '10%', 'Factor discount rate', 1);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (1, 'EuropeanUnion', 'USD', 20850000000, 'USD', 17110000000, 0.023, 238900000, 0.0860, 80.7, 'CHF', 1000000, 'CHF', 5000000, 40905648, 38860151, 28085190, 26851677, 107404085, 105480809, 33083278, 34885100, 43673572, 57881819);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (2, 'China', 'USD', 23210000000, 'USD', 12010000000, 0.069, 806700000, 0.04, 75.8, 'CHF', 10000000, 'CHF', 50000000, 128270371, 110120535, 91443139, 79181726, 338189015, 324180103, 79340391, 77857806, 74277631, 81828269);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (3, 'Switzerland', 'USD', 523100000, 'USD', 679000000, 0.017, 5159000, 0.032, 82.7, 'CHF', 100000, 'CHF', 500000, 650151, 612479, 453003, 433101, 1781425, 1774124, 535457, 532454, 672024, 848591);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (4, 'United States', 'USD', 19490000000, 'USD', 19490000000, 0.022, 160400000, 0.044, 80.1, 'CHF', 1000000, 'CHF', 20000000, 31329121, 29984705, 22119340, 21082599, 64858646, 64496889, 20578432, 22040267, 23489515, 29276951);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (5, 'Japan', 'USD', 5443000000, 'USD', 4873000000, 0.017, 65010000, 0.029, 85.5, 'CHF', 1000000, 'CHF', 8000000, 8251336, 7787234, 6397995, 5746140, 23246562, 23784273, 7588597, 7563245, 15655860, 20146914);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (6, 'Nigeria', 'USD', 1121000000, 'USD', 376400000, 0.008, 60080000, 0.165, 59.3, 'CHF', 1000, 'CHF', 100000, 44087799, 42278742, 20452045, 19861371, 31031253, 30893168, 4017658, 4197739, 3138206, 3494524);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (7, 'India', 'USD', 9474000000, 'USD', 2602000000, 0.067, 521900000, 0.085, 69.1, 'CHF', 5000000, 'CHF', 40000000, 185736879, 164194080, 122573662, 108109968, 276283581, 258563835, 49334703, 49197817, 39184523, 43654994);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (8, 'Germany', 'USD', 4199000000, 'USD', 3701000000, 0.025, 45900000, 0.038, 80.9, 'CHF', 1000000, 'CHF', 20000000, 5299798, 5024184, 4092901, 3933997, 16181931, 15896528, 5989111, 6047449, 7930590, 10061248);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (9, 'France', 'USD', 2856000000, 'USD', 2588000000, 0.023, 30680000, 0.094, 82.0, 'CHF', 1000000, 'CHF', 15000000, 6366789, 6082729, 4065780, 3884488, 12731825, 12515501, 4035073, 4331751, 5781410, 7569011);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (10, 'Austria', 'USD', 441000000, 'USD', 417400000, 0.03, 4260000, 0.055, 81.7, 'CHF', 50000, 'CHF', 2000000, 630739, 600663, 484515, 467064, 1851209, 1851100, 595146, 603249, 743174, 966511);

INSERT INTO market (id, name, gdp_ppp_currency, gdp_ppp_amount, gdp_currency, gdp_amount, gdp_growth, labor_force, unemployment, life_expectancy, cost_to_enter_market_currency, cost_to_enter_market_amount, distribution_cost_currency, distribution_cost_amount, age_to14Male, age_to14Female, age_to24Male, age_to24Female, age_to54Male, age_to54Female, age_to64Male, age_to64Female, age65older_male, age65older_female)
	VALUES (11, 'United Kingdom', 'USD', 2925000000, 'USD', 2628000000, 0.017, 33500000, 0.044, 80.9, 'CHF', 1000000, 'CHF', 10000000, 5871268, 5582107, 3895850, 3726311, 13387119, 12843549, 3936466, 4022245, 5321392, 6518939);
