INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname) VALUES (nextval('SEQ_EG_MODULE'), 'Event Notification', true, 'eventnotification', NULL, 'Event Notification');

INSERT INTO eg_module (id, name, enabled, contextroot, parentmodule, displayname) 
VALUES (nextval('SEQ_EG_MODULE'), 'Notifications', true, 'eventnotification', (select id from eg_module where name = 'Event Notification'), 'Notifications');

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Create Event','/event/create/',(select id from eg_module where name='Event Notification' ),1,
'Create Event',false,'eventnotification',(select id from eg_module where name='Event Notification'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Event','/event/view/',(select id from eg_module where name='Event Notification' ),1,
'Event',true,'eventnotification',(select id from eg_module where name='Event Notification'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Modify Event','/event/update/',(select id from eg_module where name='Event Notification' ),1,
'Modify Event',false,'eventnotification',(select id from eg_module where name='Event Notification'));

Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Create Event'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Event'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Modify Event'));


Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Drafts','/drafts/create/',(select id from eg_module where name='Notifications' ),1,
'Draft',false,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'View Drafts','/drafts/view/',(select id from eg_module where name='Notifications' ),1,
'Draft',true,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Update Drafts','/drafts/update/',(select id from eg_module where name='Notifications' ),1,
'Draft',false,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Update Drafts'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Drafts'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='View Drafts'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Create Schedule','/schedule/create/',(select id from eg_module where name='Notifications' ),1,
'Create Schedule',false,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Schedule','/schedule/view/',(select id from eg_module where name='Notifications' ),1,
'Schedule',true,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Delete Schedule','/schedule/delete/',(select id from eg_module where name='Notifications' ),1,
'Delete Schedule',false,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_action(id,name,url,parentmodule,ordernumber,displayname,enabled,contextroot,application) 
values(nextval('SEQ_EG_ACTION'),'Update Schedule','/schedule/update/',(select id from eg_module where name='Notifications' ),1,
'Update Schedule',false,'eventnotification',(select id from eg_module where name='Notifications'));

Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Update Schedule'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Schedule'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Create Schedule'));
Insert into eg_roleaction values((select id from eg_role where name='SYSTEM'),(select id from eg_action where name='Delete Schedule'));

DROP TABLE IF EXISTS egevntnotification_module;
DROP SEQUENCE IF EXISTS seq_egevntnotification_module;

DROP TABLE IF EXISTS egevntnotification_category;
DROP SEQUENCE IF EXISTS seq_egevntnotification_category;

DROP TABLE IF EXISTS egevntnotification_parameters;
DROP SEQUENCE IF EXISTS seq_egevntnotification_parameters;

DROP TABLE IF EXISTS egevntnotification_event;
DROP SEQUENCE IF EXISTS seq_egevntnotification_event;

DROP TABLE IF EXISTS egevntnotification_eventtype;
DROP SEQUENCE IF EXISTS seq_egevntnotification_eventtype;

CREATE SEQUENCE seq_egevntnotification_eventtype; 
CREATE TABLE egevntnotification_eventtype
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_eventtype_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE seq_egevntnotification_event;
CREATE TABLE egevntnotification_event
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  description character varying(200) NOT NULL,
  eventhost character varying(100) NOT NULL,
  eventlocation character varying(100) NOT NULL,
  address character varying(200) NOT NULL,
  contactnumber character varying(50) NOT NULL,
  eventtype bigint NOT NULL,
  paid boolean NOT NULL,
  cost double precision,
  filestore bigint,
  start_date timestamp with time zone NOT NULL,
  end_date timestamp with time zone NOT NULL,
  message character varying(200),
  url character varying(200),
  status character varying(20),
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_event_pkey PRIMARY KEY (id),
  CONSTRAINT egevntnotification_event_fkey FOREIGN KEY (eventtype)
      REFERENCES egevntnotification_eventtype (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

DROP TABLE IF EXISTS egevntnotification_userevent;
DROP SEQUENCE IF EXISTS seq_egevntnotification_userevent;

CREATE SEQUENCE seq_egevntnotification_userevent;
CREATE TABLE egevntnotification_userevent
(
  id bigint NOT NULL,
  userid bigint NOT NULL,
  event bigint NOT NULL,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version bigint,
  CONSTRAINT egevntnotification_userevent_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS egevntnotification_userfcmdevice;
DROP SEQUENCE IF EXISTS seq_egevntnotification_userfcmdevice;

CREATE SEQUENCE seq_egevntnotification_userfcmdevice;
CREATE TABLE egevntnotification_userfcmdevice
(
  id bigint NOT NULL,
  userid bigint NOT NULL,
  devicetoken character varying NOT NULL,
  deviceid character varying,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version bigint,
  CONSTRAINT pk_eg_userfcmdevice_pkey PRIMARY KEY (id),
  CONSTRAINT unq_eg_user_devicetoken UNIQUE (userid, devicetoken)
);
DROP TABLE IF EXISTS egevntnotification_schedule;
DROP SEQUENCE IF EXISTS seq_egevntnotification_schedule;

DROP TABLE IF EXISTS egevntnotification_drafts;
DROP SEQUENCE IF EXISTS seq_egeventnotification_drafts;

DROP TABLE IF EXISTS egevntnotification_drafttype;
DROP SEQUENCE IF EXISTS seq_egevntnotification_drafttype;

CREATE SEQUENCE seq_egevntnotification_drafttype; 
CREATE TABLE egevntnotification_drafttype
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_drafttype_pkey PRIMARY KEY (id)
);
DROP TABLE IF EXISTS egevntnotification_schedulerepeat;
DROP SEQUENCE IF EXISTS seq_egevntnotification_schedulerepeat;

CREATE SEQUENCE seq_egevntnotification_schedulerepeat; 
CREATE TABLE egevntnotification_schedulerepeat
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_schedulerepeat_pkey PRIMARY KEY (id)
);
CREATE SEQUENCE seq_egevntnotification_schedule;
CREATE TABLE egevntnotification_schedule
(
  id bigint NOT NULL,
  templatename character varying(100) NOT NULL,
  status character varying(100) NOT NULL,
  start_date timestamp with time zone NOT NULL,
  message_template character varying(500) NOT NULL,
  drafttype bigint,
  schedulerepeat bigint,
  module bigint,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_schedule_pkey PRIMARY KEY (id),
  CONSTRAINT fk_drafttype_schedule_ref FOREIGN KEY (drafttype)
      REFERENCES egevntnotification_drafttype (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_schedulerepeat_schedule_ref FOREIGN KEY (schedulerepeat)
      REFERENCES egevntnotification_schedulerepeat (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

DROP TABLE IF EXISTS egevntnotification_category_parameters;
DROP SEQUENCE IF EXISTS seq_egevntnotification_category_parameters;
DROP TABLE IF EXISTS egevntnotification_module_category;
DROP SEQUENCE IF EXISTS seq_egevntnotification_module_category;
DROP TABLE IF EXISTS egevntnotification_template_module;
DROP SEQUENCE IF EXISTS seq_egevntnotification_template_module;

CREATE SEQUENCE seq_egevntnotification_template_module; 
CREATE TABLE egevntnotification_template_module
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  active boolean,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_module_pkey PRIMARY KEY (id),
  CONSTRAINT eg_evntnotification_module_unq UNIQUE (name)
);

CREATE SEQUENCE seq_egevntnotification_module_category; 
CREATE TABLE egevntnotification_module_category
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  active boolean,
  module bigint,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_category_pkey PRIMARY KEY (id),
  CONSTRAINT fk_module_category_ref FOREIGN KEY (module)
      REFERENCES egevntnotification_template_module (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT eg_evntnotification_category_unq UNIQUE (name)
);

CREATE SEQUENCE seq_egevntnotification_category_parameters; 
CREATE TABLE egevntnotification_category_parameters
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  active boolean,
  category bigint,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_parameters_pkey PRIMARY KEY (id),
  CONSTRAINT fk_category_parameters_ref FOREIGN KEY (category)
      REFERENCES egevntnotification_module_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE seq_egeventnotification_drafts; 
CREATE TABLE egevntnotification_drafts
(
  id bigint NOT NULL,
  name character varying(100) NOT NULL,
  module bigint,
  category bigint,
  notification_message text,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version bigint DEFAULT 0,
  drafttype bigint,
  CONSTRAINT egevntnotification_drafts_pkey PRIMARY KEY (id),
  CONSTRAINT fk_category_drafts_ref FOREIGN KEY (category)
      REFERENCES egevntnotification_module_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_drafttype_draft_ref FOREIGN KEY (drafttype)
      REFERENCES egevntnotification_drafttype (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_module_drafts_ref FOREIGN KEY (module)
      REFERENCES egevntnotification_template_module (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

DROP TABLE IF EXISTS egevntnotification_schedule_log;
DROP SEQUENCE IF EXISTS seq_egevntnotification_schedule_log;

CREATE SEQUENCE seq_egevntnotification_schedule_log;
CREATE TABLE egevntnotification_schedule_log
(
  id bigint NOT NULL,
  filestore bigint NOT NULL,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version bigint DEFAULT 0,
  CONSTRAINT egevntnotification_schedule_log_pkey PRIMARY KEY (id)
);
